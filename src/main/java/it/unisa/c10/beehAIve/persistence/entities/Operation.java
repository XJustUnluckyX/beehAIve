package it.unisa.c10.beehAIve.persistence.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class Operation {
  @Id
  private int id;
  private String operationName;
  private String operationType;
  private String operationStatus;
  private LocalDateTime operationDate;
  private String notes;
  private int hiveId;
  private String beekeeperEmail;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getOperationName() {
    return operationName;
  }

  public void setOperationName(String name) {
    this.operationName = name;
  }

  public String getOperationType() {
    return operationType;
  }

  public void setOperationType(String type) {
    this.operationType = type;
  }

  public String getOperationStatus() {
    return operationStatus;
  }

  public void setOperationStatus(String status) {
    this.operationStatus = status;
  }

  public LocalDateTime getOperationDate() {
    return operationDate;
  }

  public void setOperationDate(LocalDateTime date) {
    this.operationDate = date;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
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
