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

import java.util.ArrayList;
import java.util.List;

@Controller
public class DashboardController {
  private final DashboardService dashboardService;

  public DashboardController(DashboardService dashboardService) {
    this.dashboardService = dashboardService;
  }

  @GetMapping("/dashboard")
  public String showBeekeeperHives(HttpSession session, Model model) {
    // Ottenimento di tutte le arnie registrate dall'apicoltore
    Beekeeper beekeeper = (Beekeeper) session.getAttribute("beekeeper");
    List<Hive> hives = dashboardService.getBeekeeperHives(beekeeper.getEmail());

    // Passaggio della lista di arnie
    model.addAttribute("hives", hives);

    // Redirect alla dashboard con tutte le arnie ottenute
    return "hive/dashboard";
  }

  @GetMapping("/show-by-filters")
  public String showHivesByFilters(@RequestParam String nickname,
                                   @RequestParam(required = false)  String filterType,
                                   HttpSession session, Model model) {
    // Controllo sui valori che può assumere filterType
    if(!(filterType.equals("scheduledOperations") || filterType.equals("healthStatus"))) {
      return "error/500";
    }

    Beekeeper beekeeper = (Beekeeper) session.getAttribute("beekeeper");
    String beekeeperEmail = beekeeper.getEmail();
    List<Hive> hives = new ArrayList<>();

    if (filterType == null) {

      // Se nessun filtro è applicato, si visualizzano tutte le arnie
      if (nickname.isBlank()) {
        return showBeekeeperHives(session, model);
      } else { // Visualizzazione delle arnie solo in base al nickname
        hives = dashboardService.getBeekeeperHivesByNickname(beekeeperEmail, nickname);
      }

    } else {

      // Visualizzazione delle arnie solo in base a interventi pianificati
      if (nickname.isBlank() && filterType.equals("scheduledOperations")) {
        hives = dashboardService.getBeekeeperHivesWithScheduledOperations(beekeeperEmail);
      }

      // Visualizzazione delle arnie solo in base a problemi di salute
      if (nickname.isBlank() && filterType.equals("healthStatus")) {
        hives = dashboardService.getBeekeeperHivesWithHealthIssues(beekeeperEmail);
      }

      // Visualizzazione delle arnie in base al nickname e a interventi pianificati
      if(!nickname.isBlank() && filterType.equals("scheduledOperations")) {
        hives = dashboardService.getBeekeeperHivesByNicknameAndScheduledOperations(beekeeperEmail,
            nickname);
      }

      // Visualizzazione delle arnie in base al nickname e a problemi di salute
      if(!nickname.isBlank() && filterType.equals("healthStatus")) {
        hives = dashboardService.getBeekeeperHivesByNicknameAndHealthIssues(beekeeperEmail, nickname);
      }

    }

    // Passaggio della lista di arnie
    model.addAttribute("hives", hives);

    // Redirect alla dashboard con tutte le arnie ottenute
    return "hive/dashboard";
  }

  @GetMapping("/show-hive-creation")
  public String showCreationHive() {
    return "hive/creation-hive";
  }
}