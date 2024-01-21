package it.unisa.c10.beehAIve.controller.gestioneArnie.gestioneStato;

import com.google.gson.Gson;
import it.unisa.c10.beehAIve.persistence.entities.Hive;
import it.unisa.c10.beehAIve.persistence.entities.Measurement;
import it.unisa.c10.beehAIve.service.gestioneArnie.DashboardService;
import it.unisa.c10.beehAIve.service.gestioneArnie.StatusService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HiveHealthController {
  private StatusService statusService;
  private DashboardService dashboardService;

  @Autowired
  public HiveHealthController(StatusService statusService, DashboardService dashboardService) {
    this.statusService = statusService;
    this.dashboardService = dashboardService;
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

  @GetMapping("/create_report")
  public void generateHealthReport(@RequestParam String hiveId, @RequestParam String hiveNickname,
                                   HttpServletResponse response) throws IOException {
    // TODO: Controlli
    response.setContentType("application/pdf");
    String header = "Content-Disposition";
    String headerValue = "attachment; filename=" + hiveNickname + "_Health_Report.pdf";
    response.setHeader(header, headerValue);
    statusService.generateReport(Integer.parseInt(hiveId), response);
  }

  @GetMapping("/parameters-redirect-form")
  public String showHiveParameters(@RequestParam String hiveId, Model model) {
    if (!hiveId.matches("^[\\d]+$") && Integer.parseInt(hiveId) <= 0) {
      return "error/500";
    }
    int intHiveId = Integer.parseInt(hiveId);

    Measurement hiveParameters = statusService.getHiveLastMeasurement(intHiveId);

    // TODO: Capire perché è necessario passare l'arnia nel model
    Hive hive = dashboardService.getHive(intHiveId);
    model.addAttribute("hive", hive);
    model.addAttribute("hiveParameters", hiveParameters);

    return "hive/parameters-hive";
  }
}
