package it.unisa.c10.beehAIve.controller.gestioneArnie.gestioneDashboard;

import it.unisa.c10.beehAIve.persistence.entities.Beekeeper;
import it.unisa.c10.beehAIve.persistence.entities.Hive;
import it.unisa.c10.beehAIve.service.gestioneArnie.DashboardService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.List;

//Gestisce la visualizzazione di tutte le arnie di un apicoltore ed eventuali filtri (Anomalie rilevate, interventi pianificati)

@Controller
@SessionAttributes("beekeeper")
public class DashboardController {
  @Autowired
  private DashboardService dashboardService;

  @GetMapping("/dashboard")
  public String showAllHives(HttpSession session, Model model) {
    // Ottenimento di tutte le arnie registrate dall'apicoltore
    Beekeeper beekeeper = (Beekeeper) session.getAttribute("beekeeper");
    List<Hive> hives = dashboardService.getBeekeeperHives(beekeeper.getEmail());

    // Passaggio della lista di arnie
    model.addAttribute("hives", hives);

    // Redirect alla dashboard con tutte le arnie ottenute
    return "hive/dashboard";
  }

  @GetMapping("/state-hive")
  public String showStateHive(@RequestParam String hiveId, Model model) {
    // Controllo della validità dell'ID dell'arnia
    if (!hiveId.matches("//d+") && Integer.parseInt(hiveId) <= 0) {
      return "errors/error500";
    }

    // Ottenimento dell'arnia
    int intHiveId = Integer.parseInt(hiveId);
    Hive hive = dashboardService.getHive(intHiveId);

    // Passaggio dell'arnia
    model.addAttribute("hive", hive);

    // Redirect alla pagina relativa all'arnia
    return "hive/state-hive";
  }

  @GetMapping("/show-hive-creation")
  public String showCreationHive() {
    return "hive/creation-hive";
  }
}