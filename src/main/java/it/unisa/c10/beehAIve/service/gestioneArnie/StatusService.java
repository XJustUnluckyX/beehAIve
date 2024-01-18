package it.unisa.c10.beehAIve.service.gestioneArnie;

import com.google.gson.Gson;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;
import com.mysql.cj.util.Base64Decoder;
import it.unisa.c10.beehAIve.persistence.dao.AnomalyDAO;
import it.unisa.c10.beehAIve.persistence.dao.HiveDAO;
import it.unisa.c10.beehAIve.persistence.dao.MeasurementDAO;
import it.unisa.c10.beehAIve.persistence.dao.OperationDAO;
import it.unisa.c10.beehAIve.persistence.entities.Anomaly;
import it.unisa.c10.beehAIve.persistence.entities.Hive;
import it.unisa.c10.beehAIve.persistence.entities.Measurement;
import it.unisa.c10.beehAIve.persistence.entities.Operation;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/*
La seguente classe deve supportare le seguenti operazioni:
1. Creare il grafico della temperatura dell'arnia nel tempo.
2. Creare il grafico del peso dell'arnia nel tempo.
3. Creare il grafico dell'umidità dell'arnia nel tempo.
4. Creare il grafico della presenza della regina nel tempo.
5. Inviare una notifica in caso di anomalia.
6. Impostare un'anomalia come "risolta".
7. Eliminare un'anomalia.
8. Generare un report di salute.
 */

@Service
public class StatusService {

  private MeasurementDAO measurementDAO;
  private AnomalyDAO anomalyDAO;
  private OperationDAO operationDAO;

  private HiveDAO hiveDAO;
  @Autowired
  public StatusService(MeasurementDAO measurementDAO, AnomalyDAO anomalyDAO, OperationDAO operationDAO, HiveDAO hiveDAO) {
    this.measurementDAO = measurementDAO;
    this.anomalyDAO = anomalyDAO;
    this.operationDAO = operationDAO;
    this.hiveDAO = hiveDAO;
  }

  public List<ArrayList<Object>> getGraphData (int hiveId) {
    // Prendiamo le ultime 48 misurazioni
    List<Measurement> measurements = measurementDAO.findByHiveIdOrderByMeasurementDateAsc(hiveId); //TODO Change

    List<ArrayList<Object>> result = new ArrayList<>();

    // Per ogni misurazione andiamo a inserire i dati seguendo il formato di AnyChart
    for (Measurement m : measurements) {

      ArrayList<Object> list = new ArrayList<>();

      // Inseriamo la data, che fungerà da asse delle x
      list.add(m.getMeasurementDate().toString().replace("T", " "));
      // Inseriamo i dati relativi a quella data
      list.add(m.getWeight());
      list.add(m.getHumidity());
      list.add(m.getTemperature());
      // Andiamo a inserire dei numeri per plottare la regina in una forma più visibile
      if (m.isQueenPresent())
        list.add(0);
      else
        list.add(-20);

      // Aggiungiamo la lista al risultato
      result.add(list);

    }

    return result;

  }

  public void notifyAnomaly (Anomaly anomaly) {
    Hive hive = hiveDAO.findById(anomaly.getHiveId()).get();

    // Se l'arnia è in salute in base all'anomalia cambiamo lo stato
    if (hive.getHiveHealth() == 1) {
      if (anomaly.getAnomalyName().equals("Possible CCD"))
        hive.setHiveHealth(3);
      else
        hive.setHiveHealth(2);
    }

    // Se l'arnia è in pericolo medio aggiorniamo solo se l'anomalia è grave
    if (hive.getHiveHealth() == 2)
      if (anomaly.getAnomalyName().equals("Possible CCD"))
        hive.setHiveHealth(3);

    hiveDAO.save(hive);
  }

  public void resolveAnomaly (int anomalyId) {

    // Prendiamo l'anomalia dal database
    Anomaly anomaly = anomalyDAO.findById(anomalyId).get();

    // Impostiamola a risolta
    anomaly.setResolved(true);

    // Salviamo nel database l'anomalia sistemata
    anomalyDAO.save(anomaly);

    // Prendiamo l'arnia per controllare se va cambiato lo stato di salute
    Hive hive = hiveDAO.findById(anomaly.getHiveId()).get();

    // Operazione per il nuovo stato di salute
    hive.setHiveHealth(getNewHealthStatus(hive.getId()));

    // Salviamo l'arnia nel DB
    hiveDAO.save(hive);

  }

  public void deleteAnomaly (int anomalyId) {

    // Prendiamo l'anomalia dal database per ricavarne l'arnia dopo
    Anomaly anomaly = anomalyDAO.findById(anomalyId).get();

    // Eliminiamo l'arnia dal database
    anomalyDAO.deleteById(anomalyId);

    // Prendiamo l'arnia per controllare se va cambiato lo stato di salute
    Hive hive = hiveDAO.findById(anomaly.getHiveId()).get();

    // Operazione per il nuovo stato di salute
    hive.setHiveHealth(getNewHealthStatus(hive.getId()));

    // Salviamo l'arnia nel DB
    hiveDAO.save(hive);

  }

  private int getNewHealthStatus (int hiveId) {

    List<Anomaly> anomalies = anomalyDAO.findByHiveIdAndResolvedFalse(hiveId);

    // Se non ci sono anomalie l'arnia è in salute
    if (anomalies.isEmpty())
      return 1;

    // Controlla se ci sono anomalie di Possibile CCD
    for (Anomaly a : anomalies)
      if (a.getAnomalyName().equals("Possible CCD"))
        return 3;

    // Le uniche anomalie rimaste sono di gravità media
    return 2;

  }

