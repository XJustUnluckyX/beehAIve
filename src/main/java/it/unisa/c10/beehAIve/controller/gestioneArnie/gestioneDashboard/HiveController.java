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

  private boolean isNicknameFormatInvalid(String nickname) {
    return !nickname.matches("^[a-zA-Z0-9\\s\\-_()’”]+$");
  }

  private boolean isNicknameLenghtTooShort(String nickname) {
    return nickname.length() < 2;
  }

  private boolean isNicknameLenghtTooLong(String nickname) {
    return nickname.length() > 50;
  }

  private boolean isHiveTypeInvalid(String hiveType) {
    List<String> validHiveTypes = Arrays.asList("Langstroth", "Warre", "Top-Bar", "Horizontal",
        "Vertical", "Bee Skep", "WBC", "Dadant");
    return !validHiveTypes.contains(hiveType);
  }

  private boolean isBeeSpeciesInvalid(String beeSpecies) {
    List<String> validBeeSpecies = Arrays.asList("Apis millifera", "Apis cerana", "Apis dorsata",
        "Apis florea");
    return !validBeeSpecies.contains(beeSpecies);
  }

  @GetMapping("/create-hive")
  public String createHive(@RequestParam String nickname, @RequestParam String hiveType,
                           @RequestParam String beeSpecies, HttpSession session, Model model) {
    Beekeeper beekeeper = (Beekeeper) session.getAttribute("beekeeper");
    String beekeeperEmail = beekeeper.getEmail();
    int hivesCount = dashboardService.getBeekeeperHivesCount(beekeeperEmail);
    double payment = beekeeper.getPaymentDue();

    // Controllo della validità del formato del nickname dell'arnia
    if (isNicknameFormatInvalid(nickname)) {
      model.addAttribute("error","Invalid nickname format.");
      return "hive/creation-hive";
    }

    // Controllo della lunghezza del nickname dell'arnia
    if (isNicknameLenghtTooShort(nickname)) {
      model.addAttribute("error","Insufficient nickname length.");
      return "hive/creation-hive";
    }
    if (isNicknameLenghtTooLong(nickname)) {
      model.addAttribute("error","Nickname length too long.");
      return "hive/creation-hive";
    }

    // Controllo della validità del tipo d'arnia
    if(isHiveTypeInvalid(hiveType)) {
      model.addAttribute("error","Invalid hive type.");
      return "hive/creation-hive";
    }

    // Controllo della validità della specie d'api dell'arnia
    if(isBeeSpeciesInvalid(beeSpecies)) {
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

  @GetMapping("/modify-hive")
  public String modifyHive(@RequestParam String hiveId, @RequestParam String nickname,
                           @RequestParam String hiveType, @RequestParam String beeSpecies,
                           HttpSession session, Model model) {
    // Controllo della validità dell'ID dell'arnia
    if (!hiveId.matches("//d+") && Integer.parseInt(hiveId) <= 0) {
      return "errors/error500";
    }
    int intHiveId = Integer.parseInt(hiveId);

    // Controllo della validità del formato del nickname dell'arnia
    if (isNicknameFormatInvalid(nickname)) {
      model.addAttribute("error","Invalid nickname format.");
      return "hive/creation-hive";
    }

    // Controllo della lunghezza del nickname dell'arnia
    if (isNicknameLenghtTooShort(nickname)) {
      model.addAttribute("error","Insufficient nickname length.");
      return "hive/creation-hive";
    }
    if (isNicknameLenghtTooLong(nickname)) {
      model.addAttribute("error","Nickname too long.");
      return "hive/creation-hive";
    }

    // Controllo della validità del tipo d'arnia
    if(isHiveTypeInvalid(hiveType)) {
      model.addAttribute("error","Invalid hive type.");
      return "hive/creation-hive";
    }

    // Controllo della validità della specie d'api dell'arnia
    if(isBeeSpeciesInvalid(beeSpecies)) {
      model.addAttribute("error","Invalid bee species.");
      return "hive/creation-hive";
    }

    // Creazione dell'arnia
    dashboardService.modifyHive(intHiveId, nickname, hiveType, beeSpecies);

    // Ottenimento di tutte le arnie registrate dall'apicoltore, inclusa quella appena inserita
    List<Hive> hives = dashboardService.getBeekeeperHives(
        ((Beekeeper) session.getAttribute("beekeeper")).getEmail());
    // Passaggio della lista di arnie
    model.addAttribute("hives", hives);

    // Redirect alla dashboard aggiornata
    return "hive/dashboard";
  }

  @GetMapping("/state-hive")
  public String showHive(@RequestParam String hiveId, Model model) {
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

  @GetMapping("/delete-hive")
  public String deleteHive(@RequestParam String hiveId, HttpSession session, Model model) {
    // Controllo della validità dell'ID dell'arnia
    if (!hiveId.matches("//d+") && Integer.parseInt(hiveId) <= 0) {
      return "errors/error500";
    }
    int intHiveId = Integer.parseInt(hiveId);

    // Eliminazione dell'arnia
    dashboardService.deleteHive(intHiveId);

    // Ottenimento di tutte le arnie registrate dall'apicoltore, eccetto quella appena eliminata
    List<Hive> hives = dashboardService.getBeekeeperHives(
        ((Beekeeper) session.getAttribute("beekeeper")).getEmail());
    // Passaggio della lista di arnie
    model.addAttribute("hives", hives);

    // Redirect alla dashboard aggiornata
    return "hive/dashboard";
  }
}
