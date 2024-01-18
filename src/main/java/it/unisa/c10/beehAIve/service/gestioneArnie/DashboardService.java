package it.unisa.c10.beehAIve.service.gestioneArnie;

import it.unisa.c10.beehAIve.persistence.dao.HiveDAO;
import it.unisa.c10.beehAIve.persistence.entities.Hive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class DashboardService {
  private final HiveDAO hiveDAO;

  @Autowired
  public DashboardService(HiveDAO hiveDAO){
    this.hiveDAO = hiveDAO;
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

  public List<Hive> getBeekeeperHives(String beekeeperEmail){
    return hiveDAO.findByBeekeeperEmail(beekeeperEmail);
  }

  public List<Hive> getBeekeeperHivesByNickname(String beekeeperEmail, String nickname) {
    return hiveDAO.findByNicknameContainingAndBeekeeperEmail(nickname, beekeeperEmail);
  }

  public List<Hive> getBeekeeperHivesByHiveType(String beekeeperEmail, String hiveType) {
    return hiveDAO.findByHiveTypeAndBeekeeperEmail(hiveType, beekeeperEmail);
  }

  public List<Hive> getBeekeeperHivesByDateRange(String beekeeperEmail,
                                                       LocalDate date1, LocalDate date2) {
    return hiveDAO.findByCreationDateBetweenAndBeekeeperEmail(date1, date2, beekeeperEmail);
  }

  public List<Hive> getBeekeeperHivesByBeeSpecies(String beekeeperEmail, String beeSpecies) {
    return hiveDAO.findByBeeSpeciesAndBeekeeperEmail(beeSpecies, beekeeperEmail);
  }

  public List<Hive> getBeekeeperHivesWithAnomalies(String beekeeperEmail){
    return hiveDAO.findByBeekeeperEmailAndAnomaliesUnresolved(beekeeperEmail);
  }

  public List<Hive> getBeekeeperHivesWithScheduledOperations(String beekeeperEmail) {
    return hiveDAO.findByBeekeeperEmailAndUncompletedOperationsTrue(beekeeperEmail);
  }

  public List<Hive> getBeekeeperHivesByAllFilters(String nickname, String hiveType,
                                                  LocalDate date1, LocalDate date2,
                                                  String beekeeperEmail, String beeSpecies) {
    return hiveDAO.findByFilters(nickname, hiveType, date1, date2, beekeeperEmail, beeSpecies);
  }

  public void deleteHive(int id){
    hiveDAO.deleteById(id);
  }
}
