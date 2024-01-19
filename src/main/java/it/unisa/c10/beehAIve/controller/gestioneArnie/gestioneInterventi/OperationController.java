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
import java.time.format.DateTimeParseException;

@Controller
public class OperationController {
  private OperationService operationService;
  private DashboardService dashboardService;

  @Autowired
  public OperationController(OperationService operationService, DashboardService dashboardService) {
    this.operationService = operationService;
    this.dashboardService = dashboardService;
  }

  @GetMapping("/operations-hive")
  public String showPlanningOperationForm(Model model){
    return "hive/operations-hive";
  }

  private boolean regexName(String name) {
    String regex = "^[a-zA-Z0-9\\s\\-_()’”]+$";
    return name.matches(regex);
  }

  private boolean formatType(String type){
    return type.equals("Medical Inspection") || type.equals("Medical Treatment") ||
      type.equals("Check Population") || type.equals("Extraction") || type.equals("Vet Visit") ||
      type.equals("Feeding") || type.equals("Transfer") || type.equals("Maintenance");
  }

  private boolean formatStatus(String operationStatus) {
    return operationStatus.equals("Completed") || operationStatus.equals("Not Completed");
  }

  private LocalDateTime formatDate(String operationDateString) {
    try {
      LocalDateTime operationDate;
      // Definizione del formato della data
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");

      // Conversione
      operationDate = LocalDateTime.parse(operationDateString, formatter);
      return operationDate;
    } catch (DateTimeParseException e) {
      return null;
    }
  }

  private boolean isValidDate(LocalDateTime operationDate) {
    LocalDateTime today = LocalDateTime.now();
    return !operationDate.isBefore(today);
  }

  private boolean regexNotes(String notes) {
    String regex = "^[a-zA-Z0-9\\s\\-_()’”.,?!:]+$";
    return notes.matches(regex);
  }

  // Pianificazione Intervento
  @GetMapping("/add_operation-form")
  public String planningOperation(@RequestParam String operationName, @RequestParam String operationDate,
                                  @RequestParam String operationType, @RequestParam String operationNotes,
                                  @RequestParam String hiveId, Model model, HttpSession session) {
    System.out.println("prova provetta");
    if(hiveId == null)
      System.out.println("hive è null");
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

    // Controllo sul formato e sulla correttezza della data dell'operazione
    LocalDateTime operationDate1 = formatDate(operationDate);
    if(operationDate1 == null) {
      return "hive/errors/error500";
    }
    if(!isValidDate(operationDate1)) {
      model.addAttribute("error", "Invalid operation date.");
      return "hive/operations-hive";
    }

    // Controllo sul formato delle note dell'operazione
    if(!regexNotes(operationNotes)) {
      model.addAttribute("error", "Invalid operation notes.");
      return "hive/operations-hive";
    }

    // Controllo sulla lunghezza delle note dell'operazione
    if(operationNotes.length() > 300) {
      model.addAttribute("error", "Operation notes too long.");
      return "hive/operations-hive";
    }

    // Assegnazione del beekeeper conservato nella sessione (servirà per l'email)
    Beekeeper beekeeper = (Beekeeper) session.getAttribute("beekeeper");

    // Controllo sull'id dell'arnia
    int hiveId1;
    if (!hiveId.matches("//d+") && Integer.parseInt(hiveId) <= 0) {
      return "errors/error500";
    }
    hiveId1 = Integer.parseInt(hiveId);

    // Salvataggio nel DB dell'operazione usando il service (se i controlli sono andati a buon fine)
    operationService.planningOperation(operationName, operationType, "Not complete", operationDate1,
      operationNotes, hiveId1, beekeeper.getEmail());

    return "hive/operations-hive";
  }

  // Modifica Intervento
  @GetMapping("/modify-operation-form")
  public String modifyOperation(@RequestParam String id, @RequestParam String operationName,
                                @RequestParam String operationType, @RequestParam String operationStatus,
                                @RequestParam String operationDateString, @RequestParam String operationNotes,
                                @RequestParam String hiveId, Model model, HttpSession session) {
    // Controllo sull'id dell'operazione
    int id1;
    if (!id.matches("//d+") && Integer.parseInt(id) <= 0) {
      return "errors/error500";
    }
    id1 = Integer.parseInt(id);


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

    // Controllo sul formato e sulla correttezza della data dell'operazione
    LocalDateTime operationDate = formatDate(operationDateString);
    if(operationDate == null) {
      return "errors/error500";
    }
    if(!isValidDate(operationDate)) {
      model.addAttribute("error", "Invalid operation date.");
      return "hive/operations-hive";
    }

    // Controllo sul formato delle note dell'operazione
    if(!regexNotes(operationNotes)) {
      model.addAttribute("error", "Invalid operation notes.");
      return "hive/operations-hive";
    }

    // Controllo sulla lunghezza delle note dell'operazione
    if(operationNotes.length() > 300) {
      model.addAttribute("error", "Operation notes too long.");
      return "hive/operations-hive";
    }

    // Assegnazione del beekeeper conservato nella sessione (servirà per l'email)
    Beekeeper beekeeper = (Beekeeper) session.getAttribute("beekeeper");

    // Controllo sull'id dell'arnia
    int hiveId1;
    if (!hiveId.matches("//d+") && Integer.parseInt(hiveId) <= 0) {
      return "errors/error500";
    }
    hiveId1 = Integer.parseInt(hiveId);

    // Aggiornamento nel DB dell'operazione usando il service (se i controlli sono andati a buon fine)
    operationService.modifyScheduledOperation(id1, operationName, operationType, operationStatus, operationDate, operationNotes,
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

  @GetMapping("/redirect-form")
  public String redirectToOperationPanel(@RequestParam String hiveId, Model model) {
    System.out.println("evvai sono qui");
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
}