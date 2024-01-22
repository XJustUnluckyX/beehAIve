package it.unisa.c10.beehAIve.persistence.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import it.unisa.c10.beehAIve.persistence.entities.Hive;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HiveDAO extends JpaRepository<Hive, Integer> {


  /**
   * Restituisce tutte le arnie di un apicoltore che contengono una stringa nel nome.
   * @param nickname La stringa che cerchiamo all'interno del nome dell'arnia
   * @param beekeeperEmail L'email dell'apicoltore di cui vogliamo le arnie.
   * @return Una lista di {@code Hive} dell'apicoltore che corrisponde ai parametri di ricerca.
   */
  List<Hive> findByNicknameContainingAndBeekeeperEmail(String nickname, String beekeeperEmail);

  /**
   * Restituisce tutte le arnie di un apicoltore che hanno operazioni imminenti
   * @param beekeeperEmail L'email dell'apicoltore di cui vogliamo le arnie.
   * @return Una lista di {@code Hive} dell'apicoltore che corrisponde ai parametri di ricerca.
   */
  List<Hive> findByUncompletedOperationsTrueAndBeekeeperEmail(String beekeeperEmail);

  /**
   * Restituisce tutte le arnie di un apicoltore che presentano uno stato di salute non ottimale.
   * @param beekeeperEmail L'email dell'apicoltore di cui vogliamo le arnie.
   * @return Una lista di {@code Hive} dell'apicoltore che corrisponde ai parametri di ricerca.
   */
  @Query("SELECT h " +
             "FROM Hive h, Beekeeper b " +
                 "WHERE b.email = :beekeeperEmail " +
                     "AND b.email = h.beekeeperEmail " +
                         "AND (h.hiveHealth = 2 OR h.hiveHealth = 3) " +
                             "ORDER BY h.hiveHealth DESC")
  List<Hive> findByHealthIssuesAndBeekeeperEmail(String beekeeperEmail);

  /**
   * Restituisce tutte le arnie di un apicoltore che hanno operazioni imminenti e che contengono una stringa nel nome.
   * @param nickname La stringa che cerchiamo all'interno del nome dell'arnia
   * @param beekeeperEmail L'email dell'apicoltore di cui vogliamo le arnie.
   * @return Una lista di {@code Hive} dell'apicoltore che corrisponde ai parametri di ricerca.
   */
  List<Hive> findByNicknameContainingAndUncompletedOperationsTrueAndBeekeeperEmail(String nickname
      , String beekeeperEmail);

  /**
   * Prende tutte le arnie di un apicoltore che presentano uno stato di salute non ottimale e che contengono una stringa nel nome.
   * @param nickname La stringa che cerchiamo all'interno del nome dell'arnia
   * @param beekeeperEmail L'email dell'apicoltore di cui vogliamo le arnie.
   * @return Una lista di {@code Hive} dell'apicoltore che corrisponde ai parametri di ricerca.
   */
  @Query("SELECT h " +
             "FROM Hive h, Beekeeper b " +
                 "WHERE b.email = :beekeeperEmail " +
                     "AND b.email = h.beekeeperEmail " +
                         "AND h.nickname LIKE %:nickname% " +
                             "AND (h.hiveHealth = 2 OR h.hiveHealth = 3) " +
                                 "ORDER BY h.hiveHealth DESC")
  List<Hive> findByNicknameContainingAndHealthIssuesAndBeekeeperEmail(String nickname,
                                                                      String beekeeperEmail);

  /**
   * Restituisce tutte le arnie di un apicoltore dal database.
   * @param beekeeperEmail L'email dell'apicoltore di cui vogliamo le arnie.
   * @return Una lista di {@code Hive} dell'apicoltore.
   */
  List<Hive> findByBeekeeperEmail(String beekeeperEmail);

  /**
   * Restituisce l'ultima arnia creata di un apicoltore.
   * @param beekeeperEmail L'email dell'apicoltore di cui vogliamo ottenere l'ultima arnia creata.
   * @return Un oggetto {@code Hive} contenente tutte le informazioni relative all'ultima arnia creata dall'apicoltore.
   */
  Hive findTopByBeekeeperEmailOrderByIdDesc(String beekeeperEmail);

  /**
   * Restituisce il numero totale di arnie di un apicoltore.
   * @param beekeeperEmail L'email dell'apicoltore di cui vogliamo ottenere il numero totale di arnie.
   * @return Il numero totale di arnie di un apicoltore.
   */
  int countByBeekeeperEmail(String beekeeperEmail);

  @Query("SELECT h.nickname " +
            "FROM Hive h " +
                "WHERE h.id = :id")
  String findByIdSelectNickname(int id);

}
