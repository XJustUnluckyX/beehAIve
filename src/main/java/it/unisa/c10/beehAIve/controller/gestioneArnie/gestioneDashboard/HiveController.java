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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

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

  /**
   * Gestisce le richieste GET per reindirizzare l'apicoltore alla pagina di una delle sue arnie.
   * @param hiveId L'ID dell'arnia che vogliamo visualizzare.
   * @param session Un oggetto {@code HttpSession} per ottenere l'oggetto {@code Beekeeper} dalla
   *                sessione, cos&igrave; da verificare che l'arnia specificata sia di propriet&agrave;
   *                dell'apicoltore autenticato.
   * @param redirectAttributes Un oggetto {@code RedirectAttributes} per trasferire messaggi di
   *                           risposta.
   * @param model Un oggetto {@code Model} per aggiungere attributi alla risposta.
   * @return Una stringa che rappresenta un URL di reindirizzamento alla pagina dell'arnia
   *         specificata.
   * @see DashboardService#getHive(int)
   * @see AnomalyService#getUnresolvedAnomalies(int)
   * @see StatusService#getHiveLastMeasurement(int)
   */
  @GetMapping("/hive")
  public String showHive(
      @RequestParam String hiveId,
      HttpSession session,
      RedirectAttributes redirectAttributes,
      Model model) {
    Beekeeper beekeeper = (Beekeeper) session.getAttribute("beekeeper");

    // Controllo sull'iscrizione dell'apicoltore a uno dei piani di abbonamento
    if (!beekeeper.isSubscribed()) {
      redirectAttributes.addFlashAttribute("error",
        "To create and monitor your hives, subscribe to one of our plans first!");
      return "redirect:/user";
    }

    // Controllo della validità dell'ID dell'arnia
    if (!hiveId.matches("^\\d+$") || Integer.parseInt(hiveId) <= 0) {
      throw new RuntimeException();
    }

    // Ottenimento dell'arnia
    int intHiveId = Integer.parseInt(hiveId);
    Hive hive = dashboardService.getHive(intHiveId);

    // Controllo sulla coerenza tra l'ID dell'arnia da modificare e l'email dell'apicoltore
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
    return "hive/hive";
  }

  /**
   * Gestisce le richieste GET per reindirizzare l'apicoltore al form per creare un'arnia.
   * @param nickname Il nickname dell'arnia che vogliamo creare.
   * @param hiveType Il tipo dell'arnia che vogliamo creare.
   * @param beeSpecies La specie d'api ospitata dall'arnia che vogliamo creare.
   * @param session Un oggetto {@code HttpSession} per ottenere l'oggetto {@code Beekeeper} dalla
   *                sessione.
   * @param redirectAttributes Un oggetto {@code RedirectAttributes} per trasferire messaggi di
   *                           risposta.
   * @return Una stringa che rappresenta un URL di reindirizzamento alla dashboard contenente tutte
   *         le arnie dell'apicoltore, inclusa quella creata.
   * @see DashboardService#createHive(String, String, String, String)
   */
  @GetMapping("/create-hive")
  public String createHive(
      @RequestParam String nickname,
      @RequestParam String hiveType,
      @RequestParam String beeSpecies,
      HttpSession session,
      RedirectAttributes redirectAttributes) {
    Beekeeper beekeeper = (Beekeeper) session.getAttribute("beekeeper");

    // Controllo sull'iscrizione dell'apicoltore a uno dei piani di abbonamento
    if (!beekeeper.isSubscribed()) {
      redirectAttributes.addFlashAttribute("error",
          "To create and monitor your hives, subscribe to one of our plans first!");
      return "redirect:/user";
    }

    String beekeeperEmail = beekeeper.getEmail();
    int hivesCount = dashboardService.getBeekeeperHivesCount(beekeeperEmail);
    double payment = beekeeper.getPaymentDue();

    // Controllo della lunghezza del nickname dell'arnia
    if (nickname.length() < 2) {
      redirectAttributes.addFlashAttribute("error","Nickname too short.");
      return "redirect:/new_hive";
    }
    if (nickname.length() > 50) {
      redirectAttributes.addFlashAttribute("error","Nickname too long.");
      return "redirect:/new_hive";
    }

    // Controllo della validità del formato del nickname dell'arnia
    if (isNicknameFormatInvalid(nickname)) {
      redirectAttributes.addFlashAttribute("error","Nickname must contain one or more " +
        "characters, which can be uppercase and lowercase letters, digits, spaces and special" +
        "symbols ( -_()'\"");
      return "redirect:/new_hive";
    }

    // Controllo sulla validità del tipo d'arnia
    if(isHiveTypeInvalid(hiveType)) {
      redirectAttributes.addFlashAttribute("error","Invalid hive type.");
      return "redirect:/new_hive";
    }

    // Controllo sulla validità della specie d'api dell'arnia
    if(isBeeSpeciesInvalid(beeSpecies)) {
      redirectAttributes.addFlashAttribute("error","Invalid bee species.");
      return "redirect:/new_hive";
    }

    // Controllo del rispetto del limite del numero di arnie in base all'abbonamento
    if ((payment == 39.99 && hivesCount >= 15)
     || (payment == 239.99 && hivesCount >= 100)
     || (payment == 709.99 && hivesCount >= 300)) {
      redirectAttributes.addFlashAttribute("error",
          "You've reached the maximum number of hives!");
      // Redirect alla dashboard con errore
      return "redirect:/new_hive";
    }

    // Creazione dell'arnia
    dashboardService.createHive(beekeeperEmail, nickname, hiveType, beeSpecies);

    // Redirect alla dashboard aggiornata
    return "redirect:/dashboard";
  }

  /**
   * Gestisce le richieste GET per permettere all'apicoltore di modificare le informazioni di
   * un'arnia di sua proprietà.
   * @param hiveId L'ID dell'arnia che vogliamo modificare.
   * @param nickname Il nuovo nickname dell'arnia che vogliamo modificare.
   * @param hiveType Il nuovo tipo dell'arnia che vogliamo modificare.
   * @param beeSpecies La nuova specie d'api ospitata dall'arnia che vogliamo modificare.
   * @param session Un oggetto {@code HttpSession} per ottenere l'oggetto {@code Beekeeper} dalla
   *                sessione, cos&igrave; da verificare che l'arnia specificata sia di propriet&agrave;
   *                dell'apicoltore autenticato.
   * @param redirectAttributes Un oggetto {@code RedirectAttributes} per trasferire messaggi di
   *                           risposta.
   * @return Una stringa che rappresenta un URL di reindirizzamento alla pagina dell'arnia
   *         modificata.
   * @see DashboardService#modifyHive(int, String, String, String)
   */
  @GetMapping("/modify-hive")
  public String modifyHive(
      @RequestParam String hiveId,
      @RequestParam String nickname,
      @RequestParam String hiveType,
      @RequestParam String beeSpecies,
      HttpSession session,
      RedirectAttributes redirectAttributes) {
    Beekeeper beekeeper = (Beekeeper) session.getAttribute("beekeeper");

    // Controllo sull'iscrizione dell'apicoltore a uno dei piani di abbonamento
    if (!beekeeper.isSubscribed()) {
      redirectAttributes.addFlashAttribute("error",
          "To create and monitor your hives, subscribe to one of our plans first!");
      return "redirect:/user";
    }

    // Controllo della validità dell'ID dell'arnia
    if (!hiveId.matches("^\\d+$") || Integer.parseInt(hiveId) <= 0) {
      throw new RuntimeException();
    }

    int intHiveId = Integer.parseInt(hiveId);

    // Controllo della lunghezza del nickname dell'arnia
    if (nickname.length() < 2) {
      redirectAttributes.addFlashAttribute("error","Nickname too short.");
      return "redirect:/hive?hiveId="+ hiveId;
    }
    if (nickname.length() > 50) {
      redirectAttributes.addFlashAttribute("error","Nickname too long.");
      return "redirect:/hive?hiveId="+ hiveId;
    }

    // Controllo della validità del formato del nickname dell'arnia
    if (isNicknameFormatInvalid(nickname)) {
      redirectAttributes.addFlashAttribute("error","Nickname must contain one or more " +
        "characters, which can be uppercase and lowercase letters, digits, spaces and special" +
        "symbols ( -_()'\"");
      return "redirect:/hive?hiveId="+ hiveId;
    }

    // Controllo sulla validità del tipo d'arnia
    if(isHiveTypeInvalid(hiveType)) {
      redirectAttributes.addFlashAttribute("error","Invalid hive type.");
      return "redirect:/hive?hiveId="+ hiveId;
    }

    // Controllo sulla validità della specie d'api dell'arnia
    if(isBeeSpeciesInvalid(beeSpecies)) {
      redirectAttributes.addFlashAttribute("error","Invalid bee species.");
      return "redirect:/hive?hiveId="+ hiveId;
    }

    // Controllo sulla coerenza tra l'ID dell'arnia da modificare e l'email dell'apicoltore
    if(isNotConsistentBetweenHiveIdAndBeekeeperEmail(intHiveId, beekeeper.getEmail())) {
      throw new RuntimeException();
    }

    // Modifica dell'arnia
    dashboardService.modifyHive(intHiveId, nickname, hiveType, beeSpecies);

    // Redirect allo stato dell'arnia aggiornato
    return "redirect:/hive?hiveId=" + hiveId;
  }

  /**
   * Gestisce le richieste GET per permettere all'apicoltore di eliminare un'arnia di sua proprietà.
   * @param hiveId L'ID dell'arnia che vogliamo eliminare.
   * @param redirectAttributes Un oggetto {@code RedirectAttributes} per trasferire messaggi di
   *                           risposta.
   * @param session Un oggetto {@code HttpSession} per ottenere l'oggetto {@code Beekeeper} dalla
   *                sessione, cos&igrave; da verificare che l'arnia specificata sia di propriet&agrave;
   *                dell'apicoltore autenticato.
   * @return Una stringa che rappresenta un URL di reindirizzamento alla dashboard contenente tutte
   *         le arnie dell'apicoltore, senza quella eliminata.
   * @see DashboardService#deleteHive(int)
   */
  @GetMapping("/delete-hive")
  public String deleteHive(
      @RequestParam String hiveId, RedirectAttributes redirectAttributes, HttpSession session) {
    Beekeeper beekeeper = (Beekeeper) session.getAttribute("beekeeper");

    // Controllo sull'iscrizione dell'apicoltore a uno dei piani di abbonamento
    if (!beekeeper.isSubscribed()) {
      redirectAttributes.addFlashAttribute("error",
          "To create and monitor your hives, subscribe to one of our plans first!");
      return "redirect:/user";
    }

    // Controllo della validità dell'ID dell'arnia
    if (!hiveId.matches("^\\d+$") || Integer.parseInt(hiveId) <= 0) {
      throw new RuntimeException();
    }
    int intHiveId = Integer.parseInt(hiveId);

    // Controllo sulla coerenza tra l'ID dell'arnia da modificare e l'email dell'apicoltore
    if(isNotConsistentBetweenHiveIdAndBeekeeperEmail(intHiveId, beekeeper.getEmail())) {
      throw new RuntimeException();
    }

    // Eliminazione dell'arnia dal database
    dashboardService.deleteHive(intHiveId);

    // Redirect alla dashboard aggiornata
    return "redirect:/dashboard";
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