package it.unisa.c10.beehAIve.persistence.dao;

import it.unisa.c10.beehAIve.persistence.entities.Operation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OperationDAO extends JpaRepository<Operation, Integer> {

  /**
   * Restituisce tutti gli interventi relativi a un'arnia.
   * @param hiveId L'ID dell'arnia di cui vogliamo ottenere gli interventi.
   * @return Una lista contenente tutti gli interventi dell'arnia.
   */
  List<Operation> findAllByHiveId(int hiveId);

  /**
   * Restituisce tutti gli interventi con uno specifico stato relativi a un'arnia.
   * @param status Lo stato degli interventi che vogliamo ottenere ("Completed", "Not completed")
   * @param hiveId L'ID dell'arnia di cui vogliamo ottenere gli interventi.
   * @return Una lista contenente tutti gli interventi dell'arnia con uno specifico stato.
   */
  List<Operation> findAllByOperationStatusAndHiveId(String status, int hiveId);

  /**
   * Restituisce tutti gli interventi relativi alle arnie di un apicoltore.
   * @param beekeeperEmail L'email dell'apicoltore di cui vogliamo ottenere gli interventi.
   * @return Una lista contenente tutti gli interventi delle arnie di un apicoltore.
   */
  List<Operation> findAllByBeekeeperEmail(String beekeeperEmail);

  /**
   * Restituisce tutti gli interventi con uno specifico stato relativi a un'arnia, ordinati in base alla data
   * prevista dell'intervento in ordine crescente.
   * @param status Lo stato degli interventi che vogliamo ottenere ("Completed", "Not completed")
   * @param hiveId L'ID dell'arnia di cui vogliamo ottenere gli interventi.
   * @return Una lista contenente tutti gli interventi dell'arnia con uno specifico stato, ordinati in base alla data
   * prevista dell'intervento in ordine crescente.
   */
  List<Operation> findAllByOperationStatusAndHiveIdOrderByOperationDateAsc(String status, int hiveId);

  /**
   * Restituisce tutti gli interventi con uno specifico stato relativi a un'arnia, ordinati in base alla data
   * prevista dell'intervento in ordine decrescente.
   * @param status Lo stato degli interventi che vogliamo ottenere ("Completed", "Not completed")
   * @param hiveId L'ID dell'arnia di cui vogliamo ottenere gli interventi.
   * @return Una lista contenente tutti gli interventi dell'arnia con uno specifico stato, ordinati in base alla data
   * prevista dell'intervento in ordine decrescente.
   */
  List<Operation> findAllByOperationStatusAndHiveIdOrderByOperationDateDesc(String status, int hiveId);

  /**
   * Restituisce il numero di interventi non ancora completati relativi a un'arnia.
   * @param hiveId L'ID dell'arnia di cui vogliamo ottenere gli interventi.
   * @return Una lista contenente tutti gli interventi non ancora completati dell'arnia.
   */
  @Query("SELECT COUNT(*) FROM Operation o, Hive h WHERE o.hiveId = h.id AND h.id = :hiveId AND o.operationStatus = 'Not completed'")
  int countByHiveIdAndOperationsNotCompleted(int hiveId);

}
