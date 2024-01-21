package it.unisa.c10.beehAIve.controller.gestioneArnie.gestioneDashboard;

import it.unisa.c10.beehAIve.persistence.entities.Beekeeper;
import it.unisa.c10.beehAIve.persistence.entities.Hive;
import it.unisa.c10.beehAIve.service.gestioneArnie.DashboardService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.ArrayList;
import java.util.List;

@Controller
public class DashboardController {
  private final DashboardService dashboardService;

  public DashboardController(DashboardService dashboardService) {
    this.dashboardService = dashboardService;
  }

  @GetMapping("/dashboard")
  public String showHivesByFilters(@RequestParam(required = false) String nickname,
                                   @RequestParam(required = false)  String filterType,
                                   HttpSession session, Model model,
                                   RedirectAttributes redirectAttributes) {
    Beekeeper beekeeper = (Beekeeper) session.getAttribute("beekeeper");
    String beekeeperEmail = beekeeper.getEmail();
    List<Hive> hives = new ArrayList<>();

    // Controllo sull'iscrizione dell'apicoltore a uno dei piani di abbonamento
    if (!beekeeper.isSubscribed()) {
      redirectAttributes.addFlashAttribute("error",
          "To create and monitor your hives, subscribe to one of our plans first!");
      return "redirect:/user";
    }

    if (filterType == null && nickname == null) {

      hives = dashboardService.getBeekeeperHives(beekeeper.getEmail());

    } else if (filterType == null) {

      // Se nessun filtro è applicato, si visualizzano tutte le arnie
      if (nickname.isBlank()) {
        hives = dashboardService.getBeekeeperHives(beekeeper.getEmail());
      } else { // Visualizzazione delle arnie solo in base al nickname
        hives = dashboardService.getBeekeeperHivesByNickname(beekeeperEmail, nickname);
      }

    } else {

      // Controllo sui valori che può assumere filterType
      if(!(filterType.equals("scheduledOperations") || filterType.equals("healthStatus"))) {
        throw new RuntimeException();
      }

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

  @GetMapping("/new_hive")
  public String showCreationHive() {
    return "hive/creation-hive";
  }
}