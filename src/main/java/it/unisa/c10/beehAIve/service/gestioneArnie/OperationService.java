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
  * Modifica dell'attributo "uncompletedOperations" dell'arnia specificata dall'id a false
  * (non c'è alcun intervento da svolgere su essa)
  * */
  private void controlOperationsOnHive(int hiveId) {
    // Controllo sulla presenza di altri interventi non completati sull'arnia
    int opNotComp = operationDAO.countByHiveIdAndOperationsNotCompleted(hiveId);

    // Modifica l'attributo se non ci sono altri interventi da completare
    if(opNotComp == 0){
      Hive hive = hiveDAO.findById(hiveId).get();
      hive.setUncompletedOperations(false);
      hiveDAO.save(hive);
    }
  }

  // Creazione dell'oggetto Operation
  private Operation createOperation(String operationName, String operationType, String operationStatus,
                                    LocalDate operationDate, String notes, int hiveId, String beekeeperEmail) {
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
  public void scheduleOperation(String operationName, String operationType, String operationStatus,
                                    LocalDate operationDate, String notes, int hiveId, String beekeeperEmail) {
    // Creazione dell'intervento
    Operation operation = this.createOperation(operationName, operationType, operationStatus, operationDate, notes,
        hiveId, beekeeperEmail);

    // Salvataggio l'intervento nel DB
    operation = operationDAO.save(operation);

    // Notifica l'intervento
    this.notifyImminentOperation(operation.getId());
  }

  // Modificare di un intervento pianificato
  public void modifyScheduledOperation(String operationName, String operationType, String operationStatus,
                                           LocalDate operationDate, String notes, int hiveId, String beekeeperEmail) {
    // Creazione dell'intervento
    Operation operation = this.createOperation(operationName, operationType, operationStatus, operationDate, notes,
      hiveId, beekeeperEmail);

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
      Operation operation = optionalOperation.get();

      // Ottenimento dell'arnia dal DB come Optional
      Optional<Hive> optionalHive = hiveDAO.findById(operation.getHiveId());
      // Modifica dell'attributo "uncompletedOperations" dell'arnia se l'oggetto Optional non è null
      if(optionalHive.isPresent()) {
        Hive hive = optionalHive.get();
        hive.setUncompletedOperations(true);
        // Aggiornamento dell'arnia nel DB
        hiveDAO.save(hive);
      }
    }
  }

  // Visualizzare dello storico di tutti gli interventi
  public List<Operation> viewOperationHistory() {
    // Ottenimento della lista poi restituita
    return operationDAO.findAllByOrderByOperationDateAsc();
  }

  public List<Operation> viewHiveOperations(int hiveId) {
    return operationDAO.findAllByHiveId(hiveId);
  }


  // Impostare di un intervento come "effettuato"
  public void markOperationAsComplete(int id) {
    // Ottenimento dell'intervento come oggetto Optional
    Optional<Operation> optionalOperation = operationDAO.findById(id);

    // Ottenimento di Operation se l'oggetto Optional restituito non è null
    if(optionalOperation.isPresent()) {
      Operation operation = optionalOperation.get();
      // Modifica dello status
      operation.setOperationStatus("Complete");
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

    String result="[";

    for (Operation op : operations) {

      result += "{\"title\" : \"" + op.getOperationName() +"\", \"start\" : \"" + op.getOperationDate().toString() + "\", \"allDay\": false},";

    }

    result+="]";

    StringBuilder sb = new StringBuilder(result);
    sb.deleteCharAt(result.length()-2);
    result = sb.toString();


    return result;

  }

}