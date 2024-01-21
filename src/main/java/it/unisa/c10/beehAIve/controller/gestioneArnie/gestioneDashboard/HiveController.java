package it.unisa.c10.beehAIve.controller.gestioneArnie.gestioneDashboard;

import it.unisa.c10.beehAIve.persistence.entities.Anomaly;
import it.unisa.c10.beehAIve.persistence.entities.Beekeeper;
import it.unisa.c10.beehAIve.persistence.entities.Hive;
import it.unisa.c10.beehAIve.service.gestioneAnomalie.AnomalyService;
import it.unisa.c10.beehAIve.service.gestioneArnie.DashboardService;
import it.unisa.c10.beehAIve.service.gestioneArnie.StatusService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

// Gestisce Creazione, Modifica, Cancellazione e Visualizzazione di una singola arnia (CRUD)

@Controller
public class HiveController {
  private final DashboardService dashboardService;

  private final AnomalyService anomalyService;

  private final StatusService statusService;

  @Autowired
  public HiveController(DashboardService dashboardService, AnomalyService anomalyService,
                        StatusService statusService) {
    this.dashboardService = dashboardService;
    this.anomalyService = anomalyService;
    this.statusService = statusService;
  }

  @GetMapping("/create-hive")
  public String createHive(@RequestParam String nickname, @RequestParam String hiveType,
                           @RequestParam String beeSpecies, HttpSession session, Model model) {
    Beekeeper beekeeper = (Beekeeper) session.getAttribute("beekeeper");
    String beekeeperEmail = beekeeper.getEmail();
    int hivesCount = dashboardService.getBeekeeperHivesCount(beekeeperEmail);
    double payment = beekeeper.getPaymentDue();

    // Controllo sulla validità del formato del nickname dell'arnia
    if (isNicknameFormatInvalid(nickname)) {
      model.addAttribute("error","Nickname must contain one or more " +
          "characters, which can be uppercase and lowercase letters, digits, spaces and special" +
          "symbols ( -_()'\"");
      return "hive/creation-hive";
    }
    // Controllo sulla lunghezza del nickname dell'arnia
    if (nickname.length() < 2) {
      model.addAttribute("error","Nickname too short.");
      return "hive/creation-hive";
    }
    if (nickname.length() > 50) {
      model.addAttribute("error","Nickname too long.");
      return "hive/creation-hive";
    }

    // Controllo sulla validità del tipo d'arnia
    if(isHiveTypeInvalid(hiveType)) {
      model.addAttribute("error","Invalid hive type.");
      return "hive/creation-hive";
    }

    // Controllo sulla validità della specie d'api dell'arnia
    if(isBeeSpeciesInvalid(beeSpecies)) {
      model.addAttribute("error","Invalid bee species.");
      return "hive/creation-hive";
    }

    // Controllo del rispetto del limite del numero di arnie in base all'abbonamento
    if ((payment == 50 && hivesCount >= 15) ||
        (payment == 350 && hivesCount >= 100) ||
        (payment == 1050 && hivesCount >= 300)) {
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
    // Controllo sulla validità dell'ID dell'arnia
    if (!hiveId.matches("^[\\d]+$") && Integer.parseInt(hiveId) <= 0) {
      return "error/500";
    }
    int intHiveId = Integer.parseInt(hiveId);

    // Controllo sulla validità del formato del nickname dell'arnia
    if (isNicknameFormatInvalid(nickname)) {
      model.addAttribute("error","Nickname must contain one or more " +
          "characters, which can be uppercase and lowercase letters, digits, spaces and special" +
          "symbols ( -_()'\"");
      return "redirect:state-hive?hiveId="+ hiveId;
    }
    // Controllo sulla lunghezza del nickname dell'arnia
    if (nickname.length() < 2) {
      model.addAttribute("error","Nickname too short.");
      return "redirect:state-hive?hiveId="+ hiveId;
    }
    if (nickname.length() > 50) {
      model.addAttribute("error","Nickname too long.");
      return "redirect:state-hive?hiveId="+ hiveId;
    }

    // Controllo sulla validità del tipo d'arnia
    if(isHiveTypeInvalid(hiveType)) {
      model.addAttribute("error","Invalid hive type.");
      return "redirect:state-hive?hiveId="+ hiveId;
    }

    // Controllo sulla validità della specie d'api dell'arnia
    if(isBeeSpeciesInvalid(beeSpecies)) {
      model.addAttribute("error","Invalid bee species.");
      return "redirect:state-hive?hiveId="+ hiveId;
    }

    // Controllo sulla coerenza tra l'ID dell'arnia da modificare e l'email dell'apicoltore
    Beekeeper beekeeper = (Beekeeper) session.getAttribute("beekeeper");
    if(isNotConsistentBetweenHiveIdAndBeekeeperEmail(intHiveId, beekeeper.getEmail())) {
      return "error/500";
    }

    // Creazione dell'arnia
    dashboardService.modifyHive(intHiveId, nickname, hiveType, beeSpecies);

    // Ottenimento di tutte le arnie registrate dall'apicoltore, inclusa quella appena inserita
    List<Hive> hives = dashboardService.getBeekeeperHives(
      ((Beekeeper) session.getAttribute("beekeeper")).getEmail());
    // Passaggio della lista di arnie
    model.addAttribute("hives", hives);

    // Redirect alla dashboard aggiornata
    return "redirect:state-hive?hiveId="+ hiveId;
  }

  @GetMapping("/state-hive")
  public String showHive(@RequestParam String hiveId, HttpSession session, Model model) {
    // Controllo della validità dell'ID dell'arnia
    if (!hiveId.matches("^[\\d]+$") && Integer.parseInt(hiveId) <= 0) {
      return "error/500";
    }

    int intHiveId = Integer.parseInt(hiveId);

    // Ottenimento dell'arnia
    Hive hive = dashboardService.getHive(intHiveId);
    // Ottenimento di tutte le anomalie non risolte dell'arnia
    List<Anomaly> anomalies = anomalyService.getUnresolvedAnomalies(intHiveId);
    // Ottenimento della data dell'ultima misurazione dell'arnia
    LocalDate lastMeasure = LocalDate.from(statusService.getHiveLastMeasurement(intHiveId)
        .getMeasurementDate());

    // Controllo sulla coerenza tra l'ID dell'arnia da modificare e l'email dell'apicoltore
    Beekeeper beekeeper = (Beekeeper) session.getAttribute("beekeeper");
    if(isNotConsistentBetweenHiveIdAndBeekeeperEmail(intHiveId, beekeeper.getEmail())) {
      return "error/500";
    }

    // Passaggio dell'arnia e delle anomalie
    model.addAttribute("hive", hive);
    model.addAttribute("lastMeasure",lastMeasure);
    model.addAttribute("anomalies", anomalies);

    // Redirect alla pagina relativa all'arnia
    return "hive/state-hive";
  }

  @GetMapping("/delete-hive")
  public String deleteHive(@RequestParam String hiveId, HttpSession session, Model model) {
    // Controllo della validità dell'ID dell'arnia
    if (!hiveId.matches("^[\\d]+$") && Integer.parseInt(hiveId) <= 0) {
      return "error/500";
    }
    int intHiveId = Integer.parseInt(hiveId);

    // Controllo sulla coerenza tra l'ID dell'arnia da modificare e l'email dell'apicoltore
    Beekeeper beekeeper = (Beekeeper) session.getAttribute("beekeeper");
    if(isNotConsistentBetweenHiveIdAndBeekeeperEmail(intHiveId, beekeeper.getEmail())) {
      return "error/500";
    }

    // Eliminazione dell'arnia dal database
    dashboardService.deleteHive(intHiveId);

    // Ottenimento di tutte le arnie registrate dall'apicoltore, eccetto quella appena eliminata
    List<Hive> hives = dashboardService.getBeekeeperHives(
      ((Beekeeper) session.getAttribute("beekeeper")).getEmail());
    // Passaggio della lista di arnie
    model.addAttribute("hives", hives);

    // Redirect alla dashboard aggiornata
    return "hive/dashboard";
  }

  private boolean isNicknameFormatInvalid(String nickname) {
    return !nickname.matches("^[a-zA-Z0-9\\s\\-_()'\"]+$");
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

  // Controllo sulla coerenza tra id dell'arnia e beekeeper
  private boolean isNotConsistentBetweenHiveIdAndBeekeeperEmail(int hiveId, String beekeeperEmail) {
    // Ottengo l'arnia
    Hive hive = dashboardService.getHive(hiveId);

    return !hive.getBeekeeperEmail().equals(beekeeperEmail);
  }
}