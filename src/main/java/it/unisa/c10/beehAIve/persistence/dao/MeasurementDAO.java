package it.unisa.c10.beehAIve.persistence.dao;

import it.unisa.c10.beehAIve.persistence.entities.Measurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MeasurementDAO extends JpaRepository<Measurement, Integer> {
  // save()

  // findAll()

  // findById()

  // deleteById()

  List<Measurement> findByHiveId(int hiveId);

  List<Measurement> findByMeasurementDateBetween(LocalDate date1, LocalDate date2);

  List<Measurement> findByMeasurementDateBetweenAndHiveId(LocalDate date1, LocalDate date2, int hiveId);

  List<Measurement> findByQueenPresentTrue();

  List<Measurement> findByQueenPresentFalse();

  List<Measurement> findByQueenPresentTrueAndHiveId(int hiveId);

  List<Measurement> findByQueenPresentFalseAndHiveId(int hiveId);

  @Query("SELECT id, sensorId, measurementDate, weight, spectrogram, temperature, ambientTemperature, humidity, ambientHumidity, " +
             "queenPresent " +
                 "FROM Measurement " +
                     "WHERE (temperature BETWEEN 33 AND 36) " +
                         "AND (humidity BETWEEN 20 AND 30) " +
                             "AND (weight BETWEEN 25 AND 130)")
  List<Measurement> findGoodMeasurements();

  @Query("SELECT id, sensorId, measurementDate, weight, spectrogram, temperature, ambientTemperature, humidity, ambientHumidity, " +
    "queenPresent " +
    "FROM Measurement " +
    "WHERE (temperature BETWEEN 33 AND 36) " +
    "AND (humidity BETWEEN 20 AND 30) " +
    "AND (weight BETWEEN 25 AND 130) " +
    "AND hiveId = :hiveId")
  List<Measurement> findGoodMeasurementsByHiveId(int hiveId);

  @Query("SELECT id, sensorId, measurementDate, weight, spectrogram, temperature, ambientTemperature, humidity, ambientHumidity, " +
             "queenPresent " +
                 "FROM Measurement " +
                     "WHERE (temperature NOT BETWEEN 33 AND 36) " +
                         "AND (humidity NOT BETWEEN 20 AND 30) " +
                             "AND (weight NOT BETWEEN 25 AND 130)")
  List<Measurement> findBadMeasurements();

  @Query("SELECT id, sensorId, measurementDate, weight, spectrogram, temperature, ambientTemperature, humidity, ambientHumidity, " +
             "queenPresent " +
                 "FROM Measurement " +
                     "WHERE (temperature NOT BETWEEN 33 AND 36) " +
                         "AND (humidity NOT BETWEEN 20 AND 30) " +
                             "AND (weight NOT BETWEEN 25 AND 130) " +
                                 "AND hiveId = :hiveId")
  List<Measurement> findBadMeasurementsByHiveId(int hiveId);

  int countByHiveId(int hiveId);
}
