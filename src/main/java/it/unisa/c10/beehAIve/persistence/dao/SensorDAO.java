package it.unisa.c10.beehAIve.persistence.dao;

import org.springframework.stereotype.Repository;

@Repository
public interface SensorDAO extends JpaRepository<Sensor, Integer> {
  // save()

  // findAll()

  // findById()

  // deleteById()

  @Query("SELECT Sensor.id, Sensor.hiveId FROM Sensor, Hive WHERE Sensor.hiveId = Hive.id AND Hive.beekeeperEmail = :beekeeperEmail")
  List<Sensor> findAllByBeekeeperEmail(String BeekeeperEmail);

  @Query("SELECT COUNT(*) FROM Sensor, Hive WHERE Sensor.hiveId = Hive.id AND Hive.beekeeperEmail = :beekeeperEmail")
  int countByBeekeeperEmail(String beekeeperEmail);
}
