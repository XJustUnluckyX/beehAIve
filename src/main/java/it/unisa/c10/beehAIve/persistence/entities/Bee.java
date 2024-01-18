package it.unisa.c10.beehAIve.persistence.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Bee {
  @Id
  private String scientificName;
  private String commonName;
  private String beeDescription;
  private String photo;

  public String getScientificName() {
    return scientificName;
  }

  public void setScientificName(String scientificName) {
    this.scientificName = scientificName;
  }

  public String getCommonName() {
    return commonName;
  }

  public void setCommonName(String commonName) {
    this.commonName = commonName;
  }

  public String getBeeDescription() {
    return beeDescription;
  }

  public void setBeeDescription(String description) {
    this.beeDescription = description;
  }

  public String getPhoto() {
    return photo;
  }

  public void setPhoto(String photo) {
    this.photo = photo;
  }
}
