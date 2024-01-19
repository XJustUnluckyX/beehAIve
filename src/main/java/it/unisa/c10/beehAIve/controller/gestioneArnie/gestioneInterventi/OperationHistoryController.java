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
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.List;

// Gestisce lo storico degli interventi ed eventuali filtri associati (Interventi completati, futuri, passati, ecc ecc)
@Controller
@SessionAttributes("beekeeper")
public class OperationHistoryController {

  private OperationService operationService;
  @Autowired
  public OperationHistoryController(OperationService operationService) {
    this.operationService = operationService;
  }
  @GetMapping("/get_hive_operation_history")
  @ResponseBody
  public String getHiveOperationHistory(@RequestParam int hiveId) {
    List<Operation> operations = operationService.viewHiveOperations(hiveId);
    String result = operationService.convertOperationToCalendar(operations);
    return result;
  }

  @GetMapping("get_beekeeper_operation_history")
  @ResponseBody
  public String getBeekeeperOperationHistory(HttpSession session) {
    Beekeeper user = (Beekeeper) session.getAttribute("beekeeper");
    List<Operation> operations = operationService.viewAllBeekeeperOperations(user.getEmail());
    String result = operationService.convertOperationToCalendar(operations);
    return result;
  }

}
