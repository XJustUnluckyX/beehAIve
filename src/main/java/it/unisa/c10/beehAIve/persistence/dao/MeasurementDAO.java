package it.unisa.c10.beehAIve.persistence.dao;

import it.unisa.c10.beehAIve.persistence.entities.DateAndSensorID;
import it.unisa.c10.beehAIve.persistence.entities.Measurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MeasurementDAO extends JpaRepository<Measurement, DateAndSensorID> {
  // save()

  // findAll()

  // findById()

  // deleteById()

  @Query("SELECT dateAndSensorID, weight, spectrogram, temperature, ambientTemperature, humidity, ambientHumidity, " +
             "isQueenPresent " +
                 "FROM Measurement, Sensor " +
                     "WHERE dateAndSensorID.sensorId = Sensor.id " +
                         "AND Sensor.hiveId = :hiveId")
  List<Measurement> findByHiveID(int hiveId);

  @Query("SELECT dateAndSensorID, weight, spectrogram, temperature, ambientTemperature, humidity, ambientHumidity, " +
             "isQueenPresent " +
                 "FROM Measurement " +
                     "WHERE dateAndSensorID.date BETWEEN :date1 AND :date2")
  List<Measurement> findByMeasurementDateBetween(LocalDate date1, LocalDate date2);

  @Query("SELECT dateAndSensorID, weight, spectrogram, temperature, ambientTemperature, humidity, ambientHumidity, " +
             "isQueenPresent " +
                 "FROM Measurement, Sensor " +
                     "WHERE dateAndSensorID.date BETWEEN :date1 AND :date2 " +
                         "AND dateAndSensorID.sensorId = Sensor.id " +
                             "AND Sensor.hiveId = :hiveId")
  List<Measurement> findByMeasurementDateBetweenAndHiveID(LocalDate date1, LocalDate date2, int hiveId);

  List<Measurement> findByIsQueenPresentTrue();

  List<Measurement> findByIsQueenPresentFalse();

  @Query("SELECT dateAndSensorID, weight, spectrogram, temperature, ambientTemperature, humidity, ambientHumidity, " +
             "isQueenPresent " +
                 "FROM Measurement, Sensor " +
                     "WHERE isQueenPresent = true " +
                         "AND dateAndSensorID.sensorId = Sensor.id " +
                             "AND Sensor.hiveId = :hiveId")
  List<Measurement> findByIsQueenPresentTrueAndHiveId(int hiveId);


  @Query("SELECT dateAndSensorID, weight, spectrogram, temperature, ambientTemperature, humidity, ambientHumidity, " +
             "isQueenPresent " +
                 "FROM Measurement, Sensor " +
                     "WHERE isQueenPresent = false " +
                         "AND dateAndSensorID.sensorId = Sensor.id " +
                             "AND Sensor.hiveId = :hiveId")
  List<Measurement> findByIsQueenPresentFalseAndHiveId(int hiveId);

  @Query("SELECT dateAndSensorID, weight, spectrogram, temperature, ambientTemperature, humidity, ambientHumidity, " +
             "isQueenPresent " +
                 "FROM Measurement, Sensor " +
                     "WHERE (temperature BETWEEN 33 AND 36) " +
                         "AND (humidity BETWEEN 20 AND 30) " +
                             "AND (weight BETWEEN 25 AND 130)")
  List<Measurement> findGoodMeasurements();

  @Query("SELECT dateAndSensorID, weight, spectrogram, temperature, ambientTemperature, humidity, ambientHumidity, " +
             "isQueenPresent " +
                 "FROM Measurement, Sensor " +
                     "WHERE (temperature BETWEEN 33 AND 36) " +
                         "AND (humidity BETWEEN 20 AND 30) " +
                             "AND (weight BETWEEN 25 AND 130) " +
                                 "AND dateAndSensorID.sensorId = Sensor.id " +
                                     "AND Sensor.hiveId = :hiveId")
  List<Measurement> findGoodMeasurementsByHiveId(int hiveId);

  @Query("SELECT dateAndSensorID, weight, spectrogram, temperature, ambientTemperature, humidity, ambientHumidity, " +
             "isQueenPresent " +
                 "FROM Measurement, Sensor " +
                     "WHERE (temperature NOT BETWEEN 33 AND 36) " +
                         "AND (humidity NOT BETWEEN 20 AND 30) " +
                             "AND (weight NOT BETWEEN 25 AND 130)")
  List<Measurement> findBadMeasurements();

  @Query("SELECT dateAndSensorID, weight, spectrogram, temperature, ambientTemperature, humidity, ambientHumidity, " +
             "isQueenPresent " +
                 "FROM Measurement, Sensor " +
                     "WHERE (temperature NOT BETWEEN 33 AND 36) " +
                         "AND (humidity NOT BETWEEN 20 AND 30) " +
                             "AND (weight NOT BETWEEN 25 AND 130) " +
                                 "AND dateAndSensorID.sensorId = Sensor.id " +
                                     "AND Sensor.hiveId = :hiveId")
  List<Measurement> findBadMeasurementsByHiveId(int hiveId);

  @Query("SELECT COUNT(*) " +
             "FROM Measurement, Sensor " +
                 "WHERE Measurement.dateAndSensorID.sensorId = Sensor.id " +
                     "AND Sensor.hiveId = :hiveId")
  int countByHiveId(int hiveId);
}
