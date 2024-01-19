package it.unisa.c10.beehAIve.controller.gestioneArnie.gestioneInterventi;

// Gestisce Creazione, Modifica, Cancellazione e Visualizzazione di una singolo intervento (CRUD)
// Gestisce inoltre il cambio di stato dell' Intervento.
// In particolare la visualizzazione del singolo verrà utilizzata per mostrare un popup con i tasti di gestione
// come Modifica o cambiare lo stato (Eseguito o meno)
// Inoltre gestisce le notifiche sugli interventi imminenti

import it.unisa.c10.beehAIve.persistence.entities.Beekeeper;
import it.unisa.c10.beehAIve.persistence.entities.Hive;
import it.unisa.c10.beehAIve.persistence.entities.Operation;
import it.unisa.c10.beehAIve.service.gestioneArnie.DashboardService;
import org.springframework.ui.Model;
import it.unisa.c10.beehAIve.service.gestioneArnie.OperationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
@SessionAttributes("beekeeper")
public class OperationController {
  private OperationService operationService;
  private DashboardService dashboardService;
  @Autowired
  public OperationController(OperationService operationService, DashboardService dashboardService) {
    this.operationService = operationService;
    this.dashboardService = dashboardService;
  }

  // Pulsante nel pannello per mandare alla pagina dell'arnia sugli interventi
  @GetMapping("/redirect-form")
  public String redirectToOperationPanel(@RequestParam String hiveId, Model model) {
    // Controllo sull'id dell'arnia
    int hiveId1;
    if (!hiveId.matches("//d+") && Integer.parseInt(hiveId) <= 0) {
      return "errors/error500";
    }
    hiveId1 = Integer.parseInt(hiveId);

    // Ricavo dell'arnia dall'id ottenuto
    Hive hive = dashboardService.getHive(hiveId1);
    model.addAttribute("hive", hive);

    return "hive/operations-hive";
  }

  // Visualizzazione del form
  @GetMapping("/operations-hive")
  public String showPlanningOperationForm(Model model){
    return "hive/operations-hive";
  }

  // Regex sul nome dell'intervento
  private boolean regexName(String name) {
    String regex = "^[a-zA-Z0-9\\s\\-_()’”]+$";
    return name.matches(regex);
  }

  // Controllo del formato del tipo dell'intervento
  private boolean formatType(String type){
    return type.equals("Medical Inspection") || type.equals("Medical Treatment") ||
      type.equals("Check Population") || type.equals("Extraction") || type.equals("Vet Visit") ||
      type.equals("Feeding") || type.equals("Transfer") || type.equals("Maintenance");
  }

  // Controllo del formato dello status dell'intervento
  private boolean formatStatus(String operationStatus) {
    return operationStatus.equals("Completed") || operationStatus.equals("Not Completed");
  }

  // Conversione della data dell'intervento
  private LocalDateTime formatDate(String operationDateString) {
    DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Conversione da stringa in data con il formato "yyyy-MM-dd'T'HH:mm"
    LocalDateTime tempOperationDate = LocalDateTime.parse(operationDateString, formatter1);
    // Conversione da data in stringa con il formato "yyyy-MM-dd HH:mm:ss" la data appena ottenuta
    String correctDate = tempOperationDate.format(formatter2);
    // Conversione da stringa in data con il formato "yyyy-MM-dd HH:mm:ss"
    LocalDateTime operationDate1 = LocalDateTime.parse(correctDate, formatter2);

    return operationDate1;
  }

  // Controllo sulla validità della data dell'intervento
  private boolean isValidDate(LocalDateTime operationDate) {
    LocalDateTime today = LocalDateTime.now();
    return !operationDate.isBefore(today);
  }

  // Regex sulle note dell'intervento
  private boolean regexNotes(String notes) {
    String regex = "^[a-zA-Z0-9\\s\\-_()’”.,?!:]+$";
    return notes.matches(regex);
  }

