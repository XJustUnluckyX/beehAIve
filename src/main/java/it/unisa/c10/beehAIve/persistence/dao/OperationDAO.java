package it.unisa.c10.beehAIve.persistence.dao;

import it.unisa.c10.beehAIve.persistence.entities.Operation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OperationDAO extends JpaRepository<Operation, Integer> {

  List<Operation> findAllByHiveId(int hiveId);

  List<Operation> findAllByOperationStatusAndHiveId(String status, int hiveId);

  List<Operation> findAllByBeekeeperEmail(String beekeeperEmail);

  List<Operation> findAllByOperationStatusAndHiveIdOrderByOperationDateAsc(String status, int hiveId);

  List<Operation> findAllByOperationStatusAndHiveIdOrderByOperationDateDesc(String status, int hiveId);

  @Query("SELECT COUNT(*) FROM Operation o, Hive h WHERE o.hiveId = h.id AND h.id = :hiveId AND o.operationStatus = 'Not completed'")
  int countByHiveIdAndOperationsNotCompleted(int hiveId);

}
