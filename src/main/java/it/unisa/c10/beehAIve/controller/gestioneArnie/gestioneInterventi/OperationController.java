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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
                                         HttpSession session, RedirectAttributes redirectAttributes) {
    Beekeeper beekeeper = (Beekeeper) session.getAttribute("beekeeper");

    // Controllo sull'iscrizione dell'apicoltore a uno dei piani di abbonamento
    if (!beekeeper.isSubscribed()) {
      redirectAttributes.addFlashAttribute("error",
          "To create and monitor your hives, subscribe to one of our plans first!");
      return "redirect:/user-page";
    }

    // Controllo sull'id dell'arnia
    if (!hiveId.matches("^\\d+$") || Integer.parseInt(hiveId) <= 0) {
      throw new RuntimeException();
    }

    int hiveId1 = Integer.parseInt(hiveId);

    // Ricavo dell'arnia dall'ID ottenuto
    Hive hive = dashboardService.getHive(hiveId1);
    model.addAttribute("hive", hive);

    // Aggiunta delle operazioni da svolgere
    List<Operation> toComplete = operationService.getHiveUncompletedOperations(hiveId1);
    model.addAttribute("toComplete", toComplete);

    // Aggiunta delle operazioni passate
    List<Operation> completed = operationService.getHiveCompletedOperations(hiveId1);
    model.addAttribute("completed", completed);

    // Controllo sulla coerenza tra ID dell'arnia ed email dell'apicoltore
    if(isNotConsistentBetweenHiveIdAndBeekeeperEmail(hiveId1, beekeeper.getEmail())) {
      throw new RuntimeException();
    }

    return "hive/operations-hive";
  }


  // Pianificazione Intervento
  @GetMapping("/add_operation-form")
  public String planningOperation(@RequestParam String operationName,
                                  @RequestParam String operationDate,
                                  @RequestParam String operationHour,
                                  @RequestParam String operationType,
                                  @RequestParam String noteOperation, @RequestParam String hiveId,
                                  HttpSession session, RedirectAttributes redirectAttributes) {
    Beekeeper beekeeper = (Beekeeper) session.getAttribute("beekeeper");

    // Controllo sull'iscrizione dell'apicoltore a uno dei piani di abbonamento
    if (!beekeeper.isSubscribed()) {
      redirectAttributes.addFlashAttribute("error",
          "To create and monitor your hives, subscribe to one of our plans first!");
      return "redirect:/user-page";
    }

    // Controllo sull'id dell'arnia, ricavo dell'arnia e inserimento nel model
    if (!hiveId.matches("^\\d+$") || Integer.parseInt(hiveId) <= 0) {
      throw new RuntimeException();
    }

    int hiveId1 = Integer.parseInt(hiveId);
    Hive hive = dashboardService.getHive(hiveId1);

    // Controllo sulla lunghezza del nome dell'intervento
    if(operationName.length() < 2) {
      redirectAttributes.addFlashAttribute("error", "Operation name too short.");
      return "redirect:/show_operations?hiveId=" + hiveId;
    }

    if(operationName.length() > 70) {
      redirectAttributes.addFlashAttribute("error", "Operation name too long.");
      return "redirect:/show_operations?hiveId=" + hiveId;
    }

    // Controllo sul formato del nome dell'intervento
    if(isFormatNameInvalid(operationName)) {
      redirectAttributes.addFlashAttribute("error", "Invalid operation name.");
      return "redirect:/show_operations?hiveId=" + hiveId;
    }

    // Controllo sul formato delle note dell'intervento
    if(isFormatNotesInvalid(noteOperation)) {
      redirectAttributes.addFlashAttribute("error", "Notes must contain zero or more " +
        "characters, which can be uppercase and lowercase letters, digits, spaces and special" +
        "symbols ( -_()'\",.?!: )");
      return "redirect:/show_operations?hiveId=" + hiveId;
    }
    // Controllo sulla lunghezza delle note dell'intervento
    if(noteOperation.length() > 300) {
      redirectAttributes.addFlashAttribute("error", "Operation notes too long.");
      return "redirect:/show_operations?hiveId=" + hiveId;
    }

    // Controllo sul formato della data dell'intervento
    LocalDateTime operationDate1 = formatDate(operationDate, operationHour);
    if(isDateInvalid(operationDate1)) {
      redirectAttributes.addFlashAttribute("error", "Invalid operation date.");
      return "redirect:/show_operations?hiveId=" + hiveId;
    }

    // Controllo sul tipo dell'intervento
    if(isFormatTypeInvalid(operationType)) {
      redirectAttributes.addFlashAttribute("error", "Invalid operation type.");
      return "redirect:/show_operations?hiveId=" + hiveId;
    }

    // Controllo sulla coerenza tra ID dell'arnia ed email dell'apicoltore
    if(isNotConsistentBetweenHiveIdAndBeekeeperEmail(hiveId1, beekeeper.getEmail())) {
      throw new RuntimeException();
    }

    // Salvataggio nel DB dell'intervento usando il service (se i controlli sono andati a buon fine)
    operationService.planningOperation(operationName, operationType, "Not completed",
        operationDate1, noteOperation, hiveId1, hive.getBeekeeperEmail());

    return "redirect:/show_operations?hiveId=" + hiveId;
  }

  // Modifica Intervento
  @GetMapping("/modify-operation-form")
  public String modifyOperation(@RequestParam String operationIdModify, @RequestParam String operationName,
                                @RequestParam String operationType, @RequestParam String operationStatus,
                                @RequestParam String operationDate, @RequestParam String operationHour,
                                @RequestParam String operationNotes, @RequestParam String hiveIdModify,
                                HttpSession session, RedirectAttributes redirectAttributes) {
    Beekeeper beekeeper = (Beekeeper) session.getAttribute("beekeeper");

    // Controllo sull'iscrizione dell'apicoltore a uno dei piani di abbonamento
    if (!beekeeper.isSubscribed()) {
      redirectAttributes.addFlashAttribute("error",
          "To create and monitor your hives, subscribe to one of our plans first!");
      return "redirect:/user-page";
    }

    // Controllo sull'id dell'arnia, ricavo dell'arnia e inserimento nel model
    if (!hiveIdModify.matches("^\\d+$") || Integer.parseInt(hiveIdModify) <= 0) {
      throw new RuntimeException();
    }

    int hiveId1 = Integer.parseInt(hiveIdModify);

    // Controllo sull'id dell'intervento
    if (!operationIdModify.matches("^\\d+$") || Integer.parseInt(operationIdModify) <= 0) {
      throw new RuntimeException();
    }
    int operationId1 = Integer.parseInt(operationIdModify);

    // Controllo sulla lunghezza e sul formato del nome dell'intervento
    if(operationName.length() < 2) {
      redirectAttributes.addFlashAttribute("error", "Operation name too short.");
      return "redirect:/show_operations?hiveId=" + hiveIdModify;
    }
    if(operationName.length() > 70) {
      redirectAttributes.addFlashAttribute("error", "Operation name too long.");
      return "redirect:/show_operations?hiveId=" + hiveIdModify;
    }
    if(isFormatNameInvalid(operationName)) {
      redirectAttributes.addFlashAttribute("error", "Invalid operation name.");
      return "redirect:/show_operations?hiveId=" + hiveIdModify;
    }

    // Controllo sul formato delle note dell'intervento
    if(isFormatNotesInvalid(operationNotes)) {
      redirectAttributes.addFlashAttribute("error", "Operation notes must contain zero or " +
        "more characters, which can be uppercase and lowercase letters, digits, spaces and " +
        "special symbols ( -_()'\",.?!: )");
      return "hive/operations-hive";
    }
    // Controllo sulla lunghezza delle note dell'intervento
    if(operationNotes.length() > 300) {
      redirectAttributes.addFlashAttribute("error", "Operation notes too long.");
      return "redirect:/show_operations?hiveId=" + hiveIdModify;
    }


    // Conversione sul formato della data dell'intervento
    LocalDateTime operationDate1 = formatDate(operationDate, operationHour);
    if(isDateInvalid(operationDate1)) {
      redirectAttributes.addFlashAttribute("error", "Invalid operation date.");
      return "redirect:/show_operations?hiveId=" + hiveIdModify;
    }

    // Controllo sul tipo dell'intervento
    if(isFormatTypeInvalid(operationType)) {
      redirectAttributes.addFlashAttribute("error", "Invalid operation type.");
      return "redirect:/show_operations?hiveId=" + hiveIdModify;
    }

    // Controllo sul formato dello stato dell'intervento
    if(isFormatStatusInvalid(operationStatus)) {
      redirectAttributes.addFlashAttribute("error", "Invalid operation status.");
      return "redirect:/show_operations?hiveId=" + hiveIdModify;
    }

    // Controllo sulla coerenza tra ID dell'arnia ed email dell'apicoltore
    if(isNotConsistentBetweenHiveIdAndBeekeeperEmail(hiveId1, beekeeper.getEmail())) {
      throw new RuntimeException();
    }

    // Controllo sulla coerenza tra ID dell'intervento ed email dell'apicoltore
    if(isNotConsistentBetweenOperationIdAndBeekeeperEmail(operationId1, beekeeper.getEmail())) {
      throw new RuntimeException();
    }

    // Salvataggio dell'intervento nel database
    operationService.modifyScheduledOperation(operationId1, operationName, operationType, operationStatus, operationDate1, operationNotes,
      hiveId1, beekeeper.getEmail());

    return "redirect:/show_operations?hiveId=" + hiveIdModify;
  }

  // Annullamento Intervento
  @GetMapping("/cancel-operation-form")
  public String cancelOperation(@RequestParam String operationIdCancel,
                                @RequestParam String hiveIdCancel, HttpSession session,
                                RedirectAttributes redirectAttributes) {
    Beekeeper beekeeper = (Beekeeper) session.getAttribute("beekeeper");

    // Controllo sull'iscrizione dell'apicoltore a uno dei piani di abbonamento
    if (!beekeeper.isSubscribed()) {
      redirectAttributes.addFlashAttribute("error",
          "To create and monitor your hives, subscribe to one of our plans first!");
      return "redirect:/user-page";
    }

    // Controllo sull'id dell'intervento
    if (!operationIdCancel.matches("^\\d+$") || Integer.parseInt(operationIdCancel) <= 0) {
      throw new RuntimeException();
    }
    int operationId1 = Integer.parseInt(operationIdCancel);

    // Controllo sull'id dell'arnia, ricavo dell'arnia e inserimento nel model
    if (!hiveIdCancel.matches("^\\d+$") || Integer.parseInt(hiveIdCancel) <= 0) {
      throw new RuntimeException();
    }
    int hiveId1 = Integer.parseInt(hiveIdCancel);

    // Controllo sulla coerenza tra ID dell'arnia ed email dell'apicoltore
    if(isNotConsistentBetweenHiveIdAndBeekeeperEmail(hiveId1, beekeeper.getEmail())) {
      throw new RuntimeException();
    }
    // Controllo sulla coerenza tra ID dell'intervento ed email dell'apicoltore
    if(isNotConsistentBetweenOperationIdAndBeekeeperEmail(operationId1, beekeeper.getEmail())) {
      throw new RuntimeException();
    }

    // Eliminazione dell'intervento dal database
    operationService.cancelScheduledOperation(operationId1);

    return "redirect:/show_operations?hiveId=" + hiveIdCancel;
  }


  // Impostazione Intervento come completato
  @GetMapping("/change-operation-status-form")
  public String changeOperationStatus(@RequestParam String operationIdStatus,
                                      @RequestParam String hiveIdStatus, HttpSession session,
                                      Model model, RedirectAttributes redirectAttributes) {
    Beekeeper beekeeper = (Beekeeper) session.getAttribute("beekeeper");

    // Controllo sull'iscrizione dell'apicoltore a uno dei piani di abbonamento
    if (!beekeeper.isSubscribed()) {
      redirectAttributes.addFlashAttribute("error",
          "To create and monitor your hives, subscribe to one of our plans first!");
      return "redirect:/user-page";
    }

    // Controllo sull'id dell'intervento
    if (!operationIdStatus.matches("^\\d+$") && Integer.parseInt(operationIdStatus) <= 0) {
      throw new RuntimeException();
    }
    int operationId1 = Integer.parseInt(operationIdStatus);

    // Controllo sull'id dell'arnia, ricavo dell'arnia e inserimento nel model
    if (!hiveIdStatus.matches("^\\d+$") && Integer.parseInt(hiveIdStatus) <= 0) {
      throw new RuntimeException();
    }
    int hiveId1 = Integer.parseInt(hiveIdStatus);

    Hive hive = dashboardService.getHive(hiveId1);
    model.addAttribute("hive", hive);

    // Controllo sulla coerenza tra ID dell'arnia ed email dell'apicoltore
    if(isNotConsistentBetweenHiveIdAndBeekeeperEmail(hiveId1, beekeeper.getEmail())) {
      throw new RuntimeException();
    }
    // Controllo sulla coerenza tra ID dell'intervento ed email dell'apicoltore
    if(isNotConsistentBetweenOperationIdAndBeekeeperEmail(operationId1, beekeeper.getEmail())) {
      throw new RuntimeException();
    }

    // Modifica dello stato dell'intervento
    operationService.changeOperationStatus(operationId1);

    return "redirect:/show_operations?hiveId=" + hiveIdStatus;
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
    String regex = "^[a-zA-Z0-9\\s\\-_()'\".,?!:]*$";
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