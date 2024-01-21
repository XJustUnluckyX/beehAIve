package it.unisa.c10.beehAIve.persistence.dao;

import it.unisa.c10.beehAIve.persistence.entities.Anomaly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AnomalyDAO extends JpaRepository<Anomaly, Integer> {

  List<Anomaly> findByHiveIdAndResolvedTrue(int hiveId);

  List<Anomaly> findByHiveIdAndResolvedFalse(int hiveId);

}
