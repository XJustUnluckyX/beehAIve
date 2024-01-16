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
  private boolean presentQueen;

  public DateAndSensorID getDateAndSensorID() {
    return dateAndSensorID;
  }

  public void setDateAndSensorID(DateAndSensorID dateAndSensorID) {
    this.dateAndSensorID = dateAndSensorID;
  }

  public double getWeight() {
    return weight;
  }

  public void setWeight(double weight) {
    this.weight = weight;
  }

  public double getNoise() {
    return noise;
  }

  public void setNoise(double noise) {
    this.noise = noise;
  }

  public double getTemperature() {
    return temperature;
  }

  public void setTemperature(double temperature) {
    this.temperature = temperature;
  }

  public double getAmbientTemperature() {
    return ambientTemperature;
  }

  public void setAmbientTemperature(double ambientTemperature) {
    this.ambientTemperature = ambientTemperature;
  }

  public double getHumidity() {
    return humidity;
  }

  public void setHumidity(double humidity) {
    this.humidity = humidity;
  }

  public double getAmbientHumidity() {
    return ambientHumidity;
  }

  public void setAmbientHumidity(double ambientHumidity) {
    this.ambientHumidity = ambientHumidity;
  }

  public boolean isPresentQueen() {
    return presentQueen;
  }

  public void setPresentQueen(boolean presentQueen) {
    this.presentQueen = presentQueen;
  }
}
