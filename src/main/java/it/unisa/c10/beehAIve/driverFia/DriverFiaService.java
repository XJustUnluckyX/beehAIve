package it.unisa.c10.beehAIve.driverFia;

import it.unisa.c10.beehAIve.misc.FlaskAdapter;
import org.springframework.stereotype.Service;

@Service
public class DriverFiaService {

  private FlaskAdapter adapter = new FlaskAdapter();

  // Invochiamo l'adapter per far comunicare i moduli
  public Prediction predictWithCNN(double apparentHiveTemp, double apparentWeatherTemp) {
    return adapter.predictWithCNN(apparentHiveTemp, apparentWeatherTemp);
  }

  // Invochiamo l'adapter per far comunicare i moduli
  public Prediction predictWithoutCNN(double apparentHiveTemp, double apparentWeatherTemp, int queenPresence) {
    return adapter.predictWithoutCNN(apparentHiveTemp, apparentWeatherTemp, queenPresence);
  }

}