  public void generateReport (int hiveId, HttpServletResponse response) throws IOException {

    Hive hive = hiveDAO.findById(hiveId).get();

    List<Operation> notCompletedOperations = operationDAO.findAllByOperationStatusAndHiveId("Not Completed", hiveId);
    List<Operation> completedOperations = operationDAO.findAllByOperationStatusAndHiveId("Completed", hiveId);

    List<Anomaly> unresolvedAnomalies = anomalyDAO.findByHiveIdAndResolvedFalse(hiveId);
    List<Anomaly> resolvedAnomalies = anomalyDAO.findByHiveIdAndResolvedTrue(hiveId);

    Document document = new Document(PageSize.A4);

    PdfWriter.getInstance(document, response.getOutputStream());

    document.open();

    Font titleFont = FontFactory.getFont(FontFactory.HELVETICA);
    titleFont.setSize(20);

    Font baseFont = FontFactory.getFont(FontFactory.HELVETICA);
    baseFont.setSize(12);

    Paragraph reportTitle = new Paragraph("Health report of " + hive.getNickname(), titleFont);
    reportTitle.setAlignment(Paragraph.ALIGN_CENTER);
    document.add(reportTitle);

    LineSeparator separator = new LineSeparator();
    separator.setOffset(-5);
    document.add(separator);

    document.add(Chunk.NEWLINE);

    document.add(new Paragraph("Hive Type: " + hive.getHiveType(),baseFont));

    document.add(new Paragraph("Bee Species: " + hive.getBeeSpecies(),baseFont));

    document.add(new Paragraph("Hive added on: " + hive.getCreationDate(),baseFont));

    document.add(Chunk.NEWLINE);

    document.add(separator);

    document.add(Chunk.NEWLINE);

    Paragraph imminentOperationsTitle = new Paragraph("Imminent Operations", titleFont);
    imminentOperationsTitle.setAlignment(Paragraph.ALIGN_CENTER);
    document.add(imminentOperationsTitle);

    com.lowagie.text.List unfinishedOperations = new com.lowagie.text.List();
    unfinishedOperations.setSymbolIndent(12);
    unfinishedOperations.setListSymbol("•");

    for (Operation o : notCompletedOperations)
      unfinishedOperations.add(new ListItem(" [" + o.getOperationType() + "] " + o.getOperationName() + " Planned on: " + o.getOperationDate()));

    document.add(unfinishedOperations);

    document.add(Chunk.NEWLINE);

    document.add(separator);

    document.add(Chunk.NEWLINE);

    Paragraph pastOperationsTitle = new Paragraph("Past Operations", titleFont);
    pastOperationsTitle.setAlignment(Paragraph.ALIGN_CENTER);
    document.add(pastOperationsTitle);

    com.lowagie.text.List pastOperations = new com.lowagie.text.List();
    pastOperations.setSymbolIndent(12);
    pastOperations.setListSymbol("•");

    for (Operation o : completedOperations)
      pastOperations.add(new ListItem(" [" + o.getOperationType() + "] " + o.getOperationName() + " Done on: " + o.getOperationDate()));

    document.add(pastOperations);

    document.add(Chunk.NEWLINE);

    document.add(separator);

    document.add(Chunk.NEWLINE);

    Paragraph unresolvedAnomaliesTitle = new Paragraph("Unresolved Anomalies", titleFont);
    unresolvedAnomaliesTitle.setAlignment(Paragraph.ALIGN_CENTER);
    document.add(unresolvedAnomaliesTitle);

    com.lowagie.text.List unresolvedAnomaliesList = new com.lowagie.text.List();
    unresolvedAnomaliesList.setSymbolIndent(12);
    unresolvedAnomaliesList.setListSymbol("•");

    for (Anomaly a : unresolvedAnomalies)
      unresolvedAnomaliesList.add(new ListItem(" [" + a.getAnomalyName() + "] Detected on: " + a.getDetectionDate().toString().replace("T"," ")));

    document.add(unresolvedAnomaliesList);

    document.add(Chunk.NEWLINE);

    document.add(separator);

    document.add(Chunk.NEWLINE);

    Paragraph resolvedAnomaliesTitle = new Paragraph("Past Anomalies", titleFont);
    resolvedAnomaliesTitle.setAlignment(Paragraph.ALIGN_CENTER);
    document.add(resolvedAnomaliesTitle);

    com.lowagie.text.List resolvedAnomaliesList = new com.lowagie.text.List();
    resolvedAnomaliesList.setSymbolIndent(12);
    resolvedAnomaliesList.setListSymbol("•");

    for (Anomaly a : resolvedAnomalies)
      resolvedAnomaliesList.add(new ListItem(" [" + a.getAnomalyName() + "] Detected on: " + a.getDetectionDate().toString().replace("T"," ")));

    document.add(resolvedAnomaliesList);

    document.add(Chunk.NEWLINE);

    document.add(separator);

    document.add(Chunk.NEWLINE);

    LocalDate today = LocalDate.now();

    Paragraph date = new Paragraph("Report created on: " + today, baseFont);
    date.setAlignment(Paragraph.ALIGN_RIGHT);
    document.add(date);

    document.close();

  }


}
