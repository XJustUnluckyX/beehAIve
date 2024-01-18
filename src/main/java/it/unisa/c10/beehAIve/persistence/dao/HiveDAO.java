package it.unisa.c10.beehAIve.persistence.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import it.unisa.c10.beehAIve.persistence.entities.Hive;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HiveDAO extends JpaRepository<Hive, Integer> {
  // save();

  // findAll();

  // findById();

  // deleteById();

  List<Hive> findByNicknameContainingAndBeekeeperEmail(String nickname, String beekeeperEmail);

  List<Hive> findByHiveTypeAndBeekeeperEmail(String type, String beekeeperEmail);

  List<Hive> findByCreationDateBetweenAndBeekeeperEmail(LocalDate date1, LocalDate date2,
                                                        String beekeeperEmail);

  List<Hive> findByBeekeeperEmail(String beekeeperEmail);

  List<Hive> findByBeeSpeciesAndBeekeeperEmail(String beeSpecies, String beekeeperEmail);

  @Query("SELECT h " +
             "FROM Hive h " +
                 "WHERE h.nickname = :nickname " +
                     "AND h.hiveType = :hiveType " +
                         "AND (h.creationDate BETWEEN :date1 AND :date2) " +
                             "AND h.beekeeperEmail = :beekeeperEmail " +
                                 "AND h.beeSpecies = :beeSpecies")
  List<Hive> findByFilters(String nickname, String hiveType, LocalDate date1, LocalDate date2,
                           String beekeeperEmail, String beeSpecies);

  @Query("SELECT DISTINCT h " +
             "FROM Hive h, Anomaly a " +
                 "WHERE (h.hiveHealth = 2 OR h.hiveHealth = 3) " +
                     "AND h.id = a.hiveId " +
                         "AND a.resolved = false " +
                             "ORDER BY h.hiveHealth DESC")
  List<Hive> findByBeekeeperEmailAndAnomaliesUnresolved(String beekeeperEmail);

  List<Hive> findByBeekeeperEmailAndUncompletedOperationsTrue(String beekeeperEmail);

  Hive findTopByBeekeeperEmailOrderByIdDesc(String beekeeperEmail);

  void deleteByBeekeeperEmail(String beekeeperEmail);

  int countByBeekeeperEmail(String beekeeperEmail);
}
