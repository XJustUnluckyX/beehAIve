package it.unisa.c10.beehAIve.service.gestioneArnie;

import it.unisa.c10.beehAIve.persistence.dao.AnomalyDAO;
import it.unisa.c10.beehAIve.persistence.dao.HiveDAO;
import it.unisa.c10.beehAIve.persistence.dao.OperationDAO;
import it.unisa.c10.beehAIve.persistence.entities.Anomaly;
import it.unisa.c10.beehAIve.persistence.entities.Beekeeper;
import it.unisa.c10.beehAIve.persistence.entities.Hive;
import it.unisa.c10.beehAIve.persistence.entities.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/*
La seguente classe deve supportare le seguenti operazioni:
1. Creare un'arnia.
2. Modificare un'arnia.
3. Eliminare un'arnia.
4. Visualizzare tutte le arnie dell'apicoltore.
5. Visualizzare tutte le arnie dell'apicoltore in base a determinati filtri (Interventi e Anomalie).
6. Ricavare una determinata arnia dal database.
 */

@Service
public class DashboardService {
  private final HiveDAO hiveDAO;
  private final OperationDAO operationDAO;
  private final AnomalyDAO anomalyDAO;

  @Autowired
  public DashboardService(HiveDAO hiveDAO, OperationDAO operationDAO,AnomalyDAO anomalyDAO){
    this.hiveDAO = hiveDAO;
    this.operationDAO = operationDAO;
    this.anomalyDAO = anomalyDAO;
  }

  // Metodo per ricercare l'arnia nel database, così da evitare ripetizioni nel codice
  public Hive getHive(int hiveId) {
    // Ricerca dell'arnia nel database
    Optional<Hive> optionalHive = hiveDAO.findById(hiveId);

    // Controllo sull'esistenza dell'arnia nel database
    if (optionalHive.isPresent()) {
      return optionalHive.get();
    } else {
      throw new NullPointerException("Hive not found for ID: " + hiveId);
    }
  }

  public void createHive(int hiveId, String nickname, String hiveType, String beekeeperEmail,
                         String beeSpecies){
    Hive hive = new Hive();

    hive.setId(hiveId);
    hive.setNickname(nickname);
    hive.setHiveType(hiveType);
    hive.setCreationDate(LocalDate.now());
    hive.setBeekeeperEmail(beekeeperEmail);
    hive.setBeeSpecies(beeSpecies);

    hiveDAO.save(hive);
  }

  public void modifyHive(int hiveId, String nickname, String hiveType, String beeSpecies){
    Hive hive = getHive(hiveId);

    hive.setNickname(nickname);
    hive.setHiveType(hiveType);
    hive.setBeeSpecies(beeSpecies);

    hiveDAO.save(hive);
  }

  public void deleteHive(int id){
    hiveDAO.deleteById(id);
  }

  public List<Hive> getAllHivesByBeekeeper(String beekeeperEmail){
    return hiveDAO.findByBeekeeperEmail(beekeeperEmail);
  }

  public List<Hive> getAllHivesByBeekeeperAndNickname(String beekeeperEmail, String nickname) {
    return hiveDAO.findByNicknameContainingAndBeekeeperEmail(nickname, beekeeperEmail);
  }

  public List<Hive> getAllHivesByBeekeeperAndDateRange(String beekeeperEmail,
                                                       LocalDate date1, LocalDate date2) {
    return hiveDAO.findByCreationDateBetween(date1,date2);
  }

  public List<Hive> getHivesByAnomalies(String anomaly){
    int i;
    Optional<Hive> hive;
    List<Hive> hives = new ArrayList<>();
    List<Integer> HivesId = new ArrayList<>();
    List<Anomaly> temp = anomalyDAO.findByAnomalyName(anomaly);

    for (i = 0; i < temp.size(); i++){

      HivesId.add(temp.get(i).getHiveId());
      hive = hiveDAO.findById(HivesId.get(i));
      hives.add(hive.get());

    }

    return hives;

  }

  public List ShowAllHiveByOperation(String operation){
    Optional<Hive> hive;
    List<Hive> hives = new ArrayList<>();
    List<Integer> HivesId = new ArrayList<>();
    List<Operation> temp = operationDAO.findAllByOperationStatus(operation);

    for (int i = 0; i < temp.size(); i++){
      HivesId.add(temp.get(i).getHiveId());
      hive = hiveDAO.findById(HivesId.get(i));
      hives.add(hive.get());
    }

    return hives;

  }
}
