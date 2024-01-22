package it.unisa.c10.beehAIve.controller.gestioneArnie.gestioneInterventi;

import it.unisa.c10.beehAIve.persistence.entities.Beekeeper;
import it.unisa.c10.beehAIve.persistence.entities.Operation;
import it.unisa.c10.beehAIve.service.gestioneArnie.OperationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
public class OperationHistoryController {
  private final OperationService operationService;
  @Autowired
  public OperationHistoryController(OperationService operationService) {
    this.operationService = operationService;
  }

  @GetMapping("/calendar")
  public String calendarTest (HttpSession session, RedirectAttributes redirectAttributes) {
    Beekeeper beekeeper = (Beekeeper) session.getAttribute("beekeeper");

    // Controllo sull'iscrizione dell'apicoltore a uno dei piani di abbonamento
    if (!beekeeper.isSubscribed()) {
      redirectAttributes.addFlashAttribute("error",
        "To create and monitor your hives, subscribe to one of our plans first!");
      return "redirect:/user";
    }

    return "calendar-page";
  }

  /**
   * Gestisce le richieste GET per ottenere la cronologia degli interventi di un'arnia attraverso
   * AJAX.
   * @param hiveId L'ID dell'arnia di cui vogliamo ottenere la cronologia delle operazioni.
   * @return Una stringa JSON che rappresenta la cronologia delle operazioni dell'arnia specificata.
   * @see OperationService#viewHiveOperations(int) 
   * @see OperationService#convertOperationToCalendar(List) 
   */
  @GetMapping("/get_hive_operation_history")
  @ResponseBody
  public String getHiveOperationHistory(@RequestParam int hiveId) {
    List<Operation> operations = operationService.viewHiveOperations(hiveId);
    return operationService.convertOperationToCalendar(operations);
  }

  /**
   * Gestisce le richieste GET per ottenere la cronologia degli interventi di tutte le arnie
   * associate a un apicoltore attraverso AJAX.
   * @param session Un oggetto {@code HttpSession} per ottenere l'oggetto {@code Beekeeper} dalla
   *                sessione.
   * @return Una stringa JSON che rappresenta la cronologia delle operazioni di tutte le arnie
   *         dell'apicoltore autenticato.
   * @see OperationService#viewAllBeekeeperOperations(String)
   * @see OperationService#convertOperationToCalendar(List)
   */
  @GetMapping("get_beekeeper_operation_history")
  @ResponseBody
  public String getBeekeeperOperationHistory(HttpSession session) {
    Beekeeper user = (Beekeeper) session.getAttribute("beekeeper");
    List<Operation> operations = operationService.viewAllBeekeeperOperations(user.getEmail());
    return operationService.convertOperationToCalendar(operations);
  }

  /**
   * Gestisce le richieste GET per ottenere le informazioni di un intervento attraverso AJAX.
   * @param operationId L'ID dell'intervento di cui vogliamo ottenere le informazioni.
   * @return Una stringa JSON rappresentante le informazioni dell'intervento specificato.
   * @see OperationService#retrieveOperationFromDB(int)
   * @see OperationService#convertOperationToString(Operation)
   */
  @GetMapping("/get_operation_information")
  @ResponseBody
  public String getOperationInformation(@RequestParam int operationId) {
    Operation op = operationService.retrieveOperationFromDB(operationId);
    return operationService.convertOperationToString(op);
  }

}
