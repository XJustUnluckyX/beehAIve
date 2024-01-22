package it.unisa.c10.beehAIve.service.gestioneArnie;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;
import it.unisa.c10.beehAIve.persistence.dao.AnomalyDAO;
import it.unisa.c10.beehAIve.persistence.dao.HiveDAO;
import it.unisa.c10.beehAIve.persistence.dao.MeasurementDAO;
import it.unisa.c10.beehAIve.persistence.dao.OperationDAO;
import it.unisa.c10.beehAIve.persistence.entities.Anomaly;
import it.unisa.c10.beehAIve.persistence.entities.Hive;
import it.unisa.c10.beehAIve.persistence.entities.Measurement;
import it.unisa.c10.beehAIve.persistence.entities.Operation;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

  /**
   * Restituisce tutti i dati raccolti dalle misurazioni effettuate nelle ultime 48 ore relativi a una specifica arnia,
   * in un formato leggibile al grafico che le dovr&agrave; mostrare.
   * @param hiveId L'ID dell'arnia di cui si vogliono ottenere i dati delle misurazioni.
   * @return Una lista contenente i dati delle misurazioni effettuate nelle ultime 48 ore sull'arnia specificata.
   */
  public List<ArrayList<Object>> getGraphData (int hiveId) {
    // Prendiamo le ultime 48 misurazioni
    List<Measurement> measurements = measurementDAO.findFirst49ByHiveIdOrderByMeasurementDateDesc(hiveId);

    // Invertiamo la lista per rendere il grafico leggibile
    Collections.reverse(measurements);

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

  /**
   * Genera un report di salute di un'arnia, restituito all'utente come documento PDF. Il rapporto include informazioni
   * sull'arnia, tra cui operazioni pianificate e completate, anomalie non risolte e anomalie risolte.
   * @param hiveId L'ID dell'arnia di cui vogliamo generare il report.
   * @param response La risposta HTTP in cui inserire il file PDF da scaricare.
   * @throws IOException Nel caso in cui non si riesca ad aprire la response per scrivere al suo interno.
   */
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

  /**
   * Restituisce l'ultima misurazione effettuata di un'arnia.
   * @param hiveId L'ID dell'arnia di cui vogliamo ottenere l'ultima misurazione.
   * @return L'oggetto {@code Measurement} contenente le informazioni relative all'arnia.
   */
  public Measurement getHiveLastMeasurement(int hiveId) {
    return measurementDAO.findTopByHiveIdOrderByMeasurementDateDesc(hiveId);
  }

}
