package it.unisa.c10.beehAIve.persistence.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class Measurement {
  @Id
  private int id;
  private int sensorId;
  private int hiveId;
  private LocalDateTime measurementDate;
  private double weight;
  private String spectrogram;
  private double temperature;
  private double ambientTemperature;
  private double humidity;
  private double ambientHumidity;
  private boolean queenPresent;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getSensorId() {
    return sensorId;
  }

  public void setSensorId(int sensorId) {
    this.sensorId = sensorId;
  }

  public int getHiveId() {
    return hiveId;
  }

  public void setHiveId(int hiveId) {
    this.hiveId = hiveId;
  }

  public LocalDateTime getMeasurementDate() {
    return measurementDate;
  }

  public void setMeasurementDate(LocalDateTime date) {
    this.measurementDate = date;
  }

  public boolean isQueenPresent() {
    return queenPresent;
  }

  public void setQueenPresent(boolean queenPresent) {
    this.queenPresent = queenPresent;
  }

  public double getWeight() {
    return weight;
  }

  public void setWeight(double weight) {
    this.weight = weight;
  }

  public String getSpectrogram() {
    return spectrogram;
  }

  public void setSpectrogram(String spectrogram) {
    this.spectrogram = spectrogram;
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


}
