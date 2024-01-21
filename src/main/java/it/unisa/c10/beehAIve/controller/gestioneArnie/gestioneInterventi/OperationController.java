package it.unisa.c10.beehAIve.controller.gestioneArnie.gestioneInterventi;

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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Controller
public class OperationController {
  private OperationService operationService;
  private DashboardService dashboardService;

  @Autowired
  public OperationController(OperationService operationService, DashboardService dashboardService) {
    this.operationService = operationService;
    this.dashboardService = dashboardService;
  }

  // Pulsante nel pannello per mandare alla pagina dell'arnia sugli interventi
  @GetMapping("/show_operations")
  public String redirectToOperationPanel(@RequestParam String hiveId, Model model,
                                         HttpSession session) {
    // Controllo sull'ID dell'arnia
    int hiveId1;
    if (!hiveId.matches("^[\\d]+$") && Integer.parseInt(hiveId) <= 0) {
      return "error/500";
    }
    hiveId1 = Integer.parseInt(hiveId);

    // Ricavo dell'arnia dall'ID ottenuto
    Hive hive = dashboardService.getHive(hiveId1);
    model.addAttribute("hive", hive);

    // Aggiunta delle operazioni da svolgere
    List<Operation> toComplete = operationService.getHiveUncompletedOperations(hiveId1);
    model.addAttribute("toComplete", toComplete);

    // Aggiunta delle operazioni passate
    List<Operation> completed = operationService.getHiveCompletedOperations(hiveId1);
    model.addAttribute("completed", completed);

    Beekeeper beekeeper = (Beekeeper) session.getAttribute("beekeeper");

    // Controllo sulla coerenza tra ID dell'arnia ed email dell'apicoltore
    if(isNotConsistentBetweenHiveIdAndBeekeeperEmail(hiveId1, beekeeper.getEmail())) {
      return "error/500";
    }

    return "hive/operations-hive";
  }

  // Visualizzazione del form
  @GetMapping("/operations-hive")
  public String showPlanningOperationForm(Model model){
    return "hive/operations-hive";
  }

  // Pianificazione Intervento
  @GetMapping("/add_operation-form")
  public String planningOperation(@RequestParam String operationName,
                                  @RequestParam String operationDate,
                                  @RequestParam String operationHour,
                                  @RequestParam String operationType,
                                  @RequestParam String noteOperation, @RequestParam String hiveId,
                                  Model model, HttpSession session) {
    // Controllo sull'ID dell'arnia
    int hiveId1;
    if (!hiveId.matches("^[\\d]+$") && Integer.parseInt(hiveId) <= 0) {
      return "error/500";
    }
    hiveId1 = Integer.parseInt(hiveId);
    Hive hive = dashboardService.getHive(hiveId1);
    model.addAttribute("hive", hive);

    // Controllo sul formato del nome dell'intervento
    if(isFormatNameInvalid(operationName)) {
      model.addAttribute("error", "Invalid operation name.");
      return "hive/operations-hive";
    }
    // Controllo sulla lunghezza del nome dell'intervento
    if(operationName.length() < 2) {
      model.addAttribute("error", "Operation name too short.");
      return "hive/operations-hive";
    }
    if(operationName.length() > 70) {
      model.addAttribute("error", "Operation name too long.");
      return "hive/operations-hive";
    }

    // Controllo sul formato delle note dell'intervento
    if(isFormatNotesInvalid(noteOperation)) {
      model.addAttribute("error", "Notes must contain zero or more " +
          "characters, which can be uppercase and lowercase letters, digits, spaces and special" +
          "symbols ( -_()'\",.?!: )");
      return "hive/operations-hive";
    }
    // Controllo sulla lunghezza delle note dell'intervento
    if(noteOperation.length() > 300) {
      model.addAttribute("error", "Operation notes too long.");
      return "hive/operations-hive";
    }

    // Controllo sul formato della data dell'intervento
    LocalDateTime operationDate1 = formatDate(operationDate, operationHour);
    if(isDateInvalid(operationDate1)) {
      model.addAttribute("error", "Invalid operation date.");
      return "hive/operations-hive";
    }

    // Controllo sul tipo dell'intervento
    if(isFormatTypeInvalid(operationType)) {
      model.addAttribute("error", "Invalid operation type.");
      return "hive/operations-hive";
    }

    // Controllo sulla coerenza tra ID dell'arnia ed email dell'apicoltore
    Beekeeper beekeeper = (Beekeeper) session.getAttribute("beekeeper");
    if(isNotConsistentBetweenHiveIdAndBeekeeperEmail(hiveId1, beekeeper.getEmail())) {
      return "error/500";
    }

    // Aggiunta delle operazioni da svolgere
    List<Operation> toComplete = operationService.getHiveUncompletedOperations(hiveId1);
    model.addAttribute("toComplete", toComplete);

    // Aggiunta delle operazioni passate
    List<Operation> completed = operationService.getHiveCompletedOperations(hiveId1);
    model.addAttribute("completed", completed);

    // Salvataggio nel DB dell'intervento usando il service (se i controlli sono andati a buon fine)
    operationService.planningOperation(operationName, operationType, "Not completed",
        operationDate1, noteOperation, hiveId1, hive.getBeekeeperEmail());

    return "redirect:/show_operations?hiveId=" + hiveId;
  }

