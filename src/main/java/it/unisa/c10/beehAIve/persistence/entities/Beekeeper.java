package it.unisa.c10.beehAIve.persistence.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDateTime;
//TODO colonna payment_due per calcolare quanto deve pagare l'apicoltore
@Entity
public class Beekeeper {
  @Id
  private String email;
  private String password;
  private String name;
  private String surname;
  private String companyName;
  private String companyPIVA;
  private boolean subscribed;
  private LocalDateTime subscrExpirationDate;
}
