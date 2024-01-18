package it.unisa.c10.beehAIve.controller.gestioneArnie.gestioneStato;

import com.google.gson.Gson;
import it.unisa.c10.beehAIve.service.gestioneArnie.StatusService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Gestisce la visualizzazione dei grafici delle misurazioni di un'Arnia in particolare di
// Temperatura, umidità, peso, presenza regina
// E permette di produrre e scaricare un report di salute dell'arnia che include i grafici e lo storico di
// interventi, anomalie
@Controller
public class HiveHealthController {

  private StatusService statusService;
  @Autowired
  public HiveHealthController(StatusService statusService) {
    this.statusService = statusService;
  }

  @GetMapping("/graph-test") //TODO remove
  public String graphTest () {
    return "graph-test";
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

  @GetMapping("/create_report") //TODO hive id
  public void generateHealthReport(HttpServletResponse response) throws IOException {
    response.setContentType("application/pdf");
    String header = "Content-Disposition";
    String headerValue = "attachment; filename=test.pdf"; //TODO change file name
    response.setHeader(header, headerValue);
    statusService.generateReport(1, response);
  }

}
