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
  public String redirectToOperationPanel(@RequestParam String hiveId, Model model, HttpSession session) {
    // Controllo sull'id dell'arnia
    if (!hiveId.matches("^\\d+$") || Integer.parseInt(hiveId) <= 0) {
      throw new RuntimeException();
    }

    int hiveId1 = Integer.parseInt(hiveId);

    // Ricavo dell'arnia dall'id ottenuto
    Hive hive = dashboardService.getHive(hiveId1);
    model.addAttribute("hive", hive);

    // Aggiungiamo le operazioni da svolgere
    List<Operation> toComplete = operationService.getHiveUncompletedOperations(hiveId1);
    model.addAttribute("toComplete", toComplete);

    // Aggiungiamo le operazioni passate
    List<Operation> completed = operationService.getHiveCompletedOperations(hiveId1);
    model.addAttribute("completed", completed);

    Beekeeper beekeeper = (Beekeeper) session.getAttribute("beekeeper");
    // Controllo sulla coerenza tra id dell'arnia e email del beekeeper
    if(isNotConsistentBetweenHiveIdAndBeekeeperEmail(hiveId1, beekeeper.getEmail())) {
      throw new RuntimeException();
    }

    return "hive/operations-hive";
  }

  // Pianificazione Intervento
  @GetMapping("/add_operation-form")
  public String planningOperation(@RequestParam String operationName, @RequestParam String operationDate,
                                  @RequestParam String operationHour, @RequestParam String operationType,
                                  @RequestParam String noteOperation, @RequestParam String hiveId,
                                  HttpSession session, RedirectAttributes redirectAttributes) {

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

    // Controllo sulla lunghezza e sul formato delle note dell'intervento
    if(noteOperation.length() > 300) {
      redirectAttributes.addFlashAttribute("error", "Operation notes too long.");
      return "redirect:/show_operations?hiveId=" + hiveId;
    }

    if(isFormatNotesInvalid(noteOperation)) {
      redirectAttributes.addFlashAttribute("error", "Invalid operation notes.");
      return "redirect:/show_operations?hiveId=" + hiveId;
    }

    // Conversione sul formato e controllo sulla correttezza della data dell'intervento
    LocalDateTime operationDate1 = formatDate(operationDate, operationHour);
    if(isDateInvalid(operationDate1)) {
      redirectAttributes.addFlashAttribute("error", "Invalid operation date.");
      return "redirect:/show_operations?hiveId=" + hiveId;
    }

    // Controllo sul formato del tipo dell'intervento
    if(isFormatTypeInvalid(operationType)) {
      redirectAttributes.addFlashAttribute("error", "Invalid operation type.");
      return "redirect:/show_operations?hiveId=" + hiveId;
    }

    // Controllo sulla coerenza tra id dell'arnia e email del beekeeper
    Beekeeper beekeeper = (Beekeeper) session.getAttribute("beekeeper");
    if(isNotConsistentBetweenHiveIdAndBeekeeperEmail(hiveId1, beekeeper.getEmail())) {
      throw new RuntimeException();
    }

    // Salvataggio nel DB dell'intervento usando il service (se i controlli sono andati a buon fine)
    operationService.planningOperation(operationName, operationType, "Not completed", operationDate1,
          noteOperation, hiveId1, hive.getBeekeeperEmail());

    return "redirect:/show_operations?hiveId=" + hiveId;
  }

  // Modifica Intervento
  @GetMapping("/modify-operation-form")
  public String modifyOperation(@RequestParam String operationIdModify, @RequestParam String operationName,
                                @RequestParam String operationType, @RequestParam String operationStatus,
                                @RequestParam String operationDate, @RequestParam String operationHour,
                                @RequestParam String operationNotes, @RequestParam String hiveIdModify,
                                HttpSession session, RedirectAttributes redirectAttributes) {

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

    // Controllo sulla lunghezza e sul formato delle note dell'intervento
    if(operationNotes.length() > 300) {
      redirectAttributes.addFlashAttribute("error", "Operation notes too long.");
      return "redirect:/show_operations?hiveId=" + hiveIdModify;
    }
    if(isFormatNotesInvalid(operationNotes)) {
      redirectAttributes.addFlashAttribute("error", "Invalid operation notes.");
      return "redirect:/show_operations?hiveId=" + hiveIdModify;
    }

    // Conversione sul formato e controllo sulla correttezza della data dell'intervento
    LocalDateTime operationDate1 = formatDate(operationDate, operationHour);
    if(isDateInvalid(operationDate1)) {
      redirectAttributes.addFlashAttribute("error", "Invalid operation date.");
      return "redirect:/show_operations?hiveId=" + hiveIdModify;
    }

    // Controllo sul formato del tipo dell'intervento
    if(isFormatTypeInvalid(operationType)) {
      redirectAttributes.addFlashAttribute("error", "Invalid operation type.");
      return "redirect:/show_operations?hiveId=" + hiveIdModify;
    }

    // Controllo sullo status
    if(isFormatStatusInvalid(operationStatus)) {
      redirectAttributes.addFlashAttribute("error", "Invalid operation status.");
      return "redirect:/show_operations?hiveId=" + hiveIdModify;
    }

    // Controllo sulla coerenza tra id dell'arnia e email del beekeeper
    Beekeeper beekeeper = (Beekeeper) session.getAttribute("beekeeper");
    if(isNotConsistentBetweenHiveIdAndBeekeeperEmail(hiveId1, beekeeper.getEmail())) {
      throw new RuntimeException();
    }

    // Controllo sulla coerenza tra id dell'intervento e email del beekeeper
    if(isNotConsistentBetweenOperationIdAndBeekeeperEmail(operationId1, beekeeper.getEmail())) {
      throw new RuntimeException();
    }

    // Aggiornamento nel DB dell'intervento usando il service (se i controlli sono andati a buon fine)
    operationService.modifyScheduledOperation(operationId1, operationName, operationType, operationStatus, operationDate1, operationNotes,
      hiveId1, beekeeper.getEmail());

    return "redirect:/show_operations?hiveId=" + hiveIdModify;
  }

  // Annullamento Intervento
  @GetMapping("/cancel-operation-form")
  public String cancelOperation(@RequestParam String operationIdCancel, @RequestParam String hiveIdCancel, HttpSession session) {

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

    // Controllo sulla coerenza tra id dell'arnia e email del beekeeper
    Beekeeper beekeeper = (Beekeeper) session.getAttribute("beekeeper");
    if(isNotConsistentBetweenHiveIdAndBeekeeperEmail(hiveId1, beekeeper.getEmail())) {
      throw new RuntimeException();
    }
    // Controllo sulla coerenza tra id dell'intervento e email del beekeeper
    if(isNotConsistentBetweenOperationIdAndBeekeeperEmail(operationId1, beekeeper.getEmail())) {
      throw new RuntimeException();
    }

    // Eliminazione nel DB dell'intervento usando il service (se il controllo è andato a buon fine)
    operationService.cancelScheduledOperation(operationId1);

    return "redirect:/show_operations?hiveId=" + hiveIdCancel;
  }


  // Impostazione Intervento come completato
  @GetMapping("/change-operation-status-form")
  public String changeOperationStatus(@RequestParam String operationIdStatus, @RequestParam String hiveIdStatus, HttpSession session, Model model) {
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

    // Controllo sulla coerenza tra id dell'arnia e email del beekeeper
    Beekeeper beekeeper = (Beekeeper) session.getAttribute("beekeeper");
    if(isNotConsistentBetweenHiveIdAndBeekeeperEmail(hiveId1, beekeeper.getEmail())) {
      throw new RuntimeException();
    }
    // Controllo sulla coerenza tra id dell'intervento e email del beekeeper
    if(isNotConsistentBetweenOperationIdAndBeekeeperEmail(operationId1, beekeeper.getEmail())) {
      throw new RuntimeException();
    }

    // Cambio lo status dell'intervento
    operationService.changeOperationStatus(operationId1);

    return "redirect:/show_operations?hiveId=" + hiveIdStatus;
  }

  // Regex sul nome dell'intervento
  private boolean isFormatNameInvalid(String name) {
    String regex = "^[a-zA-Z0-9\\s\\-_()’”]+$";
    return !name.matches(regex);
  }

  // Controllo del formato del tipo dell'intervento
  private boolean isFormatTypeInvalid(String type){
    return !(type.equals("Medical Inspection") || type.equals("Medical Treatment") ||
      type.equals("Check Population") || type.equals("Extraction") || type.equals("Vet Visit") ||
      type.equals("Feeding") || type.equals("Transfer") || type.equals("Maintenance"));
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
    String regex = "^[a-zA-Z0-9\\s\\-_()’”.,?!:]+$";
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