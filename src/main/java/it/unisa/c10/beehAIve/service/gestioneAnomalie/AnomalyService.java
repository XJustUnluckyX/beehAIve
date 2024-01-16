package it.unisa.c10.beehAIve.service.gestioneAnomalie;

import it.unisa.c10.beehAIve.misc.FlaskAdapter;
import it.unisa.c10.beehAIve.persistence.dao.AnomalyDAO;
import it.unisa.c10.beehAIve.persistence.entities.Anomaly;
import it.unisa.c10.beehAIve.persistence.entities.Measurement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/*
La seguente classe, dopo aver ricevuto l'input dalla classe SimulateSensorService, deve supportare le seguenti operazioni:
1. Controllare se esiste un'anomalia sul peso dell'arnia.
2. Controllare se esiste un'anomalia sulla temperatura dell'arnia.
3. Controllare se esiste un'anomalia sull'umidità dell'arnia.
4. Controllare se la regina è presente, consultando la rete neurale (quest'ultima sarà un microservizio Flask a parte,
quindi il metodo corrispondente riceverà in input il file audio). TODO spostare qua il codice
5. Controllare se l'arnia è a rischio CCD, interfacciandosi con l'Adapter.
 */

@Service
public class AnomalyService {

  private FlaskAdapter adapter = new FlaskAdapter();
  private AnomalyDAO anomalyDAO;
  @Autowired
  public AnomalyService(AnomalyDAO anomalyDAO) {
    this.anomalyDAO = anomalyDAO;
  }

  public Anomaly checkHiveWeight(Measurement measurement) {
    if (measurement.getWeight() < 25) {
      //Create anomaly and send to db
    } else if (measurement.getWeight() > 135) {
      //Create anomaly and send to db
    } else {
      return null;
    }

    return null;

  }

  public Anomaly checkHiveTemperature(Measurement measurement) {
    if (measurement.getTemperature() < 32) {
      //Create anomaly and send to db
    } else if (measurement.getTemperature() > 37) {
      //Create anomaly and send to db
    } else {
      return null;
    }

    return null;

  }

  public Anomaly checkHiveHumidity(Measurement measurement) {
    if (measurement.getHumidity() < 32) {
      //Create anomaly and send to db
    } else if (measurement.getHumidity() > 37) {
      //Create anomaly and send to db
    } else {
      return null;
    }

    return null;

  }

  public Anomaly checkQueenPresence(Measurement measurement) {
    if (!measurement.isQueenPresent()) {
      //Create Anomaly
    } else {
      return null;
    }

    return null;

  }



  public boolean predictQueenPresence (String spectrogram) {
    return adapter.predictQueenPresence(spectrogram);
  }

  public Anomaly predictCCDPresence (Measurement measurement) {
    boolean CCD = adapter.predictCCD(measurement.getTemperature(), measurement.getAmbientTemperature(), measurement.getHumidity(), measurement.getAmbientHumidity(), measurement.isQueenPresent());
    if (CCD) {
      //Create Anomaly
    } else {
      return null;
    }

    return null;

  }




}
