package it.unisa.c10.beehAIve.persistence.dao;

import it.unisa.c10.beehAIve.persistence.entities.Anomaly;
import it.unisa.c10.beehAIve.persistence.entities.Measurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MeasurementDAO extends JpaRepository<Measurement, Integer> {
  // save()

  // findAll()

  // findById()

  // deleteById()

  List<Measurement> findByHiveId(int hiveId);

  List<Measurement> findByMeasurementDateBetween(LocalDateTime date1, LocalDateTime date2);

  List<Measurement> findByMeasurementDateBetweenAndHiveId(LocalDate date1, LocalDate date2, int hiveId);

  List<Measurement> findByQueenPresentTrue();

  List<Measurement> findByQueenPresentFalse();

  List<Measurement> findByQueenPresentTrueAndHiveId(int hiveId);

  List<Measurement> findByQueenPresentFalseAndHiveId(int hiveId);

  @Query("SELECT m " +
                 "FROM Measurement m " +
                     "WHERE (m.temperature BETWEEN 33 AND 36) " +
                         "AND (m.humidity BETWEEN 20 AND 30) " +
                             "AND (m.weight BETWEEN 25 AND 130)")
  List<Measurement> findGoodMeasurements();

  @Query("SELECT m " +
             "FROM Measurement m " +
                 "WHERE (m.temperature BETWEEN 33 AND 36) " +
                     "AND (m.humidity BETWEEN 20 AND 30) " +
                         "AND (m.weight BETWEEN 25 AND 130)" +
                             "AND m.hiveId = :hiveId")
  List<Measurement> findGoodMeasurementsByHiveId(int hiveId);

  @Query("SELECT m " +
                 "FROM Measurement m " +
                     "WHERE (m.temperature NOT BETWEEN 33 AND 36) " +
                         "OR (m.humidity NOT BETWEEN 20 AND 30) " +
                             "OR (m.weight NOT BETWEEN 25 AND 130)")
  List<Measurement> findBadMeasurements();

  @Query("SELECT m " +
                 "FROM Measurement m " +
                     "WHERE (m.temperature NOT BETWEEN 33 AND 36) " +
                         "OR (m.humidity NOT BETWEEN 20 AND 30) " +
                             "OR (m.weight NOT BETWEEN 25 AND 130) " +
                                 "AND m.hiveId = :hiveId")
  List<Measurement> findBadMeasurementsByHiveId(int hiveId);

  List<Measurement> findByHiveIdOrderByMeasurementDateAsc(int hiveId);

  Measurement findTopByHiveIdOrderByMeasurementDateDesc(int hiveId);

  int countByHiveId(int hiveId);
}