  // Modifica Intervento
  @GetMapping("/modify-operation-form")
  public String modifyOperation(@RequestParam String operationId, @RequestParam String operationName,
                                @RequestParam String operationType, @RequestParam String operationStatus,
                                @RequestParam String operationDate, @RequestParam String operationHour,
                                @RequestParam String operationNotes, @RequestParam String hiveId,
                                Model model, HttpSession session) {
    // Controllo sull'ID dell'arnia
    int hiveId1;
    if (!hiveId.matches("^[\\d]+$") && Integer.parseInt(hiveId) <= 0) {
      return "error/500";
    }
    hiveId1 = Integer.parseInt(hiveId);
    Hive hive = dashboardService.getHive(hiveId1);
    model.addAttribute("hive", hive);

    // Controllo sull'ID dell'intervento
    int operationId1;
    if (!operationId.matches("^[\\d]+$") && Integer.parseInt(operationId) <= 0) {
      return "error/500";
    }
    operationId1 = Integer.parseInt(hiveId);

    // Controllo sul formato del nome dell'intervento
    if(isFormatNameInvalid(operationName)) {
      model.addAttribute("error", "Invalid operation name.");
      return "hive/operations-hive";
    }
    // Controllo sulla lunghezza del nome dell'intervento
    if(operationName.length() < 2) {
      model.addAttribute("error", "Operation name too short.");
      return "hive/operations-hive";
    }
    if(operationName.length() > 70) {
      model.addAttribute("error", "Operation name too long.");
      return "hive/operations-hive";
    }

    // Controllo sul formato delle note dell'intervento
    if(isFormatNotesInvalid(operationNotes)) {
      model.addAttribute("error", "Operation notes must contain zero or " +
          "more characters, which can be uppercase and lowercase letters, digits, spaces and " +
          "special symbols ( -_()'\",.?!: )");
      return "hive/operations-hive";
    }
    // Controllo sulla lunghezza delle note dell'intervento
    if(operationNotes.length() > 300) {
      model.addAttribute("error", "Operation notes too long.");
      return "hive/operations-hive";
    }

    // Conversione sul formato della data dell'intervento
    LocalDateTime operationDate1 = formatDate(operationDate, operationHour);
    if(isDateInvalid(operationDate1)) {
      model.addAttribute("error", "Invalid operation date.");
      return "hive/operations-hive";
    }

    // Controllo sul tipo dell'intervento
    if(isFormatTypeInvalid(operationType)) {
      model.addAttribute("error", "Invalid operation type.");
      return "hive/operations-hive";
    }

    // Controllo sul formato dello stato dell'intervento
    if(isFormatStatusInvalid(operationStatus)) {
      model.addAttribute("error", "Invalid operation status.");
      return "hive/operations-hive";
    }

    // Controllo sulla coerenza tra ID dell'arnia ed email dell'apicoltore
    Beekeeper beekeeper = (Beekeeper) session.getAttribute("beekeeper");
    if(isNotConsistentBetweenHiveIdAndBeekeeperEmail(hiveId1, beekeeper.getEmail())) {
      return "error/500";
    }

    // Controllo sulla coerenza tra ID dell'intervento ed email dell'apicoltore
    if(isNotConsistentBetweenOperationIdAndBeekeeperEmail(operationId1, beekeeper.getEmail())) {
      return "error/500";
    }

    // Salvataggio dell'intervento nel database
    operationService.modifyScheduledOperation(operationId1, operationName, operationType, operationStatus, operationDate1, operationNotes,
      hiveId1, beekeeper.getEmail());

    return "hive/operations-hive";
  }

