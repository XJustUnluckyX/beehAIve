package it.unisa.c10.beehAIve.persistence.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Sensor {
  @Id
  private int id; //temporary
  private int hiveID;
}
