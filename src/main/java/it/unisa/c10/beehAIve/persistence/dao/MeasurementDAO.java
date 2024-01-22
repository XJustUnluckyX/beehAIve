package it.unisa.c10.beehAIve.persistence.dao;

import it.unisa.c10.beehAIve.persistence.entities.Measurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MeasurementDAO extends JpaRepository<Measurement, Integer> {

  /**
   * Restituisce le ultime 48 misurazioni effettuate di un'arnia.
   * @param hiveId L'ID dell'arnia di cui vogliamo ottenere le ultime 48 misurazioni.
   * @return Una lista delle ultime 48 misurazioni dell'arnia specificata.
   */
  List<Measurement> findFirst49ByHiveIdOrderByMeasurementDateDesc(int hiveId);

  /**
   * Restituisce l'ultima misurazione effettuata di un'arnia.
   * @param hiveId L'ID dell'arnia di cui vogliamo ottenere l'ultima misurazione.
   * @return Un oggetto {@code Measurement} contenente le informazioni relative alla misurazione dell'arnia.
   */
  Measurement findTopByHiveIdOrderByMeasurementDateDesc(int hiveId);

}
