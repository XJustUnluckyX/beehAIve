package it.unisa.c10.beehAIve.persistence.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class Production {
  @Id
  private int id; //temporary
  private String type; //we could use enum
  private double weight;
  private String notes;
  private String beekeeperEmail;
  private LocalDateTime registrationDate;
  private int hiveID;
}
