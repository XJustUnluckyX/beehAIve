package it.unisa.c10.beehAIve.persistence.entities;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.time.LocalDateTime;

@Embeddable
public class DateAndSensorID implements Serializable {
  private LocalDateTime date;
  private int sensorID;
}
