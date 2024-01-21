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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

  // Controllo sulla coerenza tra id dell'arnia e beekeeper
  private boolean isNotConsistentBetweenHiveIdAndBeekeeperEmail(int hiveId, String beekeeperEmail) {
    // Ottengo l'arnia
    Hive hive = dashboardService.getHive(hiveId);

    return !hive.getBeekeeperEmail().equals(beekeeperEmail);
  }

  @GetMapping("/create-hive")
  public String createHive(@RequestParam String nickname, @RequestParam String hiveType,
                           @RequestParam String beeSpecies, HttpSession session, RedirectAttributes redirectAttributes) {
    Beekeeper beekeeper = (Beekeeper) session.getAttribute("beekeeper");
    String beekeeperEmail = beekeeper.getEmail();
    int hivesCount = dashboardService.getBeekeeperHivesCount(beekeeperEmail);
    double payment = beekeeper.getPaymentDue();

    // Controllo della lunghezza del nickname dell'arnia
    if (isNicknameLenghtTooShort(nickname)) {
      redirectAttributes.addFlashAttribute("error","Nickname too short.");
      return "redirect:/show-hive-creation";
    }
    if (isNicknameLenghtTooLong(nickname)) {
      redirectAttributes.addFlashAttribute("error","Nickname too long.");
      return "redirect:/show-hive-creation";
    }

    // Controllo della validità del formato del nickname dell'arnia
    if (isNicknameFormatInvalid(nickname)) {
      redirectAttributes.addFlashAttribute("error","Invalid nickname format.");
      return "redirect:/show-hive-creation";
    }

    // Controllo della validità del tipo d'arnia
    if(isHiveTypeInvalid(hiveType)) {
      redirectAttributes.addFlashAttribute("error","Invalid hive type.");
      return "redirect:/show-hive-creation";
    }

    // Controllo della validità della specie d'api dell'arnia
    if(isBeeSpeciesInvalid(beeSpecies)) {
      redirectAttributes.addFlashAttribute("error","Invalid bee species.");
      return "redirect:/show-hive-creation";
    }

    // Controllo del rispetto del limite del numero di arnie in base all'abbonamento
    if ((payment == 50 && hivesCount >= 15)
     || (payment == 350 && hivesCount >= 100)
     || (payment == 1050 && hivesCount >= 300)) {
      redirectAttributes.addFlashAttribute("error",
          "You've reached the maximum number of hives!");
      // Redirect alla dashboard con errore
      return "redirect:/show-hive-creation";
    }

    // Creazione dell'arnia
    dashboardService.createHive(beekeeperEmail, nickname, hiveType, beeSpecies);

    // Redirect alla dashboard aggiornata
    return "redirect:/dashboard";
  }

  @GetMapping("/modify-hive")
  public String modifyHive(@RequestParam String hiveId, @RequestParam String nickname,
                           @RequestParam String hiveType, @RequestParam String beeSpecies,
                           HttpSession session, Model model, RedirectAttributes redirectAttributes) {

    // Controllo della validità dell'ID dell'arnia
    if (!hiveId.matches("^\\d+$") || Integer.parseInt(hiveId) <= 0) {
      throw new RuntimeException();
    }

    int intHiveId = Integer.parseInt(hiveId);

    // Controllo della lunghezza del nickname dell'arnia
    if (isNicknameLenghtTooShort(nickname)) {
      redirectAttributes.addFlashAttribute("error","Nickname too short.");
      return "redirect:/state-hive?hiveId="+ hiveId;
    }
    if (isNicknameLenghtTooLong(nickname)) {
      redirectAttributes.addFlashAttribute("error","Nickname too long.");
      return "redirect:/state-hive?hiveId="+ hiveId;
    }

    // Controllo della validità del formato del nickname dell'arnia
    if (isNicknameFormatInvalid(nickname)) {
      redirectAttributes.addFlashAttribute("error","Invalid nickname format.");
      return "redirect:/state-hive?hiveId="+ hiveId;
    }

    // Controllo della validità del tipo d'arnia
    if(isHiveTypeInvalid(hiveType)) {
      redirectAttributes.addFlashAttribute("error","Invalid hive type.");
      return "redirect:/state-hive?hiveId="+ hiveId;
    }

    // Controllo della validità della specie d'api dell'arnia
    if(isBeeSpeciesInvalid(beeSpecies)) {
      redirectAttributes.addFlashAttribute("error","Invalid bee species.");
      return "redirect:/state-hive?hiveId="+ hiveId;
    }

    // Controllo sulla coerenza tra l'id dell'arnia da modificare e l'email del Beekeeper
    Beekeeper beekeeper = (Beekeeper) session.getAttribute("beekeeper");
    if(isNotConsistentBetweenHiveIdAndBeekeeperEmail(intHiveId, beekeeper.getEmail())) {
      throw new RuntimeException();
    }

    // Modifica dell'arnia
    dashboardService.modifyHive(intHiveId, nickname, hiveType, beeSpecies);

    // Redirect allo stato dell'arnia aggiornato
    return "redirect:state-hive?hiveId=" + hiveId;
  }

  @GetMapping("/state-hive")
  public String showHive(@RequestParam String hiveId, HttpSession session, Model model) {

    // Controllo della validità dell'ID dell'arnia
    if (!hiveId.matches("^\\d+$") || Integer.parseInt(hiveId) <= 0) {
      throw new RuntimeException();
    }

    // Ottenimento dell'arnia
    int intHiveId = Integer.parseInt(hiveId);
    Hive hive = dashboardService.getHive(intHiveId);

    // Controllo sulla coerenza tra l'id dell'arnia da modificare e l'email del Beekeeper
    Beekeeper beekeeper = (Beekeeper) session.getAttribute("beekeeper");
    if(isNotConsistentBetweenHiveIdAndBeekeeperEmail(intHiveId, beekeeper.getEmail())) {
      throw new RuntimeException();
    }

    // Prendiamo tutte le anomalie non risolte dell'arnia
    List<Anomaly> anomalies = anomalyService.getUnresolvedAnomalies(intHiveId);

    // Prediamo la data dell'ultima misurazione dell'arnia
    LocalDateTime lastMeasure = LocalDateTime.from(statusService.getHiveLastMeasurement(intHiveId).getMeasurementDate());

    // Passaggio dell'arnia e delle anomalie
    model.addAttribute("hive", hive);
    model.addAttribute("lastMeasure",lastMeasure);
    model.addAttribute("anomalies", anomalies);

    // Redirect alla pagina relativa all'arnia
    return "hive/state-hive";
  }

  @GetMapping("/delete-hive")
  public String deleteHive(@RequestParam String hiveId, HttpSession session) {

    // Controllo della validità dell'ID dell'arnia
    if (!hiveId.matches("^\\d+$") || Integer.parseInt(hiveId) <= 0) {
      throw new RuntimeException();
    }
    int intHiveId = Integer.parseInt(hiveId);

    // Controllo sulla coerenza tra l'id dell'arnia da modificare e l'email del Beekeeper
    Beekeeper beekeeper = (Beekeeper) session.getAttribute("beekeeper");
    if(isNotConsistentBetweenHiveIdAndBeekeeperEmail(intHiveId, beekeeper.getEmail())) {
      throw new RuntimeException();
    }

    // Eliminazione dell'arnia
    dashboardService.deleteHive(intHiveId);

    // Redirect alla dashboard aggiornata
    return "redirect:/dashboard";
  }


}
