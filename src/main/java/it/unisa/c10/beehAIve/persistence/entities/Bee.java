package it.unisa.c10.beehAIve.persistence.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Bee {
  @Id
  private String scientificName;
  private String common_name;
  private String description;
  //campo per l'immagine
}
