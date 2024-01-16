package it.unisa.c10.beehAIve.service.gestioneSensori;

import it.unisa.c10.beehAIve.persistence.dao.HiveDAO;
import it.unisa.c10.beehAIve.persistence.dao.MeasurementDAO;
import it.unisa.c10.beehAIve.persistence.entities.Hive;
import it.unisa.c10.beehAIve.persistence.entities.Measurement;
import it.unisa.c10.beehAIve.service.gestioneAnomalie.AnomalyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

/*
  private double weight;
*/

/*
* Codice Humidex
* double vaporTension = 6.112 * Math.pow(10, ((7.5 * hiveTemp)/(237.7 + hiveTemp))) * (hiveHumidity / 100.0);
* double apparentTemp = hiveTemp + (5.0/9.0 * (vaporTension - 10));
* */

/*
La seguente classe deve supportare le seguenti operazioni:
1. Simulare la misurazione dell'arnia.
2. Caricare la misurazione dell'arnia al database.
3. Passare la misurazione alla classe AnomalyController.
 */
@Service
public class SimulateSensorService {

  private HiveDAO hiveDAO;
  private MeasurementDAO measurementDAO;
  private AnomalyService anomalyService;

  @Autowired
  public SimulateSensorService(HiveDAO hiveDAO, MeasurementDAO measurementDAO, AnomalyService anomalyService) {
    this.hiveDAO = hiveDAO;
    this.measurementDAO = measurementDAO;
    this.anomalyService = anomalyService;
  }

  public void simulateMeasurements() {

    //La prima misurazione partirà SEMPRE alle 12 con valori ottimi

    // Prendiamo tutte le Arnie
    List<Hive> allHives = hiveDAO.findAll();

    for (Hive h : allHives) {
      // Crea la nuova misurazione
      Measurement newMeasurement = new Measurement();

      List<Measurement> measurements = measurementDAO.findByHiveId(h.getId());
      Measurement lastMeasurement = measurements.get(measurements.size()-1); //TODO Mock method to get last Measurement in a hive

      // Imposta la nuova data
      LocalDateTime newDate = lastMeasurement.getMeasurementDate().plusHours(1);
      newMeasurement.setMeasurementDate(newDate);

      // Prende l'ID del sensore
      int newSensorID = lastMeasurement.getSensorId();
      newMeasurement.setSensorId(newSensorID);

      // Prende l'ID dell'Arnia
      int newHiveID = lastMeasurement.getHiveId();
      newMeasurement.setHiveId(newHiveID);

      // Prende l'ora per fare i calcoli della temperatura
      int hour = newDate.getHour();
      // Generiamo la nuova temperatura ambientale
      double lastAmbientTemp = lastMeasurement.getAmbientTemperature();
      double newAmbientTemp = simulateAmbientTemp(lastAmbientTemp, hour);
      newMeasurement.setAmbientTemperature(newAmbientTemp);

      // Generiamo la nuova temperatura dell'arnia
      double lastHiveTemp = lastMeasurement.getTemperature();
      double newHiveTemp = simulateHiveTemp(lastHiveTemp);
      newMeasurement.setTemperature(newHiveTemp);

      // Generiamo la nuova umidità ambientale
      double lastAmbientHumidity = lastMeasurement.getAmbientHumidity();
      double newAmbientHumidity = simulateHumidity(lastAmbientHumidity);
      newMeasurement.setAmbientHumidity(newAmbientHumidity);

      // Generiamo la nuova umidità dell'arnia
      double lastHiveHumidity = lastMeasurement.getHumidity();
      double newHiveHumidity = simulateHumidity(lastHiveHumidity);
      newMeasurement.setHumidity(newHiveHumidity);

      // Generiamo un nuovo spettrogramma casuale associato alla misurazione
      String newSpectrogram = simulateSpectrogram();
      newMeasurement.setSpectrogram(newSpectrogram);

      // Controlliamo se la regina è presente attraverso l'utilizzo della CNN
      boolean newPresentQueen = anomalyService.predictQueenPresence(newSpectrogram);
      newMeasurement.setQueenPresent(newPresentQueen);

      // Generiamo il nuovo peso dell'arnia
      double lastWeight = lastMeasurement.getWeight();
      double newWeight = simulateWeight(lastWeight);
      newMeasurement.setWeight(newWeight);

      measurementDAO.save(newMeasurement);

      // Manda al controllo anomalie la misurazione
      // TODO

    }
  }

