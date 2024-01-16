package it.unisa.c10.beehAIve.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.security.MessageDigest;
import java.time.LocalDate;

@Entity
public class Beekeeper {
  @Id
  private String email;
  private String passwordhash;
  private String firstName;
  private String lastName;
  private String companyName;
  @Column(unique=true)
  private String companyPiva;
  private boolean subscribed;
  private double paymentDue;
  private LocalDate subscrExpirationDate;

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPasswordhash() {
    return passwordhash;
  }

  public void setPasswordhash(String password) {
      try {
        // Creazione di un MessageDigest per utilizzare SHA-256
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        // Conversione della stringa in un array di byte
        final byte[] hash = digest.digest(password.getBytes("UTF-8"));
        // Creazione della stringa che conterrà il risultato finale della conversione
        final StringBuilder hexString = new StringBuilder();
        // Conversione di ogni byte dell'array di byte in stringa esadecimale
        for (int i = 0; i < hash.length; i++) {
          // Conversione del byte in hex
          final String hex = Integer.toHexString(0xff & hash[i]);
          // Se la rappresentazione hex ha lunghezza 1, si aggiunge uno 0 (ogni byte è rappresentato da 2 cifre hex)
          if(hex.length() == 1) {
            hexString.append('0');
          }
          hexString.append(hex);
        }
        this.passwordhash = hexString.toString();
      } catch (Exception ex) {
        throw new RuntimeException(ex);
      }
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getCompanyName() {
    return companyName;
  }

  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }

  public String getCompanyPiva() {
    return companyPiva;
  }

  public void setCompanyPiva(String companyPiva) {
    this.companyPiva = companyPiva;
  }

  public boolean isSubscribed() {
    return subscribed;
  }

  public void setSubscribed(boolean subscribed) {
    this.subscribed = subscribed;
  }

  public double getPaymentDue() {
    return paymentDue;
  }

  public void setPaymentDue(double paymentDue) {
    this.paymentDue = paymentDue;
  }

  public LocalDate getSubscrExpirationDate() {
    return subscrExpirationDate;
  }

  public void setSubscrExpirationDate(LocalDate subscrExpirationDate) {
    this.subscrExpirationDate = subscrExpirationDate;
  }
}
