package it.unisa.c10.beehAIve.persistence.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class Hive {
  @Id
  private int id; //temporary
  private String nickname;
  private String type; //we could use enum
  private LocalDateTime creationDate;
  private String beekeeperEmail;
  private String speciesOfBee;
}
