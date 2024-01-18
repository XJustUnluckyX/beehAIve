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

import java.util.Arrays;
import java.util.List;

// Gestisce Creazione, Modifica, Cancellazione e Visualizzazione di una singola arnia (CRUD)

@Controller
@SessionAttributes("beekeeper")
public class HiveController {
  @Autowired
  private DashboardService dashboardService;

  @GetMapping("/create-hive")
  public String createHive(@RequestParam String nickname, @RequestParam String hiveType,
                           @RequestParam String beeSpecies, HttpSession session, Model model) {
    Beekeeper beekeeper = (Beekeeper) session.getAttribute("beekeeper");
    String beekeeperEmail = beekeeper.getEmail();
    int hivesCount = dashboardService.getBeekeeperHivesCount(beekeeperEmail);
    double payment = beekeeper.getPaymentDue();

    // Controllo della validità del formato del nickname dell'arnia
    if (!nickname.matches("^[a-zA-Z0-9\\s\\-_()’”]+$")) {
      model.addAttribute("error","Invalid nickname format.");
      return "hive/creation-hive";
    }

    // Controllo della lunghezza del nickname dell'arnia
    if (nickname.length() < 2) {
      model.addAttribute("error","Insufficient nickname length.");
      return "hive/creation-hive";
    }
    if (nickname.length() > 50) {
      model.addAttribute("error","Nickname length too long.");
      return "hive/creation-hive";
    }

    // Controllo della validità del tipo d'arnia
    List<String> validHiveTypes = Arrays.asList("Langstroth", "Warre", "Top-Bar", "Horizontal",
                                                "Vertical", "Bee Skep", "WBC", "Dadant");
    if(!validHiveTypes.contains(hiveType)) {
      model.addAttribute("error","Invalid hive type.");
      return "hive/creation-hive";
    }

    // Controllo della validità della specie d'api dell'arnia
    List<String> validBeeSpecies = Arrays.asList("Apis millifera", "Apis cerana",
                                                 "Apis dorsata", "Apis florea");
    if(!validBeeSpecies.contains(beeSpecies)) {
      model.addAttribute("error","Invalid bee species.");
      return "hive/creation-hive";
    }

    // Controllo del rispetto del limite del numero di arnie in base all'abbonamento
    if ((payment == 50 && hivesCount >= 15)
     || (payment == 350 && hivesCount >= 100)
     || (payment == 1050 && hivesCount >= 300)) {
      model.addAttribute("error",
          "You've reached the maximum number of hives!");
      // Redirect alla dashboard con errore
      return "hive/dashboard";
    }

    // Creazione dell'arnia
    dashboardService.createHive(beekeeperEmail, nickname, hiveType, beeSpecies);

    // Ottenimento di tutte le arnie registrate dall'apicoltore, inclusa quella appena inserita
    List<Hive> hives = dashboardService.getBeekeeperHives(beekeeperEmail);
    // Passaggio della lista di arnie
    model.addAttribute("hives", hives);

    // Redirect alla dashboard aggiornata
    return "hive/dashboard";
  }
}
