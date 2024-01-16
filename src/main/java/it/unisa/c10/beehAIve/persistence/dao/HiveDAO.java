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

  List<Hive> findByCreationDateBetween(LocalDate date1, LocalDate date2);

  List<Hive> findByBeekeeperEmail(String beekeeperEmail);

  List<Hive> findByBeeSpeciesAndBeekeeperEmail(String beeSpecies, String beekeeperEmail);

  @Query("SELECT nickname, hiveType, creationDate, beekeeperEmail, beeSpecies " +
             "FROM Hive " +
                 "WHERE nickname = :nickname " +
                     "AND hiveType = :hiveType " +
                         "AND (creationDate BETWEEN :date1 AND :date2) " +
                             "AND beekeeperEmail = :beekeeperEmail " +
                                 "AND beeSpecies = :beeSpecies")
  List<Hive> findByFilters(String nickname, String hiveType, LocalDate date1, LocalDate date2, String beekeeperEmail, String beeSpecies);

  void deleteByBeekeeperEmail(String beekeeperEmail);
}
