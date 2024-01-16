package it.unisa.c10.beehAIve.persistence.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class Anomaly {
  @Id
  private int id; //temporary
  private String type;
  private boolean resolved;
  private int sensorID;
  private int hiveID;
  private LocalDateTime date;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public boolean isResolved() {
    return resolved;
  }

  public void setResolved(boolean resolved) {
    this.resolved = resolved;
  }

  public int getSensorID() {
    return sensorID;
  }

  public void setSensorID(int sensorID) {
    this.sensorID = sensorID;
  }

  public int getHiveID() {
    return hiveID;
  }

  public void setHiveID(int hiveID) {
    this.hiveID = hiveID;
  }

  public LocalDateTime getDate() {
    return date;
  }

  public void setDate(LocalDateTime date) {
    this.date = date;
  }
}
