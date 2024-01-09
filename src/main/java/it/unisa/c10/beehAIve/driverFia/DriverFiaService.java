package it.unisa.c10.beehAIve.driverFia;

import it.unisa.c10.beehAIve.misc.AiAdapter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DriverFiaService {

  private AiAdapter adapter = new AiAdapter();

  public Prediction predictWithCNN(double apparentHiveTemp, double apparentWeatherTemp) {
    return adapter.predictWithCNN(apparentHiveTemp, apparentWeatherTemp);
  }

  public Prediction predictWithoutCNN(double apparentHiveTemp, double apparentWeatherTemp, int queenPresence) {
    return adapter.predictWithoutCNN(apparentHiveTemp, apparentWeatherTemp, queenPresence);
  }

}
