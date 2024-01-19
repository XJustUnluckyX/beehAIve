package it.unisa.c10.beehAIve.controller.gestioneArnie.gestioneStato;

import it.unisa.c10.beehAIve.persistence.entities.Anomaly;
import it.unisa.c10.beehAIve.persistence.entities.Beekeeper;
import it.unisa.c10.beehAIve.persistence.entities.Hive;
import it.unisa.c10.beehAIve.service.gestioneArnie.DashboardService;
import it.unisa.c10.beehAIve.service.gestioneArnie.StatusService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.List;

// Permette di Notificare le Anomalie all'apicoltore (Cambia anche lo stato dell'arnia)
// Permette inoltre di risolvere le anomalie e di eliminarla dal database.
@Controller
@SessionAttributes("beekeeper")
public class NotifyAnomalyController {

  public StatusService statusService;
  public DashboardService dashboardService;

  @Autowired
  public  NotifyAnomalyController(StatusService statusService,DashboardService dashboardService){
    this.statusService = statusService;
    this.dashboardService = dashboardService;
  }

  private boolean checkHive(int hiveId,String beekeeperEmail){

    List<Hive> hives = dashboardService.getBeekeeperHives(beekeeperEmail);
    int i;

    for (i = 0;i < hives.size(); i++){
      if (hives.get(i).getId() == hiveId){
        return true;
      }
    }

    return false;

  }

  @GetMapping("/complited-Anomali")
  public String complitedAnomaliy(@RequestParam String anomalyId, HttpSession session){

  Beekeeper beekeeper = (Beekeeper) session.getAttribute("beekeeper");
  String beekeeperEmail = beekeeper.getEmail();
  int intAnomalyId = Integer.parseInt(anomalyId);
  Anomaly anomaly = statusService.showSingleAnomaly(intAnomalyId);
  int hiveId = anomaly.getHiveId();

  if (!checkHive(hiveId,beekeeperEmail)){
    return "errors/error500";
  }

  statusService.resolveAnomaly(intAnomalyId);

  return "hive/state-hive";

  }

  @GetMapping("/delete-anomaly")
  public String deleteAnomaly(@RequestParam String anomalyId, HttpSession session){

    Beekeeper beekeeper = (Beekeeper) session.getAttribute("beekeeper");
    String beekeeperEmail = beekeeper.getEmail();
    int intAnomalyId = Integer.parseInt(anomalyId);
    Anomaly anomaly = statusService.showSingleAnomaly(intAnomalyId);
    int hiveId = anomaly.getHiveId();

    if (!checkHive(hiveId,beekeeperEmail)){
      return "errors/error500";
    }

    statusService.deleteAnomaly(intAnomalyId);

    return "hive/state-hibe";

  }

}
