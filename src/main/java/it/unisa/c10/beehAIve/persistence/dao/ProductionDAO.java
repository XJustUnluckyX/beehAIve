package it.unisa.c10.beehAIve.persistence.dao;

import it.unisa.c10.beehAIve.persistence.entities.Production;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProductionDAO extends JpaRepository<Production, Integer> {
  // save()

  // findAll()

  // findById()

  // deleteById()

  List<Production> findByHiveId(int hiveId);

  List<Production> findByProduct(String product);

  List<Production> findByProductAndHiveId(String product, int hiveId);

  List<Production> findByRegistrationDate(LocalDate registrationDate);

  List<Production> findByRegistrationDateAndHiveId(LocalDate registrationDate, int hiveId);

  List<Production> findByBeekeeperEmail(String beekeeperEmail);

  List<Production> findAllByOrderByRegistrationDateAsc();

  int countByBeekeeperEmail(String beekeeperEmail);

  int countByProduct(String product);

  int countByHiveId(int hiveId);

  int countByProductAndBeekeeperEmail(String product, String beekeeperEmail);

  int countByProductAndHiveId(String product, int hiveId);
}
