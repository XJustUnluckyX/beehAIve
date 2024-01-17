package it.unisa.c10.beehAIve.service.gestioneAnomalie;

import it.unisa.c10.beehAIve.misc.FlaskAdapter;
import it.unisa.c10.beehAIve.persistence.dao.AnomalyDAO;
import it.unisa.c10.beehAIve.persistence.dao.HiveDAO;
import it.unisa.c10.beehAIve.persistence.entities.Anomaly;
import it.unisa.c10.beehAIve.persistence.entities.Hive;
import it.unisa.c10.beehAIve.persistence.entities.Measurement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/*
La seguente classe, dopo aver ricevuto l'input dalla classe SimulateSensorService, deve supportare le seguenti operazioni:
1. Controllare se esiste un'anomalia sul peso dell'arnia.
2. Controllare se esiste un'anomalia sulla temperatura dell'arnia.
3. Controllare se esiste un'anomalia sull'umidità dell'arnia.
4. Controllare se la regina è presente, consultando la rete neurale (quest'ultima sarà un microservizio Flask a parte,
quindi il metodo corrispondente riceverà in input il file audio).
5. Controllare se l'arnia è a rischio CCD, interfacciandosi con l'Adapter.
 */

@Service
public class AnomalyService {

  private FlaskAdapter adapter = new FlaskAdapter();
  private AnomalyDAO anomalyDAO;
  private HiveDAO hiveDAO;

  @Autowired
  public AnomalyService(AnomalyDAO anomalyDAO, HiveDAO hiveDAO) {
    this.anomalyDAO = anomalyDAO;
    this.hiveDAO = hiveDAO;
  }

  public void checkAnomalies (Measurement measurement) {
    Hive hive = hiveDAO.findById(measurement.getHiveId()).get();
    String beekeeperEmail = hive.getBeekeeperEmail();

    List<Anomaly> anomalies = new ArrayList<>();

    Anomaly weightAnomaly = checkHiveWeight(measurement, beekeeperEmail);
    if (weightAnomaly != null)
      anomalies.add(weightAnomaly);

    Anomaly temperatureAnomaly = checkHiveTemperature(measurement, beekeeperEmail);
    if (temperatureAnomaly != null)
      anomalies.add(temperatureAnomaly);

    Anomaly humidityAnomaly = checkHiveHumidity(measurement, beekeeperEmail);
    if (humidityAnomaly != null)
      anomalies.add(humidityAnomaly);

    Anomaly queenAnomaly = checkQueenPresence(measurement, beekeeperEmail);
    if (queenAnomaly != null)
      anomalies.add(queenAnomaly);

    Anomaly ccdAnomaly = predictCCDAnomaly(measurement, beekeeperEmail);
    if (ccdAnomaly != null)
      anomalies.add(ccdAnomaly);

    for (Anomaly a : anomalies)
      anomalyDAO.save(a);

    // TODO usare StatusService per impostare le notifiche

  }

  private Anomaly checkHiveWeight(Measurement measurement, String beekeeperEmail) {

    // Impostiamo i dati di default
    Anomaly anomaly = new Anomaly();
    anomaly.setResolved(false);
    anomaly.setDetectionDate(measurement.getMeasurementDate());
    anomaly.setSensorId(measurement.getSensorId());
    anomaly.setHiveId(measurement.getHiveId());
    anomaly.setBeekeeperEmail(beekeeperEmail);

    if (measurement.getWeight() < 25) // Peso troppo basso
      anomaly.setAnomalyName("Low Weight");
    else if (measurement.getWeight() > 135) // Peso troppo alto
      anomaly.setAnomalyName("High Weight");
    else // Nessuna anomalia
      return null;

    return anomaly;
  }

  private Anomaly checkHiveTemperature(Measurement measurement, String beekeeperEmail) {

    // Impostiamo i dati di default
    Anomaly anomaly = new Anomaly();
    anomaly.setResolved(false);
    anomaly.setDetectionDate(measurement.getMeasurementDate());
    anomaly.setSensorId(measurement.getSensorId());
    anomaly.setHiveId(measurement.getHiveId());
    anomaly.setBeekeeperEmail(beekeeperEmail);

    if (measurement.getTemperature() < 32) // Temperatura troppo bassa
      anomaly.setAnomalyName("Low Temperature");
    else if (measurement.getTemperature() > 37) // Temperatura troppo alta
      anomaly.setAnomalyName("High Temperature");
    else // Nessuna anomalia
      return null;


    return anomaly;
  }

  private Anomaly checkHiveHumidity(Measurement measurement, String beekeeperEmail) {

    // Impostiamo i dati di default
    Anomaly anomaly = new Anomaly();
    anomaly.setResolved(false);
    anomaly.setDetectionDate(measurement.getMeasurementDate());
    anomaly.setSensorId(measurement.getSensorId());
    anomaly.setHiveId(measurement.getHiveId());
    anomaly.setBeekeeperEmail(beekeeperEmail);

    if (measurement.getHumidity() < 18) // Umidità troppo bassa
      anomaly.setAnomalyName("Low Humidity");
    else if (measurement.getHumidity() > 22) // Umidità troppo alta
      anomaly.setAnomalyName("High Humidity");
    else // Nessuna anomalia
      return null;

    return anomaly;
  }

  private Anomaly checkQueenPresence(Measurement measurement, String beekeeperEmail) {

    // Impostiamo i dati di default
    Anomaly anomaly = new Anomaly();
    anomaly.setResolved(false);
    anomaly.setDetectionDate(measurement.getMeasurementDate());
    anomaly.setSensorId(measurement.getSensorId());
    anomaly.setHiveId(measurement.getHiveId());
    anomaly.setBeekeeperEmail(beekeeperEmail);

    if (!measurement.isQueenPresent()) // Regina non presente
      anomaly.setAnomalyName("Queen Not Present");
    else // Nessuna anomalia
      return null;

    return anomaly;
  }

  private Anomaly predictCCDAnomaly (Measurement measurement, String beekeeperEmail) {
    // Controlliamo se è presente il CCD attraverso il modello
    boolean possibleCCD = adapter.predictCCD(measurement.getTemperature(), measurement.getAmbientTemperature(), measurement.getHumidity(), measurement.getAmbientHumidity(), measurement.isQueenPresent());

    // Impostiamo i dati di default
    Anomaly anomaly = new Anomaly();
    anomaly.setResolved(false);
    anomaly.setDetectionDate(measurement.getMeasurementDate());
    anomaly.setSensorId(measurement.getSensorId());
    anomaly.setHiveId(measurement.getHiveId());
    anomaly.setBeekeeperEmail(beekeeperEmail);

    if (possibleCCD) // Possibile CCD
      anomaly.setAnomalyName("Possible CCD");
    else // Nessuna anomalia
      return null;

    return anomaly;
  }

  public String getRandomSpectrogram() {
    return adapter.getRandomSpectrogram();
  }

  public boolean predictQueenPresence (String spectrogram) {
    return adapter.predictQueenPresence(spectrogram);
  }


}
