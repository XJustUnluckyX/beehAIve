package it.unisa.c10.beehAIve.persistence.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class Anomaly {
  @Id
  private int id;
  private String anomalyName;
  private boolean resolved;
  private LocalDateTime detectionDate;
  private int sensorId;
  private int hiveId;
  private String beekeeperEmail;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getAnomalyName() {
    return anomalyName;
  }

  public void setAnomalyName(String name) {
    this.anomalyName = name;
  }

  public boolean isResolved() {
    return resolved;
  }

  public void setResolved(boolean resolved) {
    resolved = resolved;
  }

  public LocalDateTime getDetectionDate() {
    return detectionDate;
  }

  public void setDetectionDate(LocalDateTime detectionDate) {
    this.detectionDate = detectionDate;
  }

  public int getSensorId() {
    return sensorId;
  }

  public void setSensorId(int sensorID) {
    this.sensorId = sensorID;
  }

  public int getHiveId() {
    return hiveId;
  }

  public void setHiveId(int hiveID) {
    this.hiveId = hiveID;
  }

  public String getBeekeeperEmail() {
    return beekeeperEmail;
  }

  public void setBeekeeperEmail(String beekeeperEmail) {
    this.beekeeperEmail = beekeeperEmail;
  }
}