  // Controllo sulla coerenza tra id dell'arnia e beekeeper
  private boolean isConsistentBetweenHiveIdAndBeekeeperEmail(int hiveId, String beekeeperEmail) {
    // Ottengo l'arnia
    Hive hive = dashboardService.getHive(hiveId);

    return hive.getBeekeeperEmail().equals(beekeeperEmail);
  }


  // Pianificazione Intervento
  @GetMapping("/add_operation-form")
  public String planningOperation(@RequestParam String operationName, @RequestParam String operationDate,
                                  @RequestParam String operationType, @RequestParam String noteOperation,
                                  @RequestParam String hiveId, Model model, HttpSession session) {

    // Controllo sull'id dell'arnia, ricavo dell'arnia e inserimento nel model
    int hiveId1;
    if (!hiveId.matches("//d+") && Integer.parseInt(hiveId) <= 0) {
      return "errors/error500";
    }
    hiveId1 = Integer.parseInt(hiveId);
    Hive hive = dashboardService.getHive(hiveId1);
    model.addAttribute("hive", hive);

    // Controllo sul formato del nome dell'operazione
    if(!regexName(operationName)) {
      model.addAttribute("error", "Invalid operation name.");
      return "hive/operations-hive";
    }

    // Controllo sulla lunghezza del nome dell'operazione
    if(operationName.length() < 2) {
      model.addAttribute("error", "Operation name too short.");
      return "hive/operations-hive";
    }
    if(operationName.length() > 70) {
      model.addAttribute("error", "Operation name too long.");
      return "hive/operations-hive";
    }

    // Controllo sul formato del tipo dell'operazione
    if(!formatType(operationType)) {
      model.addAttribute("error", "Invalid operation type.");
      return "hive/operations-hive";
    }

    // Lo status dell'operazione sarà di default "Not complete" quando pianificato, non necessita di controlli

    // Conversione sul formato e controllo sulla correttezza della data dell'operazione
    LocalDateTime operationDate1 = formatDate(operationDate);
    if(!isValidDate(operationDate1)) {
      model.addAttribute("error", "Invalid operation date.");
      return "hive/operations-hive";
    }

    // Controllo sul formato e sulla lunghezza delle note dell'operazione
    if(!regexNotes(noteOperation)) {
      model.addAttribute("error", "Invalid operation notes.");
      return "hive/operations-hive";
    }
    if(noteOperation.length() > 300) {
      model.addAttribute("error", "Operation notes too long.");
      return "hive/operations-hive";
    }

    // Ottengo l'oggetto beekeeper dalla sessione
    Beekeeper beekeeper = (Beekeeper) session.getAttribute("beekeeper");

    // Controllo sulla coerenza tra id dell'arnia e email del beekeeper
    if(!isConsistentBetweenHiveIdAndBeekeeperEmail(hiveId1, beekeeper.getEmail())) {
      return "errors/error500";
    }

    // Salvataggio nel DB dell'operazione usando il service (se i controlli sono andati a buon fine)
    operationService.planningOperation(operationName, operationType, "Not complete", operationDate1,
          noteOperation, hiveId1, hive.getBeekeeperEmail());

    return "hive/operations-hive";
  }

