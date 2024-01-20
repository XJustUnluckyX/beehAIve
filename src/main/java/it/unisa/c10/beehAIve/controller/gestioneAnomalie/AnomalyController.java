package it.unisa.c10.beehAIve.controller.gestioneAnomalie;

import it.unisa.c10.beehAIve.persistence.entities.Anomaly;
import it.unisa.c10.beehAIve.persistence.entities.Beekeeper;
import it.unisa.c10.beehAIve.persistence.entities.Operation;
import it.unisa.c10.beehAIve.service.gestioneAnomalie.AnomalyService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;


// Gestisce la comunicazione con l'adapter per il modello e i vari controlli arbitari sulla misurazione, ovvero
// temperatura, umidità, peso, presenza regina
// e invia l'anomalia al database
@Controller
public class AnomalyController {

  // TODO: Do we need this?
  private AnomalyService anomalyService;

  @Autowired
  public AnomalyController(AnomalyService anomalyService) {
    this.anomalyService = anomalyService;
  }

  // Bisogna fare il controllo che l'anomalia sia effettivamente di un'arnia dell'apicoltore
  private boolean checkHiveOwnership (HttpSession session, int anomalyId) {

    Anomaly anomaly = anomalyService.getAnomaly(anomalyId);

    String beekeeperEmail = ((Beekeeper) session.getAttribute("beekeeper")).getEmail();

    return anomaly.getBeekeeperEmail().equals(beekeeperEmail);

  }

  // Cancella un'anomalia
  @GetMapping("/delete_anomaly")
  @ResponseBody
  public String deleteAnomaly (@RequestParam int anomalyId, HttpSession session) {

    if (checkHiveOwnership(session, anomalyId))
      throw new RuntimeException();

    anomalyService.deleteAnomaly(anomalyId);

    return "success";
  }

  // Risolve un'anomalia
  @GetMapping("/resolve_anomaly")
  @ResponseBody
  public String resolveAnomaly (@RequestParam int anomalyId, HttpSession session) {

    if (checkHiveOwnership(session, anomalyId))
      throw new RuntimeException();

    anomalyService.resolveAnomaly(anomalyId);

    return "success";
  }




}