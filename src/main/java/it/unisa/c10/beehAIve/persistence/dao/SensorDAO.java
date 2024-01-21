package it.unisa.c10.beehAIve.persistence.dao;

import it.unisa.c10.beehAIve.persistence.entities.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SensorDAO extends JpaRepository<Sensor, Integer> {
  // save()

  // findAll()

  // findById()

  // deleteById()

  List<Sensor> findAllByBeekeeperEmail(String beekeeperEmail);

  Sensor findTopByBeekeeperEmailOrderByIdDesc(String beekeeperEmail);

  int countByBeekeeperEmail(String beekeeperEmail);
}
