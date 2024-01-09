package it.unisa.c10.beehAIve.driverFia;

import org.springframework.beans.factory.annotation.Autowired;
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

  @GetMapping("/predict_with_cnn")
  public String cddPredictionWithCNN() {
    driverFiaService.predictWithCNN(10,20);
    return "index";
  }

  @GetMapping("/predict_without_cnn")
  public String cddPredictionWithoutCNN() {
    driverFiaService.predictWithoutCNN(10,20,0);
    return "index";
  }


}
