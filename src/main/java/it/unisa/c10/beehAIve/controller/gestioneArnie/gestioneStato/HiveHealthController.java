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

  // Metodo Ajax per produrre il grafico
  @GetMapping("/produce_graph")
  @ResponseBody
  public String produceGraph(@RequestParam int hiveId) {
    List<ArrayList<Object>> measurements = statusService.getGraphData(hiveId);
    Gson gson = new Gson();
    // Convertiamo in formato JSON
    return gson.toJson(measurements);
  }

  // Chiamata Ajax per scaricare il report di salute
  @GetMapping("/create_report")
  public void generateHealthReport(@RequestParam String hiveId, @RequestParam String hiveNickname,
                                   HttpSession session, HttpServletResponse response)
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
