package it.unisa.c10.beehAIve.persistence.dao;

import it.unisa.c10.beehAIve.persistence.entities.Anomaly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AnomalyDAO extends JpaRepository<Anomaly, Integer> {
  // save()

  // findAll()

  // findById()

  // deleteById

  List<Anomaly> findByName(String name);

  List<Anomaly> findByResolvedTrue();

  List<Anomaly> findByResolvedFalse();

  List<Anomaly> findByHiveIdAndResolvedTrue(int hiveId);

  List<Anomaly> findByHiveIdAndResolvedFalse(int hiveId);

  List<Anomaly> findByDetectionDateBetween(LocalDate date1, LocalDate date2);

  List<Anomaly> findByDetectionDateBetweenAndHiveId(LocalDate date1, LocalDate date2, int hiveId);

  List<Anomaly> findByDetectionDateBetweenAndHiveIdAndResolvedTrue(LocalDate date1, LocalDate date2, int hiveId);

  List<Anomaly> findByDetectionDateBetweenAndHiveIdAndResolvedFalse(LocalDate date1, LocalDate date2, int hiveId);

  int countByBeekeeperEmail(String beekeeperEmail);

  int countByHiveId(int hiveId);

  int countBySensorId(int sensorId);

  int countByHiveIdAndSensorId(int hiveId, int sensorId);

  int countByBeekeeperEmailAndResolvedFalse(String beekeeperEmail);

  int countByHiveIdAndResolvedTrue(int hiveId);

  int countByHiveIdAndResolvedFalse(int hiveId);
}
