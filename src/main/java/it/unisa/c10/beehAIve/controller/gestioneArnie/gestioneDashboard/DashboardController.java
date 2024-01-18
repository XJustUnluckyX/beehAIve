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

import java.util.List;

//Gestisce la visualizzazione di tutte le arnie di un apicoltore ed eventuali filtri (Anomalie rilevate, interventi pianificati)

@Controller
public class DashboardController {
  @Autowired
  private DashboardService dashboardService;

  @GetMapping("/dashboard")
  public String showAllHives(Model model, HttpSession session) {
    Beekeeper beekeeper = (Beekeeper) session.getAttribute("beekeeper");
    List<Hive> hives = dashboardService.getBeekeeperHives(beekeeper.getEmail());

    model.addAttribute("hives", hives);
    return "hive/dashboard";
  }

  @GetMapping("/state-hive")
  public String showStateHive (@RequestParam String hiveId, Model model) {
    if (!hiveId.matches("//d+") && Integer.parseInt(hiveId) <= 0) {
      return "errors/error500";
    }

    int intHiveId = Integer.parseInt(hiveId);
    Hive hive = dashboardService.getHive(intHiveId);

    model.addAttribute("hive", hive);

    return "hive/state-hive";
  }
}