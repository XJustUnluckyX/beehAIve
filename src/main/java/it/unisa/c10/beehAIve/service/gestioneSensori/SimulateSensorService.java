package it.unisa.c10.beehAIve.service.gestioneSensori;

import it.unisa.c10.beehAIve.persistence.dao.HiveDAO;
import it.unisa.c10.beehAIve.persistence.dao.MeasurementDAO;
import it.unisa.c10.beehAIve.persistence.entities.Hive;
import it.unisa.c10.beehAIve.persistence.entities.Measurement;
import it.unisa.c10.beehAIve.service.gestioneAnomalie.AnomalyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

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

    // Prendiamo tutte le Arnie
    List<Hive> allHives = hiveDAO.findAll();

    for (Hive h : allHives) {
      // Crea la nuova misurazione
      Measurement newMeasurement = new Measurement();

      // Prende l'ultima misurazione dell'arnia
      Measurement lastMeasurement = measurementDAO.findTopByHiveIdOrderByMeasurementDateDesc(h.getId());

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
      String newSpectrogram = anomalyService.getRandomSpectrogram();
      newMeasurement.setSpectrogram(newSpectrogram);

      // Controlliamo se la regina è presente attraverso l'utilizzo della CNN
      boolean newPresentQueen = anomalyService.predictQueenPresence(newSpectrogram);
      newMeasurement.setQueenPresent(newPresentQueen);

      // Generiamo il nuovo peso dell'arnia
      double lastWeight = lastMeasurement.getWeight();
      double newWeight = simulateWeight(lastWeight);
      newMeasurement.setWeight(newWeight);

      // Invia la misurazione al database
      measurementDAO.save(newMeasurement);

      // Manda al controllo anomalie la misurazione per diagnosticare eventuali problemi
      anomalyService.checkAnomalies(newMeasurement);

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

    return formatNumber(temperature);
  }
  private double simulateHiveTemp(double temperature) {
    if (temperature < 32) { //Anomalia, temperatura troppo bassa
      if (Math.random() > 0.20) { //80% di probabilità di sistemare la temperatura
        double randomIncrement = 0.5 + (Math.random() * 1); // Numero tra 0.5 e 1.5
        temperature += Math.round(randomIncrement * 100.0) / 100.0;
      } else { //20% di probabilità di peggiorarla ulteriormente
        double randomDecrement = -1.5 + (Math.random() * 1); // Numero tra -1.5 e -0.5
        temperature += Math.round(randomDecrement * 100.0) / 100.0;
      }
    } else if (temperature > 37) { //Anomalia, temperatura troppo alta
      if (Math.random() > 0.20) { //80% di probabilità di sistemare la temperatura
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
    return formatNumber(temperature);
  }
  private double simulateHumidity(double humidity) {
    // Generiamo la nuova umidità (Tendiamo sempre verso 20)
    if (humidity < 18) {
      if (Math.random() > 0.20) { //80% di probabilità di sistemare l'umidità
        double randomIncrement = 0.5 + (Math.random() * 1); // Numero tra 0.5 e 1.5
        humidity += Math.round(randomIncrement * 100.0) / 100.0;
      } else { //20% di probabilità di peggiorarla ulteriormente
        double randomDecrement = -1.5 + (Math.random() * 1); // Numero tra -1.5 e -0.5
        humidity += Math.round(randomDecrement * 100.0) / 100.0;
      }
    } else if (humidity > 22) {
      if (Math.random() > 0.20) { //80% di probabilità di sistemare l'umidità
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

    return formatNumber(humidity);

  }
  private double simulateWeight(double weight) {
    if (weight < 25) { //Anomalia, peso troppo basso
      if (Math.random() > 0.20) { //80% di probabilità di sistemare il peso
        double randomIncrement = 10 + (Math.random() * 20); // Numero tra 10 e 30
        weight += Math.round(randomIncrement * 100.0) / 100.0;
      } else { //20% di probabilità di peggiorarlo ulteriormente
        double randomDecrement = -2 + (Math.random() * 1); // Numero tra -2 e -1
        weight += Math.round(randomDecrement * 100.0) / 100.0;
      }
    } else if (weight > 135) { //Anomalia, peso troppo alta
      if (Math.random() > 0.20) { //80% di probabilità di sistemare il peso
        double randomDecrement = -30 + (Math.random() * 20); // Numero tra -30 e -10
        weight += Math.round(randomDecrement * 100.0) / 100.0;
      } else { //20% di probabilità di peggiorarlo ulteriormente
        double randomIncrement = 1 + (Math.random() * 1); // Numero tra 1 e 2
        weight += Math.round(randomIncrement * 100.0) / 100.0;
      }
    } else { //Peso in range
      if (Math.random() > 0.50) { //50% di probabilità di aumentarlo
        double randomIncrement = 10 + (Math.random() * 10); // Numero tra 10 e 20
        weight += Math.round(randomIncrement * 100.0) / 100.0;
      } else { //50% di probabilità di diminuirlo
        double randomDecrement = -20 + (Math.random() * 10); // Numero tra -20 e -10
        weight += Math.round(randomDecrement * 100.0) / 100.0;
      }
    }
    return formatNumber(weight);
  }
  private double formatNumber (double number) {
    DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.getDefault());
    otherSymbols.setDecimalSeparator('.');
    otherSymbols.setGroupingSeparator(',');
    return Double.parseDouble(new DecimalFormat("0.00", otherSymbols).format(number));
  }

}

