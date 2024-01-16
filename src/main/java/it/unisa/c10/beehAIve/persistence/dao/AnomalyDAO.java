package it.unisa.c10.beehAIve.persistence.dao;

import org.springframework.stereotype.Repository;

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

  @Query("SELECT COUNT(*) FROM Anomaly, Hive WHERE Anomaly.hiveId = Hive.id AND Hive.beekeeperEmail = :beekeeperEmail")
  int countByBeekeeperEmail(String beekeeperEmail);

  int countByHiveId(int hiveId);

  int countBySensorId(int sensorId);

  int countByHiveIdAndSensorId(int hiveId, int sensorId);

  @Query("SELECT COUNT(*) FROM Anomaly, Hive WHERE Anomaly.hiveId = Hive.id AND Hive.beekeeperEmail = :beekeeperEmail" +
      " AND isResolved = false")
  int countByBeekeeperEmailAndResolvedFalse(String beekeeperEmail);

  int countByHiveIdAndResolvedTrue(int hiveId);

  int countByHiveIdAndResolvedFalse(int hiveId);
}
