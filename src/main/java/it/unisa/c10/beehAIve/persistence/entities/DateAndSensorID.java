package it.unisa.c10.beehAIve.persistence.entities;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.time.LocalDateTime;

@Embeddable
public class DateAndSensorID implements Serializable {
  private LocalDateTime date;
  private int sensorID;

  public DateAndSensorID(LocalDateTime date, int sensorID) {
    this.date = date;
    this.sensorID = sensorID;
  }

  public LocalDateTime getDate() {
    return date;
  }

  public void setDate(LocalDateTime date) {
    this.date = date;
  }

  public int getSensorID() {
    return sensorID;
  }

  public void setSensorID(int sensorID) {
    this.sensorID = sensorID;
  }
}
