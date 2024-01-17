package it.unisa.c10.beehAIve.service.gestioneArnie;

import it.unisa.c10.beehAIve.persistence.dao.AnomalyDAO;
import it.unisa.c10.beehAIve.persistence.dao.HiveDAO;
import it.unisa.c10.beehAIve.persistence.dao.OperationDAO;
import it.unisa.c10.beehAIve.persistence.entities.Anomaly;
import it.unisa.c10.beehAIve.persistence.entities.Hive;
import it.unisa.c10.beehAIve.persistence.entities.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

  private HiveDAO hiveDAO;
  private OperationDAO operationDAO;
  private AnomalyDAO anomalyDAO;

  @Autowired
  public DashboardService(HiveDAO hiveDao, OperationDAO operationDAO,AnomalyDAO anomalyDAO){
    this.hiveDAO = hiveDao;
    this.operationDAO = operationDAO;
    this.anomalyDAO = anomalyDAO;
  }

  public void creationhive(Hive hive){
    hiveDAO.save(hive);
  }

  public void ModifyHive(int id, String Nickname, String HiveType, String Bees_Type){
    Optional<Hive> tempHive = hiveDAO.findById(id);

    tempHive.get().setNickname(Nickname);
    tempHive.get().setHiveType(HiveType);
    tempHive.get().setBeeSpecies(Bees_Type);

    hiveDAO.save(tempHive.get());
  }

  public void DeleteHive(int id){
    hiveDAO.deleteById(id);
  }

  public List ShowAllHive(){
    return hiveDAO.findAll();
  }

  public List ShowAllHiveByAnomaly(String anomaly){
    Optional<Hive> hive;
    List<Hive> hives = new ArrayList<>();
    List<Integer> HivesId = new ArrayList<>();
    List<Anomaly> temp = anomalyDAO.findByName(anomaly);

    for (int i = 0; i > temp.size() -1; i++){

      HivesId.add(temp.get(i).getHiveId());
      hive = hiveDAO.findById(HivesId.get(i));
      hives.add(hive.get());

    }

    return hives;

  }

  public List ShowAllHiveByOperation(Operation operation){
    Optional<Hive> hive;
    List<Hive> hives = new ArrayList<>();
    List<Integer> HivesId = new ArrayList<>();
    List<Operation> temp = operationDAO.findAllByStatus("Not completed");

    for (int i = 0; i > temp.size()-1; i++){
      HivesId.add(temp.get(i).getHiveId());
      hive = hiveDAO.findById(HivesId.get(i));
      hives.add(hive.get());
    }

    return hives;

  }

  public Hive ShowHive(int HiveId){

    return hiveDAO.findById(HiveId).get();

  }

}
