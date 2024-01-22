package it.unisa.c10.beehAIve.service.gestioneArnie;

import it.unisa.c10.beehAIve.persistence.dao.HiveDAO;
import it.unisa.c10.beehAIve.persistence.dao.OperationDAO;
import it.unisa.c10.beehAIve.persistence.entities.Hive;
import it.unisa.c10.beehAIve.persistence.entities.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OperationService {
  private final OperationDAO operationDAO;
  private final HiveDAO hiveDAO;
  @Autowired
  public OperationService(OperationDAO operationDAO, HiveDAO hiveDAO) {
    this.operationDAO = operationDAO;
    this.hiveDAO = hiveDAO;
  }

  /**
   * Permette di pianificare un'operazione
   * @param operationName Il nome dell'operazione.
   * @param operationType Il tipo dell'operazione.
   * @param operationStatus Lo Status dell'operazione.
   * @param operationDate La data in cui &eacute; prevista l'operazione.
   * @param notes Eventuali note sull'operazione.
   * @param hiveId L'id dell'arnia su cui l'apicoltore deve effettuare l'operazione.
   * @param beekeeperEmail L'email dell'apicoltore che vuole pianificare l'operazione.
   */
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

  /**
   * Prende un'operazione dal database
   * @param id L'id dell'operazione che vogliamo recuperare.
   * @return L'{@code Operation} che corrisponde a quell'id.
   */
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

  /**
   * Permette di modificare un'operazione pianificata.
   * @param id L'id dell'operazione che vogliamo modificare
   * @param operationName Il nuovo nome dell'operazione.
   * @param operationType Il nuovo tipo dell'operazione.
   * @param operationStatus Il nuovo Status dell'operazione.
   * @param operationDate La nuova data in cui &eagrave; prevista l'operazione.
   * @param notes Eventuali note sull'operazione.
   * @param hiveId L'id dell'arnia su cui &egrave; prevista l'operazione.
   * @param beekeeperEmail L'email dell'apicoltore che vuole modificare l'operazione.
   */
  public void modifyScheduledOperation(int id, String operationName, String operationType, String operationStatus,
                                           LocalDateTime operationDate, String notes, int hiveId, String beekeeperEmail) {
    // Creazione dell'intervento
    Operation operation = this.createOperation(operationName, operationType, operationStatus, operationDate, notes,
      hiveId, beekeeperEmail);
    operation.setId(id);

    // Aggiornamento nel DB
    operationDAO.save(operation);
  }

  /**
   * Cancella un'operazione pianificata.
   * @param id L'id dell'operazione che vogliamo cancellare.
   */
  public void cancelScheduledOperation(int id) {
    // Ottenimento dell'intervento come oggetto Optional successivamente eliminato nel DB
    Optional<Operation> optionalOperation = operationDAO.findById(id);
    operationDAO.deleteById(id);

    // Modifica dell'attributo "uncompletedOperations" dell'arnia (se serve) se l'oggetto Optional non è null
    if(optionalOperation.isPresent()) {
      controlOperationsOnHive(optionalOperation.get().getHiveId());
    }
  }


  /**
   * Inverte lo Status di un'operazione da Not Completed a Completed o viceversa.
   * @param id L'id dell'operazione di cui vogliamo invertire lo status.
   */
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

  /**
   * Prende tutte le operazioni di un'arnia.
   * @param hiveId L'id dell'arnia di cui vogliamo le operazioni.
   * @return Una lista di tutte le {@code Operation} dell'arnia.
   */
  public List<Operation> viewHiveOperations(int hiveId) {
    return operationDAO.findAllByHiveId(hiveId);
  }

  /**
   * Prende tutte le operazioni non completate di un'arnia.
   * @param hiveId L'id dell'arnia di cui vogliamo le operazioni.
   * @return Una lista di tutte le {@code Operation} non completate dell'arnia.
   */
  public List<Operation> getHiveUncompletedOperations (int hiveId) {
    return operationDAO.findAllByOperationStatusAndHiveIdOrderByOperationDateAsc("Not completed", hiveId);
  }

  /**
   * Prende tutte le operazioni completate di un'arnia
   * @param hiveId L'id dell'arnia di cui vogliamo le operazioni
   * @return Una lista di tutte le {@code Operation} completate dell'arnia
   */
  public List<Operation> getHiveCompletedOperations (int hiveId) {
    return operationDAO.findAllByOperationStatusAndHiveIdOrderByOperationDateDesc("Completed", hiveId);
  }

  /**
   * Prende tute le operazioni di un apicoltore
   * @param beekeeperEmail L'email dell'apicoltore di cui vogliamo le operazioni
   * @return Una lista di tutte le {@code Operation} dell'apicoltore.
   */
  public List<Operation> viewAllBeekeeperOperations (String beekeeperEmail) {
    return operationDAO.findAllByBeekeeperEmail(beekeeperEmail);
  }

  /**
   * Converte una lista di operazioni in un formato JSON leggibile al calendario
   * @param operations Le operazioni che vogliamo convertire
   * @return La stringa JSON che rappresenta l'oggetto da convertire in un calendario.
   */
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

  /**
   * Converte un'operazione in formato JSON
   * @param op L'operazione che vogliamo convertire
   * @return La string JSON che rappresenta l'oggetto operazione.
   */
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

    return result;

  }

  private void notifyImminentOperation(int id) {
    // Ottenimento dell'intervento come oggetto Optional
    Optional<Operation> optionalOperation = operationDAO.findById(id);

    // Ottenimento di Operation se l'oggetto Optional non è null
    if(optionalOperation.isPresent()) {
      // Modifica dell'attributo "uncompletedOperations" dell'arnia se l'oggetto Optional non è null
      controlOperationsOnHive(optionalOperation.get().getHiveId());
    }
  }

  private void controlOperationsOnHive(int hiveId) {

    // Modifica dell'attributo "uncompletedOperations" dell'arnia specificata dall'id a true/false
    // (in base alla presenza di interventi da svolgere su essa)

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


}