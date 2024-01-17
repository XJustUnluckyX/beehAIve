package it.unisa.c10.beehAIve.persistence.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDate;

@Entity
public class Hive {
  @Id
  private int id;
  private String nickname;
  private String hiveType;
  private LocalDate creationDate;
  private String beekeeperEmail;
  private String beeSpecies;
  private int hiveHealth;
  private boolean uncompletedOperations;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getNickname() {
    return nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  public String getHiveType() {
    return hiveType;
  }

  public void setHiveType(String type) {
    this.hiveType = type;
  }

  public LocalDate getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(LocalDate creationDate) {
    this.creationDate = creationDate;
  }

  public String getBeekeeperEmail() {
    return beekeeperEmail;
  }

  public void setBeekeeperEmail(String beekeeperEmail) {
    this.beekeeperEmail = beekeeperEmail;
  }

  public String getBeeSpecies() {
    return beeSpecies;
  }

  public void setBeeSpecies(String beeSpecies) {
    this.beeSpecies = beeSpecies;
  }

  public int getHiveHealth() {
    return hiveHealth;
  }

  public void setHiveHealth(int hive_health) {
    this.hiveHealth = hive_health;
  }

  public boolean isUncompletedOperations() {
    return uncompletedOperations;
  }

  public void setUncompletedOperations(boolean uncompleted_operations) {
    this.uncompletedOperations = uncompleted_operations;
  }
}
