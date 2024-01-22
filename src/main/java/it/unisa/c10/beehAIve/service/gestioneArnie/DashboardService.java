package it.unisa.c10.beehAIve.service.gestioneArnie;

import it.unisa.c10.beehAIve.persistence.dao.HiveDAO;
import it.unisa.c10.beehAIve.persistence.dao.MeasurementDAO;
import it.unisa.c10.beehAIve.persistence.dao.SensorDAO;
import it.unisa.c10.beehAIve.persistence.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class DashboardService {
  private final HiveDAO hiveDAO;
  private final SensorDAO sensorDAO;
  private final MeasurementDAO measurementDAO;

  @Autowired
  public DashboardService(HiveDAO hiveDAO, SensorDAO sensorDAO, MeasurementDAO measurementDAO){
    this.hiveDAO = hiveDAO;
    this.sensorDAO = sensorDAO;
    this.measurementDAO = measurementDAO;
  }

  /**
   * Permette di creare un'arnia all'interno del database.
   * @param beekeeperEmail L'email dell'apicoltore che sta creando l'arnia.
   * @param nickname Il nome dell'arnia.
   * @param hiveType La tipologia dell'arnia.
   * @param beeSpecies La specie delle api contenute nell'arnia.
   */
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

    int createdSensorId = sensorDAO.findTopByBeekeeperEmailOrderByIdDesc(beekeeperEmail).getId();

    // Creazione della prima misurazione relativa all'arnia appena creata e salvataggio nel database
    // A scopo di simulazione, la prima misurazione registrata è sempre ottima
    Measurement measurement = new Measurement();
    measurement.setSensorId(createdSensorId);
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

  /**
   * Prende un'arnia dal database.
   * @param hiveId L'id dell'arnia da prendere.
   * @return L'{@code Hive} che corrisponde a quell'id.
   */
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

  /**
   * Permette di modificare un'arnia nel database.
   * @param hiveId L'id dell'arnia da modificare.
   * @param nickname Il nuovo nome dell'arnia.
   * @param hiveType La nuova tipologia dell'arnia.
   * @param beeSpecies La nuova specie delle api contenute nell'arnia.
   */
  public void modifyHive(int hiveId, String nickname, String hiveType, String beeSpecies){
    Hive hive = getHive(hiveId);

    hive.setNickname(nickname);
    hive.setHiveType(hiveType);
    hive.setBeeSpecies(beeSpecies);

    hiveDAO.save(hive);
  }

  /**
   * Permette di eliminare un'arnia nel database.
   * @param id L'id dell'arnia da eliminare.
   */
  public void deleteHive(int id){
    hiveDAO.deleteById(id);
  }

  /**
   * Prende tutte le arnie di un apicoltore dal database.
   * @param beekeeperEmail L'email dell'apicoltore di cui vogliamo le arnie.
   * @return La {@code List} di {@code Hive} dell'apicoltore.
   */
  public List<Hive> getBeekeeperHives(String beekeeperEmail){
    return hiveDAO.findByBeekeeperEmail(beekeeperEmail);
  }

  /**
   * Prende tutte le arnie di un apicoltore che contengono una stringa nel nome.
   * @param beekeeperEmail L'email dell'apicoltore di cui vogliamo le arnie.
   * @param nickname La stringa che cerchiamo all'interno del nome dell'arnia
   * @return La {@code List} di {@code Hive} dell'apicoltore che corrisponde ai parametri di ricerca.
   */
  public List<Hive> getBeekeeperHivesByNickname(String beekeeperEmail, String nickname) {
    return hiveDAO.findByNicknameContainingAndBeekeeperEmail(nickname, beekeeperEmail);
  }

  /**
   * Prende tutte le arnie di un apicoltore che hanno operazioni imminenti
   * @param beekeeperEmail L'email dell'apicoltore di cui vogliamo le arnie.
   * @return La {@code List} di {@code Hive} dell'apicoltore che corrisponde ai parametri di ricerca.
   */
  public List<Hive> getBeekeeperHivesWithScheduledOperations(String beekeeperEmail) {
    return hiveDAO.findByUncompletedOperationsTrueAndBeekeeperEmail(beekeeperEmail);
  }

  /**
   * Prende tutte le arnie di un apicoltore che presentano uno stato di salute non ottimale.
   * @param beekeeperEmail L'email dell'apicoltore di cui vogliamo le arnie.
   * @return La {@code List} di {@code Hive} dell'apicoltore che corrisponde ai parametri di ricerca.
   */
  public List<Hive> getBeekeeperHivesWithHealthIssues(String beekeeperEmail) {
    return hiveDAO.findByHealthIssuesAndBeekeeperEmail(beekeeperEmail);
  }

  /**
   * Prende tutte le arnie di un apicoltore che hanno operazioni imminenti e che contengono una stringa nel nome.
   * @param beekeeperEmail L'email dell'apicoltore di cui vogliamo le arnie.
   * @param nickname La stringa che cerchiamo all'interno del nome dell'arnia
   * @return La {@code List} di {@code Hive} dell'apicoltore che corrisponde ai parametri di ricerca.
   */
  public List<Hive> getBeekeeperHivesByNicknameAndScheduledOperations(String beekeeperEmail,
                                                                      String nickname) {
    return hiveDAO.findByNicknameContainingAndUncompletedOperationsTrueAndBeekeeperEmail(nickname,
        beekeeperEmail);
  }

  /**
   * Prende tutte le arnie di un apicoltore che presentano uno stato di salute non ottimale e che contengono una stringa nel nome.
   * @param beekeeperEmail L'email dell'apicoltore di cui vogliamo le arnie.
   * @param nickname La stringa che cerchiamo all'interno del nome dell'arnia
   * @return La {@code List} di {@code Hive} dell'apicoltore che corrisponde ai parametri di ricerca.
   */
  public List<Hive> getBeekeeperHivesByNicknameAndHealthIssues(String beekeeperEmail,
                                                               String nickname) {
    return hiveDAO.findByNicknameContainingAndHealthIssuesAndBeekeeperEmail(nickname,
        beekeeperEmail);
  }

  /**
   * Conta il numero di arnie di un apicoltore
   * @param beekeeperEmail L'email dell'apicoltore di cui vogliamo il numero di arnie.
   * @return Il numero di arnie di quell'apicoltore.
   */
  public int getBeekeeperHivesCount(String beekeeperEmail) {
    return hiveDAO.countByBeekeeperEmail(beekeeperEmail);
  }

}
