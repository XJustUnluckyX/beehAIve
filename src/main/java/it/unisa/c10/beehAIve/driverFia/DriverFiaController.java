package it.unisa.c10.beehAIve.driverFia;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Controller
public class DriverFiaController {

  private DriverFiaService driverFiaService;
  @Autowired
  public DriverFiaController( DriverFiaService driverFiaService) {
    this.driverFiaService = driverFiaService;
  }

  @GetMapping("/driver_fia")
  public String load_driver () {
    return "driver-fia";
  }

//  @PostMapping("/predict_with_cnn")
//  @ResponseBody
//  public String cddPredictionWithCNN(@RequestBody String model) {
//    System.out.println("Test");
//    System.out.println(model);
////    driverFiaService.predictWithCNN(10,20);
//    return "test";
//  }

  @GetMapping("/predict_with_cnn")
  @ResponseBody
  public String cddPredictionWithCNN(@RequestParam String hiveTemp, @RequestParam String ambTemp) {
    Prediction p = driverFiaService.predictWithCNN(Double.parseDouble(hiveTemp),Double.parseDouble(ambTemp));
    Gson gson = new Gson();
    String json = gson.toJson(p);
    return json;
  }

  @GetMapping("/predict_without_cnn")
  @ResponseBody
  public String cddPredictionWithoutCNN(@RequestParam String hiveTemp, @RequestParam String ambTemp, @RequestParam String queenPresence) {
    Prediction p = driverFiaService.predictWithoutCNN(Double.parseDouble(hiveTemp), Double.parseDouble(ambTemp), Integer.parseInt(queenPresence));
    Gson gson = new Gson();
    String json = gson.toJson(p);
    return json;
  }


}