  // Annullamento Intervento
  @GetMapping("/cancel-operation-form")
  public String cancelOperation(@RequestParam String operationId, @RequestParam String hiveId, HttpSession session, Model model) {
    // Controllo sull'ID dell'intervento
    int operationId1;
    if (!operationId.matches("^[\\d]+$") && Integer.parseInt(operationId) <= 0) {
      return "error/500";
    }
    operationId1 = Integer.parseInt(operationId);

    // Controllo sull'ID dell'arnia
    int hiveId1;
    if (!hiveId.matches("^[\\d]+$") && Integer.parseInt(hiveId) <= 0) {
      return "error/500";
    }
    hiveId1 = Integer.parseInt(hiveId);
    Hive hive = dashboardService.getHive(hiveId1);
    model.addAttribute("hive", hive);

    // Controllo sulla coerenza tra ID dell'arnia ed email dell'apicoltore
    Beekeeper beekeeper = (Beekeeper) session.getAttribute("beekeeper");
    if(isNotConsistentBetweenHiveIdAndBeekeeperEmail(hiveId1, beekeeper.getEmail())) {
      return "error/500";
    }
    // Controllo sulla coerenza tra ID dell'intervento ed email dell'apicoltore
    if(isNotConsistentBetweenOperationIdAndBeekeeperEmail(operationId1, beekeeper.getEmail())) {
      return "error/500";
    }

    // Eliminazione dell'intervento dal database
    operationService.cancelScheduledOperation(operationId1);

    return "hive/operations-hive";
  }

  // Visualizzazione Intervento
  @GetMapping("/visualize-operation-form")
  public String visualizeOperation(@RequestParam String id, @RequestParam String hiveId, HttpSession session, Model model) {
    // Controllo sull'ID dell'intervento
    int operationId1;
    if (!id.matches("^[\\d]+$") && Integer.parseInt(id) <= 0) {
      return "error/500";
    }
    operationId1 = Integer.parseInt(id);

    // Controllo sull'ID dell'arnia
    int hiveId1;
    if (!hiveId.matches("^[\\d]+$") && Integer.parseInt(hiveId) <= 0) {
      return "error/500";
    }
    hiveId1 = Integer.parseInt(hiveId);
    Hive hive = dashboardService.getHive(hiveId1);
    model.addAttribute("hive", hive);

    // Controllo sulla coerenza tra ID dell'arnia ed email dell'apicoltore
    Beekeeper beekeeper = (Beekeeper) session.getAttribute("beekeeper");
    if(isNotConsistentBetweenHiveIdAndBeekeeperEmail(hiveId1, beekeeper.getEmail())) {
      return "error/500";
    }

    // Controllo sulla coerenza tra ID dell'intervento ed email dell'apicoltore
    if(isNotConsistentBetweenOperationIdAndBeekeeperEmail(operationId1, beekeeper.getEmail())) {
      return "error/500";
    }

    // Ottenimento dell'intervento dal database con tutte le sue informazioni
    Operation operation = operationService.retrieveOperationFromDB(operationId1);
    model.addAttribute("operation", operation);

    return "hive/operations-hive";
  }

