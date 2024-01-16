package it.unisa.c10.beehAIve.persistence.entities;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.time.LocalDateTime;

@Embeddable
public class DateAndSensorID implements Serializable {
  private LocalDateTime date;
  private int sensorId;

  public LocalDateTime getDate() {
    return date;
  }

  public void setDate(LocalDateTime date) {
    this.date = date;
  }

  public int getSensorId() {
    return sensorId;
  }

  public void setSensorId(int sensorID) {
    this.sensorId = sensorID;
  }
}
