package it.unisa.c10.beehAIve.persistence.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class Operation {
  @Id
  private int id; //temporary
  private String name;
  private String type; //we could use enum
  private String status; //we could use enum
  private String notes;
  private String beekeeperEmail;
  private int hiveID;
  private LocalDateTime date;
}
