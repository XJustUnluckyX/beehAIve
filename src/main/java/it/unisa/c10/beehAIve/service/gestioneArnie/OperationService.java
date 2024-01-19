package it.unisa.c10.beehAIve.service.gestioneArnie;

import it.unisa.c10.beehAIve.persistence.dao.HiveDAO;
import it.unisa.c10.beehAIve.persistence.dao.OperationDAO;
import it.unisa.c10.beehAIve.persistence.dao.ProductionDAO;
import it.unisa.c10.beehAIve.persistence.entities.Hive;
import it.unisa.c10.beehAIve.persistence.entities.Operation;
import it.unisa.c10.beehAIve.persistence.entities.Production;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OperationService {
  private final OperationDAO operationDAO;
  private final ProductionDAO productionDAO;
  private final HiveDAO hiveDAO;
  @Autowired
  public OperationService(OperationDAO operationDAO, ProductionDAO productionDAO, HiveDAO hiveDAO) {
    this.operationDAO = operationDAO;
    this.productionDAO = productionDAO;
    this.hiveDAO = hiveDAO;
  }

  /*
  * Modifica dell'attributo "uncompletedOperations" dell'arnia specificata dall'id a true/false
  * (in base alla presenza di interventi da svolgere su essa)
  * */
  private void controlOperationsOnHive(int hiveId) {
    // Calcolo del numero di interventi non completati sull'arnia
    int opNotComp = operationDAO.countByHiveIdAndOperationsNotCompleted(hiveId);

    // Ottenimento dell'arnia di cui cambiare l'attributo
    Hive hive = hiveDAO.findById(hiveId).get();

    // Modifica l'attributo se non ci sono altri interventi da completare
    if(opNotComp == 0){
      hive.setUncompletedOperations(false);
    }
    else if(opNotComp > 0) {
      hive.setUncompletedOperations(true);
    }

    hiveDAO.save(hive);
  }

  // Creazione dell'oggetto Operation
  private Operation createOperation(String operationName, String operationType, String operationStatus,
                                    LocalDateTime operationDate, String notes, int hiveId, String beekeeperEmail) {
    // Creazione dell'intervento
    Operation operation = new Operation();
    operation.setOperationName(operationName);
    operation.setOperationType(operationType);
    operation.setOperationStatus(operationStatus);
    operation.setOperationDate(operationDate);
    operation.setNotes(notes);
    operation.setHiveId(hiveId);
    operation.setBeekeeperEmail(beekeeperEmail);

    return operation;
  }

  // Pianificare di un intervento
  public void planningOperation(String operationName, String operationType, String operationStatus,
                                    LocalDateTime operationDate, String notes, int hiveId, String beekeeperEmail) {
    // Creazione dell'intervento
    Operation operation = this.createOperation(operationName, operationType, operationStatus, operationDate, notes,
        hiveId, beekeeperEmail);

    // Salvataggio l'intervento nel DB
    operation = operationDAO.save(operation);

    // Notifica l'intervento
    this.notifyImminentOperation(operation.getId());
  }

  // Modificare di un intervento pianificato
  public void modifyScheduledOperation(int id, String operationName, String operationType, String operationStatus,
                                           LocalDateTime operationDate, String notes, int hiveId, String beekeeperEmail) {
    // Creazione dell'intervento
    Operation operation = this.createOperation(operationName, operationType, operationStatus, operationDate, notes,
      hiveId, beekeeperEmail);
    operation.setId(id);

    // Aggiornamento nel DB
    operationDAO.save(operation);
  }

  // Annullare di un intervento pianificato
  public void cancelScheduledOperation(int id) {
    // Ottenimento dell'intervento come oggetto Optional successivamente eliminato nel DB
    Optional<Operation> optionalOperation = operationDAO.findById(id);
    operationDAO.deleteById(id);

    // Modifica dell'attributo "uncompletedOperations" dell'arnia (se serve) se l'oggetto Optional non è null
    if(optionalOperation.isPresent()) {
      controlOperationsOnHive(optionalOperation.get().getHiveId());
    }
  }

  // Ottenere un intervento dal database
  public Operation retrieveOperationFromDB(int id) {
    // Ottenimento dell'intervento come oggetto Optional
    Optional<Operation> optionalOperation = operationDAO.findById(id);

    // Restituzione dell'oggetto Operation se l'oggetto Optional non è null
    if(optionalOperation.isPresent()) {
      return optionalOperation.get();
    } else {
      return null;
    }
  }

  // Notificare di un intervento imminente
  public void notifyImminentOperation(int id) {
    // Ottenimento dell'intervento come oggetto Optional
    Optional<Operation> optionalOperation = operationDAO.findById(id);

    // Ottenimento di Operation se l'oggetto Optional non è null
    if(optionalOperation.isPresent()) {
      // Modifica dell'attributo "uncompletedOperations" dell'arnia se l'oggetto Optional non è null
      controlOperationsOnHive(optionalOperation.get().getHiveId());
    }
  }

  // Visualizzare dello storico di tutti gli interventi
  public List<Operation> viewOperationHistory() {
    // Ottenimento della lista poi restituita
    return operationDAO.findAllByOrderByOperationDateAsc();
  }

  public List<Operation> getHiveUncompletedOperations (int hiveId) {
    return operationDAO.findAllByOperationStatusAndHiveIdOrderByOperationDateAsc("Not completed", hiveId);
  }

  public List<Operation> getHiveCompletedOperations (int hiveId) {
    return operationDAO.findAllByOperationStatusAndHiveIdOrderByOperationDateDesc("Completed", hiveId);
  }

  public List<Operation> viewHiveOperations(int hiveId) {
    return operationDAO.findAllByHiveId(hiveId);
  }

  public List<Operation> viewAllBeekeeperOperations (String beekeeperEmail) {
    return operationDAO.findAllByBeekeeperEmail(beekeeperEmail);
  }

  // Impostare di un intervento come "Completed" o "Not completed"
  public void changeOperationStatus(int id) {
    // Ottenimento dell'intervento come oggetto Optional
    Optional<Operation> optionalOperation = operationDAO.findById(id);

    // Ottenimento di Operation se l'oggetto Optional restituito non è null
    if(optionalOperation.isPresent()) {
      Operation operation = optionalOperation.get();
      // Modifica dello status (viene invertito)
      if(operation.getOperationStatus().equals("Completed")){
        operation.setOperationStatus("Not completed");
      }
      else if(operation.getOperationStatus().equals("Not completed")) {
        operation.setOperationStatus("Completed");
      }


      // Aggiornamento nel DB
      operationDAO.save(operation);
      // Modifica dell'attributo "uncompletedOperations" dell'arnia (se serve)
      controlOperationsOnHive(operation.getHiveId());
    }
  }

  // Registrare di una produzione
  public void recordProduction(String product, double weight, String notes, LocalDate registrationDate, int hiveId,
                               String beekeeperEmail) {
    // Creazione della produzione
    Production production = new Production();
    production.setProduct(product);
    production.setWeight(weight);
    production.setNotes(notes);
    production.setRegistrationDate(registrationDate);
    production.setHiveId(hiveId);
    production.setBeekeeperEmail(beekeeperEmail);

    // Salvataggio nel DB
    productionDAO.save(production);
  }

  // Visualizzare dello storico delle produzioni
  public List<Production> viewProductionHistory() {
    // Ottenimento della lista poi restituita
    return productionDAO.findAllByOrderByRegistrationDateAsc();
  }

  public String convertOperationToCalendar(List<Operation> operations) {

    if (operations.isEmpty())
      return "[]";

    String result="[";

    for (Operation op : operations) {

      String hiveName = hiveDAO.findByIdSelectNickname(op.getHiveId());
      result += "{\"title\" : \"(" + hiveName + ") " + op.getOperationName() +"\", \"start\" : \"" + op.getOperationDate().toString() + "\", \"allDay\": false},";
    }

    result+="]";

    // Rimuove l'ultima virgola dalla stringa per evitare errori di parsing JSON
    StringBuilder sb = new StringBuilder(result);
    sb.deleteCharAt(result.length()-2);
    result = sb.toString();

    return result;

  }

  public String convertOperationToString (Operation op) {

    String date = op.getOperationDate().toString();
    String[] dateTokens = date.split("T");

    String result = "{ ";

    result += "\"status\" : \"" + op.getOperationStatus() + "\", ";
    result += "\"name\" : \"" + op.getOperationName() + "\", ";
    result += "\"date\" : \"" + dateTokens[0] + "\", ";
    result += "\"hour\" : \"" + dateTokens[1] + "\", ";
    result += "\"type\" : \"" + op.getOperationType() + "\", ";
    result += "\"notes\" : \"" + op.getNotes() + "\", ";
    result += "\"hiveId\" : \"" + op.getHiveId() + "\", ";
    result += "\"id\" : \"" + op.getId() + "\"";

    result += "}";

    System.out.println(result);

    return result;

  }

}