package it.unisa.c10.beehAIve.controller.gestioneAnomalie;

import it.unisa.c10.beehAIve.persistence.entities.Anomaly;
import it.unisa.c10.beehAIve.persistence.entities.Beekeeper;
import it.unisa.c10.beehAIve.service.gestioneAnomalie.AnomalyService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AnomalyController {

  private final AnomalyService anomalyService;

  @Autowired
  public AnomalyController(AnomalyService anomalyService) {
    this.anomalyService = anomalyService;
  }

  // Risolve un'anomalia
  @GetMapping("/resolve_anomaly")
  @ResponseBody
  public String resolveAnomaly (@RequestParam int anomalyId, HttpSession session) {

    if (!checkHiveOwnership(session, anomalyId))
      return "failure";

    anomalyService.resolveAnomaly(anomalyId);

    return "success";
  }

  // Cancella un'anomalia
  @GetMapping("/delete_anomaly")
  @ResponseBody
  public String deleteAnomaly (@RequestParam int anomalyId, HttpSession session) {

    if (!checkHiveOwnership(session, anomalyId))
      return "failure";

    anomalyService.deleteAnomaly(anomalyId);

    return "success";
  }




  // Bisogna fare il controllo che l'anomalia sia effettivamente di un'arnia dell'apicoltore
  private boolean checkHiveOwnership (HttpSession session, int anomalyId) {

    Anomaly anomaly = anomalyService.getAnomaly(anomalyId);

    String beekeeperEmail = ((Beekeeper) session.getAttribute("beekeeper")).getEmail();

    return anomaly.getBeekeeperEmail().equals(beekeeperEmail);

  }




}