  private double simulateAmbientTemp(double temperature, int hour) {

    boolean isMorning = false;
    boolean isDay = false;
    boolean isNight = false;

    // Definiamo l'orario della nuova misurazione per perturbare la temperatura
    if (hour >= 6 && hour <= 12) // Dalle 06:00 alle 12:00
      isMorning = true;
    else if (hour >= 13 && hour <= 19) //Dalle 13:00 alle 20:00
      isDay = true;
    else if (hour >= 20 || hour <= 5)  //Dalle 20:00 alle 5:00
      isNight = true;

    // Generiamo la nuova temperatura ambientale basandoci sull'ora del giorno
    if (isMorning) {
      double randomTemp = 0.5 + (Math.random() * 1); // Numero tra 0.5 e 1.5
      temperature += Math.round(randomTemp * 100.0) / 100.0;
    } else if (isDay) {
      double randomTemp = 1 + (Math.random() * 1); // Numero tra 1 e 2
      temperature += Math.round(randomTemp * 100.0) / 100.0;
    } else if (isNight) {
      double randomTemp = -3 + (Math.random() * 2); // Numero tra -1 e -3
      temperature += Math.round(randomTemp * 100.0) / 100.0;
    }

    return temperature;
  }
  private double simulateHiveTemp(double temperature) {
    if (temperature < 32) { //Anomalia, temperatura troppo bassa
      if (Math.random() > 0.80) { //80% di probabilità di sistemare la temperatura
        double randomIncrement = 0.5 + (Math.random() * 1); // Numero tra 0.5 e 1.5
        temperature += Math.round(randomIncrement * 100.0) / 100.0;
      } else { //20% di probabilità di peggiorarla ulteriormente
        double randomDecrement = -1.5 + (Math.random() * 1); // Numero tra -1.5 e -0.5
        temperature += Math.round(randomDecrement * 100.0) / 100.0;
      }
    } else if (temperature > 37) { //Anomalia, temperatura troppo alta
      if (Math.random() > 0.80) { //80% di probabilità di sistemare la temperatura
        double randomDecrement = -1.5 + (Math.random() * 1); // Numero tra -1.5 e -0.5
        temperature += Math.round(randomDecrement * 100.0) / 100.0;
      } else { //20% di probabilità di peggiorarla ulteriormente
        double randomIncrement = 0.5 + (Math.random() * 1); // Numero tra 0.5 e 1.5
        temperature += Math.round(randomIncrement * 100.0) / 100.0;
      }
    } else { //Temperatura in range
      if (Math.random() > 0.50) { //50% di probabilità di aumentarla
        double randomIncrement = 1 + (Math.random() * 1); // Numero tra 1 e 2
        temperature += Math.round(randomIncrement * 100.0) / 100.0;
      } else { //50% di probabilità di diminuirla
        double randomDecrement = -2 + (Math.random() * 1); // Numero tra -2 e -1
        temperature += Math.round(randomDecrement * 100.0) / 100.0;
      }
    }
    return temperature;
  }
  private double simulateHumidity(double humidity) {
    // Generiamo la nuova umidità (Tendiamo sempre verso 20)
    if (humidity < 18) {
      if (Math.random() > 0.80) { //80% di probabilità di sistemare l'umidità
        double randomIncrement = 0.5 + (Math.random() * 1); // Numero tra 0.5 e 1.5
        humidity += Math.round(randomIncrement * 100.0) / 100.0;
      } else { //20% di probabilità di peggiorarla ulteriormente
        double randomDecrement = -1.5 + (Math.random() * 1); // Numero tra -1.5 e -0.5
        humidity += Math.round(randomDecrement * 100.0) / 100.0;
      }
    } else if (humidity > 22) {
      if (Math.random() > 0.80) { //80% di probabilità di sistemare l'umidità
        double randomDecrement = -1.5 + (Math.random() * 1); // Numero tra -1.5 e -0.5
        humidity += Math.round(randomDecrement * 100.0) / 100.0;
      } else { //20% di probabilità di peggiorarla ulteriormente
        double randomIncrement = 0.5 + (Math.random() * 1); // Numero tra 0.5 e 1.5
        humidity += Math.round(randomIncrement * 100.0) / 100.0;
      }
    } else { // Umidità in range
      if (Math.random() > 0.50) { //50% di probabilità di aumentarla
        double randomIncrement = 1 + (Math.random() * 1); // Numero tra 1 e 2
        humidity += Math.round(randomIncrement * 100.0) / 100.0;
      } else { //50% di probabilità di diminuirla
        double randomDecrement = -2 + (Math.random() * 1); // Numero tra -2 e -1
        humidity += Math.round(randomDecrement * 100.0) / 100.0;
      }
    }

    return humidity;

  }

  //TODO check if it works on all computers
  private String simulateSpectrogram () {
    // Prende uno spettrogramma casuale dalla cartella

    File dir = new File("src\\ai\\resources\\spectrograms\\");

    File[] spectrogramsList = dir.listFiles();

    Random rand = new Random();

    File spectrogram = spectrogramsList[rand.nextInt(spectrogramsList.length)];

    return spectrogram.getName();
  }
  private double simulateWeight(double weight) {
    if (weight < 25) { //Anomalia, peso troppo basso
      if (Math.random() > 0.80) { //80% di probabilità di sistemare il peso
        double randomIncrement = 10 + (Math.random() * 20); // Numero tra 10 e 30
        weight += Math.round(randomIncrement * 100.0) / 100.0;
      } else { //20% di probabilità di peggiorarlo ulteriormente
        double randomDecrement = -2 + (Math.random() * 1); // Numero tra -2 e -1
        weight += Math.round(randomDecrement * 100.0) / 100.0;
      }
    } else if (weight > 135) { //Anomalia, peso troppo alta
      if (Math.random() > 0.80) { //80% di probabilità di sistemare il peso
        double randomDecrement = -30 + (Math.random() * 20); // Numero tra -30 e -10
        weight += Math.round(randomDecrement * 100.0) / 100.0;
      } else { //20% di probabilità di peggiorarlo ulteriormente
        double randomIncrement = 1 + (Math.random() * 1); // Numero tra 1 e 2
        weight += Math.round(randomIncrement * 100.0) / 100.0;
      }
    } else { //Peso in range
      if (Math.random() > 0.50) { //50% di probabilità di aumentarlo
        double randomIncrement = 10 + (Math.random() * 20); // Numero tra 10 e 30
        weight += Math.round(randomIncrement * 100.0) / 100.0;
      } else { //50% di probabilità di diminuirlo
        double randomDecrement = -30 + (Math.random() * 20); // Numero tra -30 e -10
        weight += Math.round(randomDecrement * 100.0) / 100.0;
      }
    }
    return weight;
  }

}
