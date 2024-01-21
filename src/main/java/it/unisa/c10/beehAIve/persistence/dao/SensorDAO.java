package it.unisa.c10.beehAIve.persistence.dao;

import it.unisa.c10.beehAIve.persistence.entities.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SensorDAO extends JpaRepository<Sensor, Integer> {

  Sensor findTopByBeekeeperEmailOrderByIdDesc(String beekeeperEmail);

}