  // Impostazione Intervento come completato
  @GetMapping("/change-operation-status-form")
  public String changeOperationStatus(@RequestParam String operationId, @RequestParam String hiveId, HttpSession session, Model model) {
    // Controllo sull'ID dell'intervento
    int operationId1;
    if (!operationId.matches("^[\\d]+$") && Integer.parseInt(operationId) <= 0) {
      return "error/500";
    }
    operationId1 = Integer.parseInt(operationId);

    // Controllo sull'ID dell'arnia
    int hiveId1;
    if (!hiveId.matches("^[\\d]+$") && Integer.parseInt(hiveId) <= 0) {
      return "error/500";
    }
    hiveId1 = Integer.parseInt(hiveId);
    Hive hive = dashboardService.getHive(hiveId1);
    model.addAttribute("hive", hive);

    // Controllo sulla coerenza tra ID dell'arnia ed email dell'apicoltore
    Beekeeper beekeeper = (Beekeeper) session.getAttribute("beekeeper");
    if(isNotConsistentBetweenHiveIdAndBeekeeperEmail(hiveId1, beekeeper.getEmail())) {
      return "error/500";
    }
    // Controllo sulla coerenza tra ID dell'intervento ed email dell'apicoltore
    if(isNotConsistentBetweenOperationIdAndBeekeeperEmail(operationId1, beekeeper.getEmail())) {
      return "error/500";
    }

    // Modifica dello stato dell'intervento
    operationService.changeOperationStatus(operationId1);

    return "hive/operations-hive";
  }

  // Regex sul nome dell'intervento
  private boolean isFormatNameInvalid(String name) {
    String regex = "^[a-zA-Z0-9\\s\\-_()'\"]+$";
    return !name.matches(regex);
  }

  // Controllo del formato del tipo dell'intervento
  private boolean isFormatTypeInvalid(String type){
    List<String> validOperationTypes = Arrays.asList("Medical Inspection", "Medical Treatment",
        "Check Population", "Extraction", "Vet Visit", "Feeding", "Transfer", "Maintenance");
    return !validOperationTypes.contains(type);
  }

  // Controllo del formato dello status dell'intervento
  private boolean isFormatStatusInvalid(String operationStatus) {
    return !(operationStatus.equals("Completed") || operationStatus.equals("Not completed"));
  }

  // Ottenimento data dalle due stringhe
  private LocalDateTime formatDate(String dayString, String hourString) {
    DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Controllo del giorno
    LocalDate day = LocalDate.parse(dayString, formatter1);

    // Unione e conversione a LocalDateTime
    String operationDateString = dayString + " " + hourString + ":00";

    return LocalDateTime.parse(operationDateString, formatter2);
  }

  // Controllo sulla validità della data dell'intervento
  private boolean isDateInvalid(LocalDateTime operationDate) {
    LocalDateTime today = LocalDateTime.now();
    return operationDate.isBefore(today);
  }

  // Regex sulle note dell'intervento
  private boolean isFormatNotesInvalid(String notes) {
    String regex = "^[a-zA-Z0-9\\s\\-_()'\".,?!:]+$";
    return !notes.matches(regex);
  }

  // Controllo sulla coerenza tra id dell'arnia e beekeeper
  private boolean isNotConsistentBetweenHiveIdAndBeekeeperEmail(int hiveId, String beekeeperEmail) {
    // Ottengo l'arnia
    Hive hive = dashboardService.getHive(hiveId);

    return !hive.getBeekeeperEmail().equals(beekeeperEmail);
  }

  // Controllo sulla coerenza tra id dell'intervento e beekeeper
  private boolean isNotConsistentBetweenOperationIdAndBeekeeperEmail(int operationId, String beekeeperEmail) {
    // Ottengo l'arnia
    Operation operation = operationService.retrieveOperationFromDB(operationId);

    return !operation.getBeekeeperEmail().equals(beekeeperEmail);
  }
}