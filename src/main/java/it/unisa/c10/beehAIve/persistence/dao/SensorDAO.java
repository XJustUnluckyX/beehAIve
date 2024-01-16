package it.unisa.c10.beehAIve.persistence.dao;

import it.unisa.c10.beehAIve.persistence.entities.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

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
