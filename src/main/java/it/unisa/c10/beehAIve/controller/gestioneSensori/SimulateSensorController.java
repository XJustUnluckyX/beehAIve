package it.unisa.c10.beehAIve.controller.gestioneSensori;

import it.unisa.c10.beehAIve.service.gestioneSensori.SimulateSensorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

//Gestisce la simulazione delle misurazioni dei sensori IoT attraverso un algoritmo pseudocasuale che prende in
//input la data e l'ultima misurazione effettuata.
//Inoltre gestisce eventuali altre operazioni su questo processo come il salvataggio nel db
//Inoltre si interfaccia con l'Anomaly Controller per processare la misurazione
@Controller
public class SimulateSensorController {

  private SimulateSensorService sensorService;

  @Autowired
  public SimulateSensorController (SimulateSensorService sensorService) {
    this.sensorService = sensorService;
  }

  // (cron = 0 0 * * * *) significa ogni ora di ogni giorno alle x:00
  // (cron = */10 * * * * *) significa ogni 10 secondi
  @Scheduled(cron = "0 0 * * * *")
  public void initializeSensors() {
    sensorService.simulateMeasurements();
  }

}