  // Modifica Intervento
  @GetMapping("/modify-operation-form")
  public String modifyOperation(@RequestParam String operationId, @RequestParam String operationName,
                                @RequestParam String operationType, @RequestParam String operationStatus,
                                @RequestParam String operationDate, @RequestParam String operationNotes,
                                @RequestParam String hiveId, Model model, HttpSession session) {
    // Controllo sull'id dell'arnia, ricavo dell'arnia e inserimento nel model
    int hiveId1;
    if (!hiveId.matches("//d+") && Integer.parseInt(hiveId) <= 0) {
      return "errors/error500";
    }
    hiveId1 = Integer.parseInt(hiveId);
    Hive hive = dashboardService.getHive(hiveId1);
    model.addAttribute("hive", hive);

    // Controllo sul formato del nome dell'operazione
    if(!regexName(operationName)) {
      model.addAttribute("error", "Invalid operation name.");
      return "hive/operations-hive";
    }

    // Controllo sulla lunghezza del nome dell'operazione
    if(operationName.length() < 2) {
      model.addAttribute("error", "Operation name too short.");
      return "hive/operations-hive";
    }
    if(operationName.length() > 70) {
      model.addAttribute("error", "Operation name too long.");
      return "hive/operations-hive";
    }

    // Controllo sul formato del tipo dell'operazione
    if(!formatType(operationType)) {
      model.addAttribute("error", "Invalid operation type.");
      return "hive/operations-hive";
    }

    // Controllo sullo status
    if(!formatStatus(operationStatus)) {
      model.addAttribute("error", "Invalid operation status.");
      return "hive/operations-hive";
    }

    // Conversione sul formato e controllo sulla correttezza della data dell'operazione
    LocalDateTime operationDate1 = formatDate(operationDate);
    if(!isValidDate(operationDate1)) {
      model.addAttribute("error", "Invalid operation date.");
      return "hive/operations-hive";
    }

    // Controllo sul formato e sulla lunghezza delle note dell'operazione
    if(!regexNotes(operationNotes)) {
      model.addAttribute("error", "Invalid operation notes.");
      return "hive/operations-hive";
    }
    if(operationNotes.length() > 300) {
      model.addAttribute("error", "Operation notes too long.");
      return "hive/operations-hive";
    }

    // Assegnazione del beekeeper conservato nella sessione (servirà per l'email)
    Beekeeper beekeeper = (Beekeeper) session.getAttribute("beekeeper");

    // Controllo sull'id dell'operazione
    int operationId1;
    if (!operationId.matches("//d+") && Integer.parseInt(operationId) <= 0) {
      return "errors/error500";
    }
    operationId1 = Integer.parseInt(hiveId);

    // Aggiornamento nel DB dell'operazione usando il service (se i controlli sono andati a buon fine)
    operationService.modifyScheduledOperation(operationId1, operationName, operationType, operationStatus, operationDate1, operationNotes,
      hiveId1, beekeeper.getEmail());

    return "hive/operations-hive";
  }

  // Annullamento Intervento
  @GetMapping("/cancel-operation-form")
  public String cancelOperation(@RequestParam String id) {
    // Controllo sull'id dell'operazione
    int id1;
    if (!id.matches("//d+") && Integer.parseInt(id) <= 0) {
      return "errors/error500";
    }
    id1 = Integer.parseInt(id);


    // Eliminazione nel DB dell'operazione usando il service (se il controllo è andato a buon fine)
    operationService.cancelScheduledOperation(id1);

    return "hive/operations-hive";
  }

  // Visualizzazione Intervento
  @GetMapping("/visualize-operation-form")
  public String visualizeOperation(@RequestParam String id, Model model) {
    // Controllo sull'id dell'operazione
    int id1;
    if (!id.matches("//d+") && Integer.parseInt(id) <= 0) {
      return "errors/error500";
    }
    id1 = Integer.parseInt(id);

    // Ottengo dal DB l'oggetto Operation con tutte le sue informazioni
    Operation operation = operationService.retrieveOperationFromDB(id1);
    model.addAttribute("operation", operation);

    return "hive/operations-hive";
  }

  // Impostazione Intervento come completato
  @GetMapping("/mark-complete-operation-form")
  public String markAsCompleteOperation(@RequestParam String id) {
    // Controllo sull'id dell'operazione
    int id1;
    if (!id.matches("//d+") && Integer.parseInt(id) <= 0) {
      return "errors/error500";
    }
    id1 = Integer.parseInt(id);

    // Segno l'operazione come "Complete" cioè completata
    operationService.markOperationAsComplete(id1);

    return "hive/operations-hive";
  }
}
