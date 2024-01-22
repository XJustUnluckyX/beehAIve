package it.unisa.c10.beehAIve.persistence.dao;

import it.unisa.c10.beehAIve.persistence.entities.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SensorDAO extends JpaRepository<Sensor, Integer> {

  /**
   * Restituisce l'ultimo sensore inserito dell'apicoltore.
   * @param beekeeperEmail L'email dell'apicoltore di cui vogliamo ottenere il sensore.
   * @return Un oggetto {@code Sensor} contenente tutte le informazioni relative al sensore.
   */
  Sensor findTopByBeekeeperEmailOrderByIdDesc(String beekeeperEmail);

}
