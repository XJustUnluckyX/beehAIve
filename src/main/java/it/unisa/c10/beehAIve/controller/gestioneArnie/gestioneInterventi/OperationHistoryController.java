package it.unisa.c10.beehAIve.controller.gestioneArnie.gestioneInterventi;

import it.unisa.c10.beehAIve.persistence.entities.Beekeeper;
import it.unisa.c10.beehAIve.persistence.entities.Operation;
import it.unisa.c10.beehAIve.service.gestioneArnie.OperationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

// Gestisce lo storico degli interventi ed eventuali filtri associati (Interventi completati, futuri, passati, ecc ecc)
@Controller
public class OperationHistoryController {
  private OperationService operationService;
  @Autowired
  public OperationHistoryController(OperationService operationService) {
    this.operationService = operationService;
  }

  // Chiamata ajax per ottenere gli interventi del calendario di una singola arnia
  @GetMapping("/get_hive_operation_history")
  @ResponseBody
  public String getHiveOperationHistory(@RequestParam int hiveId) {
    List<Operation> operations = operationService.viewHiveOperations(hiveId);
    return operationService.convertOperationToCalendar(operations);
  }

  // Chiamata ajax per ottenere gli interventi del calendario di tutte le arnie di un apicoltore
  @GetMapping("get_beekeeper_operation_history")
  @ResponseBody
  public String getBeekeeperOperationHistory(HttpSession session) {
    Beekeeper user = (Beekeeper) session.getAttribute("beekeeper");
    List<Operation> operations = operationService.viewAllBeekeeperOperations(user.getEmail());
    return operationService.convertOperationToCalendar(operations);
  }

  // Chiamata Ajax per ottenere le informazioni di un singolo intervento da caricare nella modifica
  @GetMapping("/get_operation_information")
  @ResponseBody
  public String getOperationInformation(@RequestParam int operationId) {
    Operation op = operationService.retrieveOperationFromDB(operationId);
    return operationService.convertOperationToString(op);
  }

  @GetMapping("/calendar-planning")
  public String calendarTest (HttpSession session, RedirectAttributes redirectAttributes) {
    Beekeeper beekeeper = (Beekeeper) session.getAttribute("beekeeper");

    // Controllo sull'iscrizione dell'apicoltore a uno dei piani di abbonamento
    if (!beekeeper.isSubscribed()) {
      redirectAttributes.addFlashAttribute("error",
          "To create and monitor your hives, subscribe to one of our plans first!");
      return "redirect:/user-page";
    }

    return "calendar-page";
  }

}
