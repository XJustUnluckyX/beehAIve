package it.unisa.c10.beehAIve.persistence.dao;

import it.unisa.c10.beehAIve.persistence.entities.Operation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OperationDAO extends JpaRepository<Operation, Integer> {
  // save()

  // findAll()

  // findById()

  // deleteById()

  List<Operation> findAllByOperationType(String type);

  List<Operation> findAllByOperationDateBetween(LocalDate date1, LocalDate date2);

  List<Operation> findAllByOperationStatus(String status);

  List<Operation> findAllByHiveId(int hiveId);

  List<Operation> findAllByOperationTypeAndHiveId(String type, int hiveId);

  List<Operation> findAllByOperationDateBetweenAndHiveId(LocalDate date1, LocalDate date2, int hiveId);

  List<Operation> findAllByOperationStatusAndHiveId(String status, int hiveId);

  List<Operation> findAllByBeekeeperEmail(String beekeeperEmail);

  List<Operation> findAllByOperationDateBetweenAndBeekeeperEmail(LocalDate date1, LocalDate date2, String beekeeperEmail);

  List<Operation> findAllByOperationStatusAndBeekeeperEmail(String status, String beekeeperEmail);

  List<Operation> findAllByOrderByOperationDateAsc();

  int countByBeekeeperEmail(String beekeeperEmail);

  int countByHiveId(int hiveId);

  @Query("SELECT COUNT(*) FROM Operation o, Hive h WHERE o.hiveId = h.id AND h.id = :hiveId AND o.operationStatus = 'Not completed'")
  int countByHiveIdAndOperationsNotCompleted(int hiveId);
}
