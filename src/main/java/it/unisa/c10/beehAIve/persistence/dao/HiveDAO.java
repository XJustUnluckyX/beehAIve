package it.unisa.c10.beehAIve.persistence.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import it.unisa.c10.beehAIve.persistence.entities.Hive;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HiveDAO extends JpaRepository<Hive, Integer> {


  List<Hive> findByNicknameContainingAndBeekeeperEmail(String nickname, String beekeeperEmail);

  List<Hive> findByUncompletedOperationsTrueAndBeekeeperEmail(String beekeeperEmail);

  @Query("SELECT h " +
             "FROM Hive h, Beekeeper b " +
                 "WHERE b.email = :beekeeperEmail " +
                     "AND b.email = h.beekeeperEmail " +
                         "AND (h.hiveHealth = 2 OR h.hiveHealth = 3) " +
                             "ORDER BY h.hiveHealth DESC")
  List<Hive> findByHealthIssuesAndBeekeeperEmail(String beekeeperEmail);

  List<Hive> findByNicknameContainingAndUncompletedOperationsTrueAndBeekeeperEmail(String nickname
      , String beekeeperEmail);

  @Query("SELECT h " +
             "FROM Hive h, Beekeeper b " +
                 "WHERE b.email = :beekeeperEmail " +
                     "AND b.email = h.beekeeperEmail " +
                         "AND h.nickname LIKE %:nickname% " +
                             "AND (h.hiveHealth = 2 OR h.hiveHealth = 3) " +
                                 "ORDER BY h.hiveHealth DESC")
  List<Hive> findByNicknameContainingAndHealthIssuesAndBeekeeperEmail(String nickname,
                                                                      String beekeeperEmail);

  List<Hive> findByBeekeeperEmail(String beekeeperEmail);

  @Query("SELECT DISTINCT h " +
             "FROM Hive h, Anomaly a " +
                 "WHERE (h.hiveHealth = 2 OR h.hiveHealth = 3) " +
                     "AND h.id = a.hiveId " +
                         "AND a.resolved = false " +
                             "ORDER BY h.hiveHealth DESC")
  List<Hive> findByBeekeeperEmailAndAnomaliesUnresolved(String beekeeperEmail);

  Hive findTopByBeekeeperEmailOrderByIdDesc(String beekeeperEmail);

  int countByBeekeeperEmail(String beekeeperEmail);

  @Query("SELECT h.nickname " +
            "FROM Hive h " +
                "WHERE h.id = :id")
  String findByIdSelectNickname(int id);

}
