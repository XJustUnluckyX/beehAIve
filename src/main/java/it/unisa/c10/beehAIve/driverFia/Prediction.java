package it.unisa.c10.beehAIve.driverFia;

public class Prediction {

  private int ccd_result;
  private int predicted_queen;
  private int actual_queen;
  private double ccd_true_probability;

  public Prediction(int ccd_result, double ccd_true_probability) {
    this.ccd_result = ccd_result;
    this.ccd_true_probability = ccd_true_probability;
  }

  public Prediction(int ccd_result, int predicted_queen, int actual_queen, double ccd_true_probability) {
    this.ccd_result = ccd_result;
    this.predicted_queen = predicted_queen;
    this.actual_queen = actual_queen;
    this.ccd_true_probability = ccd_true_probability;
  }

  public int getCcd_result() {
    return ccd_result;
  }

  public void setCcd_result(int ccd_result) {
    this.ccd_result = ccd_result;
  }

  public int getPredicted_queen() {
    return predicted_queen;
  }

  public void setPredicted_queen(int predicted_queen) {
    this.predicted_queen = predicted_queen;
  }

  public int getActual_queen() {
    return actual_queen;
  }

  public void setActual_queen(int actual_queen) {
    this.actual_queen = actual_queen;
  }

  public double getCcd_true_probability() {
    return ccd_true_probability;
  }

  public void setCcd_true_probability(double ccd_true_probability) {
    this.ccd_true_probability = ccd_true_probability;
  }
}
