package it.unisa.c10.beehAIve.misc;

import com.google.gson.Gson;
import it.unisa.c10.beehAIve.driverFia.Prediction;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

//Converte un oggetto misurazione in JSON, comunica col microservice in Flask e riconverte la risposta in un
//oggetto anomalia CCD se necessario.
public class AiAdapter {

  public Prediction predictWithCNN (double apparentHiveTemp, double apparentWeatherTemp) {
    Prediction prediction = null;
    HttpURLConnection connection = null;
    DataOutputStream outputStream = null;
    try {
      URL url = new URL("http://127.0.0.1:5000/ccd");
      double apparentTempDiff = apparentHiveTemp - apparentWeatherTemp;
      // 3 in queen presence è un valore mock solo per far apparire il valore nel dictionary di python
      // si è deciso un numero diverso da 0 e 1 così da evitare discrepanze
      String[] inputData = {"{\"queen presence\" : 3, \"apparent hive temp\" : " + apparentHiveTemp + ", \"apparent temp diff\" : " + apparentTempDiff + "}"};
      for (String input : inputData) {
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

        String output;
        while ((output = bufferedReader.readLine()) != null) {
          Gson gson = new Gson();
          prediction = gson.fromJson(output, Prediction.class);
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

    return prediction;
  }


  public Prediction predictWithoutCNN(double apparentHiveTemp, double apparentWeatherTemp, int queenPresence) {
    Prediction prediction = null;
    HttpURLConnection connection = null;
    DataOutputStream outputStream = null;
    try {
      URL url = new URL("http://127.0.0.1:5000/ccd_no_cnn");
      double apparentTempDiff = apparentHiveTemp - apparentWeatherTemp;
      String[] inputData = {"{\"queen presence\" : " + queenPresence + ", \"apparent hive temp\" : " + apparentHiveTemp + ", \"apparent temp diff\" : " + apparentTempDiff + "}"};
      for (String input : inputData) {
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

        String output;
        while ((output = bufferedReader.readLine()) != null) {
          Gson gson = new Gson();
          prediction = gson.fromJson(output, Prediction.class);
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

    return prediction;
  }

}