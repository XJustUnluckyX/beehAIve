package it.unisa.c10.beehAIve.persistence.entities;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;

@Entity
public class Measurement {
  @EmbeddedId
  private DateAndSensorID dateAndSensorID;
  private double weight;
  private double noise;
  private double temperature;
  private double ambientTemperature;
  private double humidity;
  private double ambientHumidity;
  private double presentQueen;
}
