package it.unisa.c10.beehAIve.persistence.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDate;

@Entity
public class Production {
  @Id
  private int id;
  private String product;
  private double weight;
  private String notes;
  private LocalDate registrationDate;
  private int hiveId;
  private String beekeeperEmail;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getProduct() {
    return product;
  }

  public void setProduct(String product) {
    this.product = product;
  }

  public double getWeight() {
    return weight;
  }

  public void setWeight(double weight) {
    this.weight = weight;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }

  public LocalDate getRegistrationDate() {
    return registrationDate;
  }

  public void setRegistrationDate(LocalDate registrationDate) {
    this.registrationDate = registrationDate;
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
