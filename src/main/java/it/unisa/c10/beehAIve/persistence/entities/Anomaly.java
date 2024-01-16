package it.unisa.c10.beehAIve.persistence.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDate;

@Entity
public class Anomaly {
  @Id
  private int id;
  private String name;
  private boolean isResolved;
  private LocalDate detectionDate;
  private int sensorId;
  private int hiveId;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isResolved() {
    return isResolved;
  }

  public void setResolved(boolean resolved) {
    isResolved = resolved;
  }

  public LocalDate getDate() {
    return detectionDate;
  }

  public void setDate(LocalDate detectionDate) {
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
}
