package it.unisa.c10.beehAIve.controller.gestioneSensori;

import it.unisa.c10.beehAIve.service.gestioneSensori.SimulateSensorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

@Controller
public class SimulateSensorController {
  private SimulateSensorService sensorService;

  @Autowired
  public SimulateSensorController (SimulateSensorService sensorService) {
    this.sensorService = sensorService;
  }

  // (cron = 0 0 * * * *) significa ogni ora di ogni giorno alle xx:00
  // (cron = */10 * * * * *) significa ogni 10 secondi
  @Scheduled(cron = "0 0 * * * *")
  public void initializeSensors() {
    sensorService.simulateMeasurements();
  }
}
