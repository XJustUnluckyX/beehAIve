package it.unisa.c10.beehAIve;

import it.unisa.c10.beehAIve.persistence.dao.*;
import it.unisa.c10.beehAIve.persistence.entities.*;
import it.unisa.c10.beehAIve.service.gestioneAnomalie.AnomalyService;
import it.unisa.c10.beehAIve.service.gestioneArnie.DashboardService;
import it.unisa.c10.beehAIve.service.gestioneArnie.OperationService;
import it.unisa.c10.beehAIve.service.gestioneArnie.StatusService;
import it.unisa.c10.beehAIve.service.gestioneUtente.ProfileService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.mockito.stubbing.Answer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class BeehAIveUnitTests {

  @Mock
  private HiveDAO hiveDAO;
  @Mock
  private BeekeeperDAO beekeeperDAO;
  @Mock
  private AnomalyDAO anomalyDAO;
  @Mock
  private OperationDAO operationDAO;
  @Mock
  private MeasurementDAO measurementDAO;
  @Mock
  private SensorDAO sensorDAO;

  private OperationService operationService;
  private ProfileService profileService;
  private StatusService statusService;
  private AnomalyService anomalyService;
  private DashboardService dashboardService;

  @BeforeEach
  void setUp() {
    this.operationService = new OperationService(operationDAO, hiveDAO);
    this.profileService = new ProfileService(beekeeperDAO);
    this.statusService = new StatusService(measurementDAO, anomalyDAO, operationDAO, hiveDAO);
    this.anomalyService = new AnomalyService(anomalyDAO, hiveDAO, this.statusService);
    this.dashboardService = new DashboardService(hiveDAO, sensorDAO, measurementDAO);
  }

  @AfterEach
  void tearDown() {
  }

  static class HiveProvider implements ArgumentsProvider {
    @Override
    public Stream<Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {

      Hive h1 = new Hive();
      h1.setId(1);
      h1.setNickname("Test1");
      h1.setHiveType("Langstroth");
      h1.setCreationDate(LocalDate.now());
      h1.setBeekeeperEmail("a.depasquale@gmail.com");
      h1.setBeeSpecies("Apis mellifera");
      h1.setHiveHealth(1);
      h1.setUncompletedOperations(false);

      Hive h2 = new Hive();
      h2.setId(2);
      h2.setNickname("Test2");
      h2.setHiveType("Warre");
      h2.setCreationDate(LocalDate.now());
      h2.setBeekeeperEmail("n.gallotta@gmail.com");
      h2.setBeeSpecies("Apis mellifera");
      h2.setHiveHealth(1);
      h2.setUncompletedOperations(true);

      Hive h3 = new Hive();
      h3.setId(1);
      h3.setNickname("Test3");
      h3.setHiveType("Top-Bar");
      h3.setCreationDate(LocalDate.now());
      h3.setBeekeeperEmail("s.valente@gmail.com");
      h3.setBeeSpecies("Apis mellifera");
      h3.setHiveHealth(1);
      h3.setUncompletedOperations(false);

      return Stream.of(Arguments.of(h1), Arguments.of(h2), Arguments.of(h3));
    }

  }

  static class AnomalyProvider implements ArgumentsProvider {

    @Override
    public Stream<Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {

      Anomaly a1 = new Anomaly();
      a1.setId(1);
      a1.setAnomalyName("Low Humidity");
      a1.setResolved(false);
      a1.setDetectionDate(LocalDateTime.now());
      a1.setSensorId(1);
      a1.setHiveId(1);
      a1.setBeekeeperEmail("n.gallotta@gmail.com");

      Anomaly a2 = new Anomaly();
      a2.setId(2);
      a2.setAnomalyName("Possible CCD");
      a2.setResolved(true);
      a2.setDetectionDate(LocalDateTime.now());
      a2.setSensorId(2);
      a2.setHiveId(2);
      a2.setBeekeeperEmail("s.valente@gmail.com");

      return Stream.of(Arguments.of(a1), Arguments.of(a2));
    }

  }

  static class OperationProvider implements ArgumentsProvider{
    @Override
    public Stream<Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {

      Operation o1 = new Operation();
      o1.setId(1);
      o1.setOperationName("Health Check");
      o1.setOperationType("Medical Inspection");
      o1.setOperationStatus("Completed");
      o1.setOperationDate(LocalDateTime.now());
      o1.setNotes("test Notes 1");
      o1.setHiveId(1);
      o1.setBeekeeperEmail("f.festa@gmail.com");

      Operation o2 = new Operation();
      o2.setId(2);
      o2.setOperationName("Population Monthly Check");
      o2.setOperationType("Check population");
      o2.setOperationStatus("Not completed");
      o2.setOperationDate(LocalDateTime.now());
      o2.setNotes("test Notes 2");
      o2.setHiveId(2);
      o2.setBeekeeperEmail("a.depasquale@gmail.com");

      Operation o3 = new Operation();
      o3.setId(3);
      o3.setOperationName("Fix Hive Floor");
      o3.setOperationType("Maintenance");
      o3.setOperationStatus("Completed");
      o3.setOperationDate(LocalDateTime.now());
      o3.setNotes("test Notes 3");
      o3.setHiveId(3);
      o3.setBeekeeperEmail("s.valente@gmail.com");


      return Stream.of(Arguments.of(o1), Arguments.of(o2), Arguments.of(o3));
    }

  }



  // Test di createHive di DashboardService
  @ParameterizedTest
  @ArgumentsSource(HiveProvider.class)
  @DisplayName("Test creazione arnia avvenuto con successo")
  public void testCreateHiveSuccess(Hive h) {

    List<Hive> hiveDatabaseMock = new ArrayList<>();
    List<Sensor> sensorDatabaseMock = new ArrayList<>();

    when(hiveDAO.save(any(Hive.class))).thenAnswer(new Answer<Hive>() {
      @Override
      public Hive answer(InvocationOnMock invocationOnMock) throws Throwable {
        hiveDatabaseMock.add((Hive) invocationOnMock.getArguments()[0]);
        return null;
      }
    });

    when(hiveDAO.findTopByBeekeeperEmailOrderByIdDesc(any(String.class))).thenReturn(h);

    when(sensorDAO.save(any(Sensor.class))).thenAnswer(new Answer<Sensor>() {
      @Override
      public Sensor answer(InvocationOnMock invocationOnMock) throws Throwable {
        sensorDatabaseMock.add((Sensor) invocationOnMock.getArguments()[0]);
        return null;
      }
    });

    Sensor mockSensor = new Sensor();
    mockSensor.setId(h.getId());

    when(sensorDAO.findTopByBeekeeperEmailOrderByIdDesc(any(String.class))).thenReturn(mockSensor);

    when(measurementDAO.save(any(Measurement.class))).thenAnswer(new Answer<Measurement>() {
      @Override
      public Measurement answer(InvocationOnMock invocationOnMock) throws Throwable {
        return null;
      }
    });

    dashboardService.createHive(h.getBeekeeperEmail(),h.getNickname(),h.getHiveType(),h.getBeeSpecies());

    Assertions.assertEquals(hiveDatabaseMock.size(),1);
    Assertions.assertEquals(sensorDatabaseMock.size(),1);
    Assertions.assertEquals(hiveDatabaseMock.get(0).getNickname(),h.getNickname());

  }
  @ParameterizedTest
  @ArgumentsSource(HiveProvider.class)
  @DisplayName("Test creazione arnia fallita")
  public void testCreateHiveError(Hive h) {

    List<Hive> hiveDatabaseMock = new ArrayList<>();
    List<Sensor> sensorDatabaseMock = new ArrayList<>();

    when(hiveDAO.save(any(Hive.class))).thenAnswer(new Answer<Hive>() {
      @Override
      public Hive answer(InvocationOnMock invocationOnMock) throws Throwable {
        // In questo caso facciamo finta che non aggiunge al database
        return null;
      }
    });

    when(hiveDAO.findTopByBeekeeperEmailOrderByIdDesc(any(String.class))).thenReturn(h);

    when(sensorDAO.save(any(Sensor.class))).thenAnswer(new Answer<Sensor>() {
      @Override
      public Sensor answer(InvocationOnMock invocationOnMock) throws Throwable {
        // In questo caso facciamo finta che non aggiunge al database
        return null;
      }
    });

    Sensor mockSensor = new Sensor();
    mockSensor.setId(h.getId());

    when(sensorDAO.findTopByBeekeeperEmailOrderByIdDesc(any(String.class))).thenReturn(mockSensor);

    when(measurementDAO.save(any(Measurement.class))).thenAnswer(new Answer<Measurement>() {
      @Override
      public Measurement answer(InvocationOnMock invocationOnMock) throws Throwable {
        return null;
      }
    });

    dashboardService.createHive(h.getBeekeeperEmail(),h.getNickname(),h.getHiveType(),h.getBeeSpecies());

    Assertions.assertEquals(hiveDatabaseMock.size(),0);
    Assertions.assertEquals(sensorDatabaseMock.size(),0);

  }

  // Test del metodo deleteHive di DashboardService
  @ParameterizedTest
  @ArgumentsSource(HiveProvider.class)
  @DisplayName("Test eliminazione arnia avvenuta con successo")
  public void testDeleteHiveSuccess(Hive h) {

    List<Hive> hiveDatabaseMock = new ArrayList<>();
    hiveDatabaseMock.add(h);
    doAnswer(new Answer() {
      @Override
      public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
        hiveDatabaseMock.remove(h);
        return null;
      }
    }).when(hiveDAO).deleteById(any(Integer.class));

    dashboardService.deleteHive(h.getId());

    Assertions.assertEquals(hiveDatabaseMock.size(),0);

  }

  @ParameterizedTest
  @ArgumentsSource(HiveProvider.class)
  @DisplayName("Test eliminazione arnia fallita")
  public void testDeleteHiveError(Hive h) {
    List<Hive> hiveDatabaseMock = new ArrayList<>();
    hiveDatabaseMock.add(h);
    doAnswer(new Answer() {
      @Override
      public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
        // In questo caso facciamo finta che non rimuove dal database
        return null;
      }
    }).when(hiveDAO).deleteById(any(Integer.class));

    dashboardService.deleteHive(h.getId());

    Assertions.assertEquals(hiveDatabaseMock.size(),1);

  }

  // Test di registration di ProfileService
  @Test
  @DisplayName("Test registrazione avvenuta con successo")
  public void testRegistrationSuccess() {
    String email = "n.gallotta@gmail.com";
    String password = "Password-123";
    String firstName = "Nicolò";
    String lastName = "Gallotta";
    String companyName = "The London Bee Company";
    String companyPiva = "123456789";
    Beekeeper beekeeper = new Beekeeper();
    beekeeper.setEmail(email);
    beekeeper.setPasswordhash(password);
    beekeeper.setFirstName(firstName);
    beekeeper.setLastName(lastName);
    beekeeper.setCompanyName(companyName);
    beekeeper.setCompanyPiva(companyPiva);

    when(beekeeperDAO.save(any(Beekeeper.class))).thenReturn(beekeeper);
    Beekeeper realBeekeeper =
      profileService.registration(email, password, firstName, lastName, companyName, companyPiva);

    Assertions.assertEquals(beekeeper, realBeekeeper);
  }
  @Test
  @DisplayName("Test registrazione fallita (email già esistente)")
  public void testRegistrationAlreadyExists() {
    String email = "n.gallotta@gmail.com";
    String password = "Password-123";
    String firstName = "Nicolò";
    String lastName = "Gallotta";
    String companyName = "The London Bee Company";
    String companyPiva = "123456789";
    Beekeeper beekeeper = new Beekeeper();
    beekeeper.setEmail(email);
    beekeeper.setPasswordhash(password);
    beekeeper.setFirstName(firstName);
    beekeeper.setLastName(lastName);
    beekeeper.setCompanyName(companyName);
    beekeeper.setCompanyPiva(companyPiva);

    when(beekeeperDAO.save(any(Beekeeper.class))).thenAnswer(new Answer<Beekeeper>() {
      @Override
      public Beekeeper answer(InvocationOnMock invocationOnMock) throws Throwable {
        Beekeeper beekeeperInvocation = (Beekeeper) invocationOnMock.getArguments()[0];
        if (beekeeperInvocation.getEmail().equals(email)) {
          return null;
        }
        return beekeeperInvocation;
      }
    });

    Beekeeper realBeekeeper =
      profileService.registration(email, password, firstName, lastName, companyName, companyPiva);

    Assertions.assertNull(realBeekeeper);
  }
  @Test
  @DisplayName("Test registrazione fallita")
  public void testRegistrationError() {
    String email = "n.gallotta@gmail.com";
    String password = "Password-123";
    String firstName = "Nicolò";
    String lastName = "Gallotta";
    String companyName = "The London Bee Company";
    String companyPiva = "123456789";
    Beekeeper beekeeper = new Beekeeper();
    beekeeper.setEmail(email);
    beekeeper.setPasswordhash(password);
    beekeeper.setFirstName(firstName);
    beekeeper.setLastName(lastName);
    beekeeper.setCompanyName(companyName);
    beekeeper.setCompanyPiva(companyPiva);

    when(beekeeperDAO.save(any(Beekeeper.class))).thenReturn(null);
    Beekeeper realBeekeeper =
      profileService.registration(email, password, firstName, lastName, companyName, companyPiva);

    Assertions.assertNull(realBeekeeper);
  }

  // Test di retrieveOperationFromDB di OperationService
  @ParameterizedTest
  @ArgumentsSource(OperationProvider.class)
  @DisplayName("Test recupero intervento avvenuto con successo")
  public void testRetrieveOperationFromDBSuccess(Operation o) {

    when(operationDAO.findById(o.getId())).thenReturn(Optional.ofNullable(o));

    Operation realOperation = operationService.retrieveOperationFromDB(o.getId());

    Assertions.assertEquals(realOperation.getOperationName(), o.getOperationName());
  }
  @ParameterizedTest
  @ArgumentsSource(OperationProvider.class)
  @DisplayName("Test recupero intervento fallito")
  public void testRetrieveOperationFromDBError(Operation o) {
    when(operationDAO.findById(o.getId())).thenReturn(Optional.empty());

    Operation realOperation = operationService.retrieveOperationFromDB(o.getId());

    Assertions.assertNull(realOperation);
  }

  // Test di planningOperation di OperationService
  @ParameterizedTest
  @ArgumentsSource(OperationProvider.class)
  @DisplayName("Test pianificazione operazione avvenuta con successo")
  public void testPlanningOperationSuccess(Operation o) {

    //Mock del database
    List<Operation> databaseMock = new ArrayList<>();

    when(operationDAO.save(any(Operation.class))).thenAnswer(new Answer<Operation>() {
      @Override
      public Operation answer(InvocationOnMock invocationOnMock) throws Throwable {
        databaseMock.add((Operation) invocationOnMock.getArguments()[0]);
        return (Operation) invocationOnMock.getArguments()[0];
      }
    });

    operationService.planningOperation(o.getOperationName(),o.getOperationType(),o.getOperationStatus(),o.getOperationDate(),o.getNotes(),o.getHiveId(),o.getBeekeeperEmail());

    Assertions.assertEquals(databaseMock.get(0).getOperationName(),o.getOperationName());

  }

  @ParameterizedTest
  @ArgumentsSource(OperationProvider.class)
  @DisplayName("Test pianificazione operazione fallita")
  public void testPlanningOperationError(Operation o) {

    //Mock del database
    List<Operation> databaseMock = new ArrayList<>();

    when(operationDAO.save(any(Operation.class))).thenAnswer(new Answer<Operation>() {
      @Override
      public Operation answer(InvocationOnMock invocationOnMock) throws Throwable {
        // In questo caso facciamo finta che non aggiunge al database
        return o;
      }
    });

    operationService.planningOperation(o.getOperationName(),o.getOperationType(),o.getOperationStatus(),o.getOperationDate(),o.getNotes(),o.getHiveId(),o.getBeekeeperEmail());

    Assertions.assertEquals(databaseMock.size(),0);

  }

  // Test del metodo resolveAnomaly di AnomalyService
  @ParameterizedTest
  @ArgumentsSource(AnomalyProvider.class)
  @DisplayName("Test risoluzione anomalia avvenuta con successo")
  public void testResolveAnomalySuccess(Anomaly a) {

    when(anomalyDAO.findById(any(Integer.class))).thenReturn(Optional.ofNullable(a));

    when(anomalyDAO.save(any(Anomaly.class))).thenReturn(null);

    Hive h1 = new Hive();
    h1.setId(a.getHiveId());
    when(hiveDAO.findById(any(Integer.class))).thenReturn(Optional.ofNullable(h1));

    when(hiveDAO.save(any(Hive.class))).thenReturn(null);

    anomalyService.resolveAnomaly(a.getHiveId());

    Assertions.assertTrue(a.isResolved());

  }
  @ParameterizedTest
  @ArgumentsSource(AnomalyProvider.class)
  @DisplayName("Test risoluzione anomalia fallita")
  public void testResolveAnomalyError(Anomaly a) {

    boolean result = a.isResolved();

    when(anomalyDAO.findById(any(Integer.class))).thenReturn(Optional.ofNullable(a));

    when(anomalyDAO.save(any(Anomaly.class))).thenAnswer(new Answer<Anomaly>() {
      @Override
      public Anomaly answer(InvocationOnMock invocationOnMock) throws Throwable {
        // Facciamo finta che non salvi al database
        a.setResolved(result);
        return null;
      }
    });

    Hive h1 = new Hive();
    h1.setId(a.getHiveId());
    when(hiveDAO.findById(any(Integer.class))).thenReturn(Optional.ofNullable(h1));

    when(hiveDAO.save(any(Hive.class))).thenReturn(null);

    anomalyService.resolveAnomaly(a.getHiveId());

    Assertions.assertEquals(a.isResolved(), result);

  }

}
