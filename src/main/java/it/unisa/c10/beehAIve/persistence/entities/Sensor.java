package it.unisa.c10.beehAIve.persistence.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Sensor {
  @Id
  private int id;
  private int hiveId;

  private String beekeeperEmail;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getHiveId() {
    return hiveId;
  }

  public void setHiveId(int hiveID) {
    this.hiveId = hiveID;
  }

  public String getBeekeeperEmail() {
    return beekeeperEmail;
  }

  public void setBeekeeperEmail(String beekeeperEmail) {
    this.beekeeperEmail = beekeeperEmail;
  }
}
