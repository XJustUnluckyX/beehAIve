package it.unisa.c10.beehAIve.persistence.dao;

import it.unisa.c10.beehAIve.persistence.entities.Measurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MeasurementDAO extends JpaRepository<Measurement, Integer> {

  List<Measurement> findFirst49ByHiveIdOrderByMeasurementDateDesc(int hiveId);

  Measurement findTopByHiveIdOrderByMeasurementDateDesc(int hiveId);

}
