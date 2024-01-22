package it.unisa.c10.beehAIve.controller.gestioneArnie.gestioneStato;

import com.google.gson.Gson;
import it.unisa.c10.beehAIve.persistence.entities.Beekeeper;
import it.unisa.c10.beehAIve.persistence.entities.Hive;
import it.unisa.c10.beehAIve.persistence.entities.Measurement;
import it.unisa.c10.beehAIve.service.gestioneArnie.DashboardService;
import it.unisa.c10.beehAIve.service.gestioneArnie.StatusService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HiveHealthController {
  private final StatusService statusService;
  private final DashboardService dashboardService;

  @Autowired
  public HiveHealthController(StatusService statusService, DashboardService dashboardService) {
    this.statusService = statusService;
    this.dashboardService = dashboardService;
  }

  /**
   * Gestisce le richieste GET per visualizzare i parametri di un'arnia.
   * @param hiveId L'ID dell'arnia di cui vogliamo visualizzare i parametri.
   * @param session Un oggetto {@code HttpSession} per ottenere l'oggetto {@code Beekeeper}
   *                dalla sessione, cos&igrave; da verificare che l'arnia specificata sia di propriet&agrave;
   *                dell'apicoltore autenticato.
   * @param model Un oggetto {@code Model} per aggiungere attributi alla risposta.
   * @param redirectAttributes Un oggetto {@code RedirectAttributes} per trasferire messaggi di
   *                           risposta.
   * @return Una stringa che rappresenta un URL di reindirizzamento alla pagina che visualizza i
   *         parametri dell'arnia specificata.
   * @see StatusService#getHiveLastMeasurement(int)
   */
  @GetMapping("/parameters")
  public String showHiveParameters(@RequestParam String hiveId, HttpSession session, Model model,
                                   RedirectAttributes redirectAttributes) {
    Beekeeper beekeeper = (Beekeeper) session.getAttribute("beekeeper");

    // Controllo sull'iscrizione dell'apicoltore a uno dei piani di abbonamento
    if (!beekeeper.isSubscribed()) {
      redirectAttributes.addFlashAttribute("error",
        "To create and monitor your hives, subscribe to one of our plans first!");
      return "redirect:/user";
    }

    // Controllo sulla validità dell'ID dell'arnia
    if (!hiveId.matches("^\\d+$") || Integer.parseInt(hiveId) <= 0) {
      throw new RuntimeException();
    }
    int intHiveId = Integer.parseInt(hiveId);

    // Controllo sulla coerenza tra ID dell'arnia ed email dell'apicoltore
    if(isNotConsistentBetweenHiveIdAndBeekeeperEmail(intHiveId, beekeeper.getEmail())) {
      throw new RuntimeException();
    }

    Measurement hiveParameters = statusService.getHiveLastMeasurement(intHiveId);

    Hive hive = dashboardService.getHive(intHiveId);

    model.addAttribute("hive", hive);
    model.addAttribute("hiveParameters", hiveParameters);

    return "hive/parameters-hive";
  }

  /**
   * Gestisce le richieste GET per produrre un grafico che visualizza i dati raccolti dalle
   * misurazioni di un'arnia effettuate nelle ultime 48 ore attraverso AJAX.
   * @param hiveId L'ID dell'arnia di cui vogliamo produrre il grafico.
   * @return Una stringa JSON che rappresenta i dati del grafico per l'arnia specificata.
   * @see StatusService#getGraphData(int)
   */
  @GetMapping("/produce_graph")
  @ResponseBody
  public String produceGraph(@RequestParam int hiveId) {
    List<ArrayList<Object>> measurements = statusService.getGraphData(hiveId);
    Gson gson = new Gson();
    // Convertiamo in formato JSON
    return gson.toJson(measurements);
  }

  /**
   * Gestisce le richieste GET per generare un report di salute in formato PDF di un'arnia e
   * restituirlo come download.
   * @param hiveId L'ID dell'arnia di cui vogliamo generare il report.
   * @param hiveNickname Il nickname dell'arnia di cui vogliamo generare il report, così da
   *                     visualizzarlo nel PDF.
   * @param session Un oggetto {@code HttpSession} per ottenere l'oggetto {@code Beekeeper} dalla
   *                sessione, cos&igrave; da verificare che l'arnia specificata sia di propriet&agrave;
   *                dell'apicoltore autenticato.
   * @param response Un oggetto {@code RedirectAttributes} per trasferire messaggi di risposta.
   * @throws IOException Nel caso in cui si verifichino errori nella gestione dell'output del file
   *                     PDF.
   * @see StatusService#generateReport(int, HttpServletResponse) 
   */
  @GetMapping("/create_report")
  public void generateHealthReport(
      @RequestParam String hiveId,
      @RequestParam String hiveNickname,
      HttpSession session,
      HttpServletResponse response)
      throws IOException {
    // Controllo sulla validità dell'ID dell'arnia
    if (!hiveId.matches("^\\d+$") || Integer.parseInt(hiveId) <= 0) {
      throw new RuntimeException();
    }

    // Controllo sul formato del nickname
    if (!hiveNickname.matches("^[a-zA-Z0-9\\s\\-_()'\"]+$")) {
      throw new RuntimeException();
    }

    int intHiveId = Integer.parseInt(hiveId);
    Beekeeper beekeeper = (Beekeeper) session.getAttribute("beekeeper");
    // Controllo sulla coerenza tra ID dell'arnia ed email dell'apicoltore
    if(isNotConsistentBetweenHiveIdAndBeekeeperEmail(intHiveId, beekeeper.getEmail())) {
      throw new RuntimeException();
    }

    LocalDate today = LocalDate.now();

    response.setContentType("application/pdf");
    String header = "Content-Disposition";
    String headerValue = "attachment; filename=" + hiveNickname + "_Health_Report_" + today + ".pdf";
    response.setHeader(header, headerValue);
    statusService.generateReport(intHiveId, response);
  }

  private boolean isNotConsistentBetweenHiveIdAndBeekeeperEmail(int hiveId, String beekeeperEmail) {
    // Ottenimento dell'arnia l'arnia
    Hive hive = dashboardService.getHive(hiveId);

    // Confronto tra l'email corrispondente all'arnia e l'email dell'apicoltore
    return !hive.getBeekeeperEmail().equals(beekeeperEmail);
  }

}
