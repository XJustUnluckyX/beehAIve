package it.unisa.c10.beehAIve.controller.gestioneAnomalie;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

// Gestisce la comunicazione con l'adapter per il modello e i vari controlli arbitari sulla misurazione, ovvero
// temperatura, umidità, peso, presenza regina
// e invia l'anomalia al database
@Controller
public class AnomalyController {

  // TODO: Change this after Flask Test
  @GetMapping("/cnn_test")
  public String cnn_test() {
    HttpURLConnection connection = null;
    DataOutputStream outputStream = null;
    try {
      URL url = new URL("http://127.0.0.1:5000/predict_cnn_fia"); // Maybe a / is missing
      String[] inputData = {"{\"file\" : \"2022-06-25--23-56-32_1_spect.png\"}"};
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
        System.out.println("Output: ");
        while ((output = bufferedReader.readLine()) != null) {
          System.out.println(output);
        }
        connection.disconnect();
      }
    } catch (MalformedURLException e) {
      throw new RuntimeException(e); //TODO check exceptions
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      if (connection != null)
        connection.disconnect();
    }

    return "index";
  }


}