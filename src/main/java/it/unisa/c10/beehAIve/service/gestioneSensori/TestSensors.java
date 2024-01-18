package it.unisa.c10.beehAIve.service.gestioneSensori;

import it.unisa.c10.beehAIve.misc.FlaskAdapter;
import it.unisa.c10.beehAIve.persistence.dao.MeasurementDAO;
import it.unisa.c10.beehAIve.persistence.entities.Measurement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.File;
import java.util.List;
import java.util.Random;

@Controller
public class TestSensors {

  private MeasurementDAO measurementDAO;
  @Autowired
  public TestSensors(MeasurementDAO measurementDAO) {
    this.measurementDAO = measurementDAO;
  }

  @GetMapping("/testSensor")
  public String test() {
    List<Measurement> measurements = measurementDAO.findByHiveId(1);

    for (Measurement m : measurements)
      System.out.println(m.getMeasurementDate());

//    Measurement lastMeasurement = measurements.get(measurements.size()-1);
    return "index";
  }



}
