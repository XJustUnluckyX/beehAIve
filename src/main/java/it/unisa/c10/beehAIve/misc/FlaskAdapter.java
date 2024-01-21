package it.unisa.c10.beehAIve.misc;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

//Converte una misurazione in JSON, comunica col microservice in Flask e riconverte la risposta in un oggetto Previsione
public class FlaskAdapter {

  // Questo metodo fa fare una previsione alla CNN partendo dal nome di uno spettrogramma
  public boolean predictQueenPresence (String fileName) {
    HttpURLConnection connection = null;
    DataOutputStream outputStream = null;
    boolean result = true;
    try {
      // Istanzia la connessione sull'ip
      URL url = new URL("http://127.0.0.1:5000/use_cnn");
      // Formattiamo in formato JSON
      String[] inputData = {"{\"file name\" : \"" + fileName + "\"}"};
      for (String input : inputData) {
        // Scriviamo il nostro file JSON sulla connessione
        byte[] postDataToSend = input.getBytes(StandardCharsets.UTF_8);
        connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("charset", "utf-8");
        connection.setRequestProperty("Content-Length", Integer.toString(input.length()));
        outputStream = new DataOutputStream(connection.getOutputStream());
        outputStream.write(postDataToSend);
        outputStream.flush();

        if (connection.getResponseCode() != 200) {
          throw new RuntimeException("HTTP Error Code: " + connection.getResponseCode());
        }

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        // Leggiamo il nostro output
        String output;
        while ((output = bufferedReader.readLine()) != null) {
          result = (Integer.parseInt(output) == 1);
        }
        connection.disconnect();
      }
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      if (connection != null)
        connection.disconnect();
    }
    return result;
  }

  // Questo metodo fa fare una previsione al modello partendo dalle feature
  public boolean predictCCD (double hiveTemp, double ambientTemp, double hiveHumidity, double ambientHumidity, boolean queenPresence) {
    // Formattiamo la presenza della regina in un formato leggibile al modello
    int queenPresenceFormatted = 1;
    if (queenPresence)
      queenPresenceFormatted = 1;
    else
      queenPresenceFormatted = 0;

    // Calcoliamo la temperatura percepita dell'arnia attraverso l'indice Humidex
    double hiveVaporTension = 6.112 * Math.pow(10, ((7.5 * hiveTemp)/(237.7 + hiveTemp))) * (hiveHumidity / 100.0);
    double hiveApparentTemp = hiveTemp + (5.0/9.0 * (hiveVaporTension - 10));

    // Calcoliamo la temperatura percepita dell'ambiente attraverso l'indice Humidex
    double ambientVaporTension = 6.112 * Math.pow(10, ((7.5 * ambientTemp)/(237.7 + ambientTemp))) * (ambientHumidity / 100.0);
    double ambientApparentTemp = ambientTemp + (5.0/9.0 * (ambientVaporTension - 10));

    // Calcoliamo la differenza tra temperature percepite
    double apparentTempDiff = hiveApparentTemp - ambientApparentTemp;

    boolean result = false;
    HttpURLConnection connection = null;
    DataOutputStream outputStream = null;

    try {
      // Istanzia la connessione sull'ip
      URL url = new URL("http://127.0.0.1:5000/predict_ccd");
      // Formattiamo in formato JSON
      String[] inputData = {"{\"queen presence\" : " + queenPresenceFormatted + ", \"apparent hive temp\" : " + hiveApparentTemp + ", \"apparent temp diff\" : " + apparentTempDiff + "}"};
      for (String input : inputData) {
        // Scriviamo il nostro file JSON sulla connessione
        byte[] postDataToSend = input.getBytes(StandardCharsets.UTF_8);
        connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("charset", "utf-8");
        connection.setRequestProperty("Content-Length", Integer.toString(input.length()));
        outputStream = new DataOutputStream(connection.getOutputStream());
        outputStream.write(postDataToSend);
        outputStream.flush();

        if (connection.getResponseCode() != 200) {
          throw new RuntimeException("HTTP Error Code: " + connection.getResponseCode());
        }

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        // Leggiamo il nostro output e convertiamolo in JSon
        String output;
        while ((output = bufferedReader.readLine()) != null) {
          result = (Integer.parseInt(output) == 1);
        }
        connection.disconnect();
      }
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      if (connection != null)
        connection.disconnect();
    }

    return result;
  }

  // Questo metodo prende uno spettrogramma a caso dal dataset
  public String getRandomSpectrogram () {
    HttpURLConnection connection = null;
    DataOutputStream outputStream = null;
    String result = "";
    try {
      // Istanzia la connessione sull'ip
      URL url = new URL("http://127.0.0.1:5000/get_spectrogram");
      // Formattiamo in formato JSON
      String[] inputData = {"{\"nothing\" : \"nothing\"}"};
      for (String input : inputData) {
        // Scriviamo il nostro file JSON sulla connessione
        byte[] postDataToSend = input.getBytes(StandardCharsets.UTF_8);
        connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("charset", "utf-8");
        connection.setRequestProperty("Content-Length", Integer.toString(input.length()));
        outputStream = new DataOutputStream(connection.getOutputStream());
        outputStream.write(postDataToSend);
        outputStream.flush();

        if (connection.getResponseCode() != 200) {
          throw new RuntimeException("HTTP Error Code: " + connection.getResponseCode());
        }

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        // Leggiamo il nostro output
        String output;
        while ((output = bufferedReader.readLine()) != null) {
          result = output;
        }
        connection.disconnect();
      }
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      if (connection != null)
        connection.disconnect();
    }
    result = result.substring( 1, result.length() - 1 );
    return result;
  }

}