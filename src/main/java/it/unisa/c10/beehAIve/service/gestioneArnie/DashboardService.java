package it.unisa.c10.beehAIve.service.gestioneArnie;

import it.unisa.c10.beehAIve.persistence.dao.BeekeeperDAO;
import it.unisa.c10.beehAIve.persistence.dao.HiveDAO;
import it.unisa.c10.beehAIve.persistence.dao.MeasurementDAO;
import it.unisa.c10.beehAIve.persistence.dao.SensorDAO;
import it.unisa.c10.beehAIve.persistence.entities.*;
import it.unisa.c10.beehAIve.service.gestioneUtente.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class DashboardService {
  private SubscriptionService subscriptionService;
  private final HiveDAO hiveDAO;
  private final BeekeeperDAO beekeeperDAO;
  private final SensorDAO sensorDAO;
  private final MeasurementDAO measurementDAO;

  @Autowired
  public DashboardService(HiveDAO hiveDAO, BeekeeperDAO beekeeperDAO,
                          SensorDAO sensorDAO, MeasurementDAO measurementDAO){
    this.hiveDAO = hiveDAO;
    this.beekeeperDAO = beekeeperDAO;
    this.sensorDAO = sensorDAO;
    this.measurementDAO = measurementDAO;
  }

  public void createHive(String beekeeperEmail, String nickname, String hiveType, String beeSpecies){
    // Creazione dell'arnia e salvataggio nel database
    Hive hive = new Hive();
    hive.setNickname(nickname);
    hive.setHiveType(hiveType);
    hive.setCreationDate(LocalDate.now());
    hive.setBeekeeperEmail(beekeeperEmail);
    hive.setBeeSpecies(beeSpecies);
    hive.setHiveHealth(1);
    hive.setUncompletedOperations(false);
    hiveDAO.save(hive);

    // Recupero dell'ID dell'arnia appena creata
    int createdHiveId = hiveDAO.findTopByBeekeeperEmailOrderByIdDesc(beekeeperEmail).getId();

    // Creazione del sensore relativo all'arnia appena creata e salvataggio nel database
    Sensor sensor = new Sensor();
    sensor.setHiveId(createdHiveId);
    sensor.setBeekeeperEmail(beekeeperEmail);
    sensorDAO.save(sensor);

    // Creazione della prima misurazione relativa all'arnia appena creata e salvataggio nel database
    // A scopo di simulazione, la prima misurazione registrata è sempre ottima
    Measurement measurement = new Measurement();
    measurement.setSensorId(createdHiveId);
    measurement.setHiveId(createdHiveId);
    measurement.setMeasurementDate(LocalDate.now().atTime(12, 0));
    measurement.setWeight(80);
    measurement.setSpectrogram("bzzz");
    measurement.setTemperature(34.5);
    measurement.setAmbientTemperature(23);
    measurement.setHumidity(20);
    measurement.setAmbientHumidity(20);
    measurement.setQueenPresent(true);
    measurementDAO.save(measurement);
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

  public int getBeekeeperHivesCount(String beekeeperEmail) {
    return hiveDAO.countByBeekeeperEmail(beekeeperEmail);
  }

  public void deleteHive(int id){
    hiveDAO.deleteById(id);
  }
}
