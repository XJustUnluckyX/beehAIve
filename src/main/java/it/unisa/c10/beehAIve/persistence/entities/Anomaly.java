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
}
