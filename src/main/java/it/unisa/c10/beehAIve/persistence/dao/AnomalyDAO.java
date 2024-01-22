package it.unisa.c10.beehAIve.persistence.dao;

import it.unisa.c10.beehAIve.persistence.entities.Anomaly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AnomalyDAO extends JpaRepository<Anomaly, Integer> {

  /**
   * Restituisce le anomalie risolte di un'arnia.
   * @param hiveId L'id dell'arnia di cui vogliamo le anomalie.
   * @return Una lista di {@code Anomaly} risolte dell'arnia.
   */
  List<Anomaly> findByHiveIdAndResolvedTrue(int hiveId);

  /**
   * Restituisce le anomalie non risolte di un'arnia.
   * @param hiveId L'id dell'arnia di cui vogliamo le anomalie.
   * @return Una lista di {@code Anomaly} non risolte dell'arnia.
   */
  List<Anomaly> findByHiveIdAndResolvedFalse(int hiveId);

}
