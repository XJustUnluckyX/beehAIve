package it.unisa.c10.beehAIve.controller.gestioneArnie.gestioneInterventi;

import it.unisa.c10.beehAIve.persistence.entities.Operation;
import it.unisa.c10.beehAIve.service.gestioneArnie.OperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

// Gestisce lo storico degli interventi ed eventuali filtri associati (Interventi completati, futuri, passati, ecc ecc)
@Controller
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

}
