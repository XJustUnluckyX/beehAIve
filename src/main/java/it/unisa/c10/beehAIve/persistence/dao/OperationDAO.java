package it.unisa.c10.beehAIve.persistence.dao;

import it.unisa.c10.beehAIve.persistence.entities.Operation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OperationDAO extends JpaRepository<Operation, Integer> {
  // save()

  // findAll()

  // findById()

  // deleteById()

  List<Operation> findAllByType(String type);

  List<Operation> findAllByDateBetween(LocalDate date1, LocalDate date2);

  List<Operation> findAllByStatus(String status);

  List<Operation> findAllByHiveId(int hiveId);

  List<Operation> findAllByTypeAndHiveId(String type, int hiveId);

  List<Operation> findAllByDateBetweenAndHiveId(LocalDate date1, LocalDate date2, int hiveId);

  List<Operation> findAllByStatusAndHiveId(String status, int hiveId);

  List<Operation> findAllByBeekeeperEmail(String beekeeperEmail);

  List<Operation> findAllByDateBetweenAndBeekeeperEmail(LocalDate date1, LocalDate date2, String beekeeperEmail);

  List<Operation> findAllByStatusAndBeekeeperEmail(String status, String beekeeperEmail);

  int countByBeekeeperEmail(String beekeeperEmail);

  int countByHiveId(int hiveId);

}
