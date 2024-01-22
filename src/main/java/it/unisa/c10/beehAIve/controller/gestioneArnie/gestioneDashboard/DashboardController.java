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

  /**
   * Gestisce le richieste GET per reindirizzare l'apicoltore alla dashboard delle proprie arnie.
   * @return Una stringa rappresentante l'URL di reindirizzamento alla dashboard delle proprie
   *         arnie.
   */
  @GetMapping("/new_hive")
  public String showCreationHive() {
    return "hive/creation-hive";
  }

  /**
   * Gestisce le richieste GET per visualizzare la dashboard di tutte le arnie di un apicoltore.
   * Questo metodo prevede anche l'utilizzo di parametri che possono filtrare la ricerca delle
   * arnie.
   * @param nickname (Opzionale) Il nickname, o parte del nickname, dell'arnia da ricercare.
   * @param filterType (Opzionale) Il tipo di filtro da applicare, che può assumere solo i seguenti
   *                   due valori:
   *                   <ul>
   *                   <li>scheduledOperations</li>
   *                   <li>healthStatus</li>
   *                   </ul>
   * @param session Un oggetto {@code HttpSession} per ottenere l'oggetto {@code Beekeeper} dalla
   *                sessione.
   * @param model Un oggetto {@code Model} per aggiungere attributi alla risposta.
   * @param redirectAttributes Un oggetto {@code RedirectAttributes} per trasferire messaggi di
   *                           risposta.
   * @return Una stringa che rappresenta un URL di reindirizzamento alla dashboard contenente tutte
   *         le arnie dell'apicoltore.
   * @see DashboardService#getBeekeeperHives(String) 
   * @see DashboardService#getBeekeeperHivesByNickname(String, String) 
   * @see DashboardService#getBeekeeperHivesWithScheduledOperations(String) 
   * @see DashboardService#getBeekeeperHivesWithHealthIssues(String) 
   * @see DashboardService#getBeekeeperHivesByNicknameAndScheduledOperations(String, String) 
   * @see DashboardService#getBeekeeperHivesByNicknameAndHealthIssues(String, String) 
   */
  @GetMapping("/dashboard")
  public String showHivesByFilters(
      @RequestParam(required = false) String nickname,
      @RequestParam(required = false) String filterType,
      HttpSession session,
      Model model,
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

}