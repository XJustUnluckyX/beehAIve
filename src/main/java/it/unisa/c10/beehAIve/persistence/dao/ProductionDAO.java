package it.unisa.c10.beehAIve.persistence.dao;

import org.springframework.stereotype.Repository;

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

  int countByBeekeeperEmail(String beekeeperEmail);

  int countByProduct(String product);

  int countByHiveId(int hiveId);

  int countByProductAndBeekeeperEmail(String product, String beekeeperEmail);

  int countByProductAndHiveId(String product, int hiveId);
}
