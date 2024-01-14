package it.unisa.c10.beehAIve.service.gestioneSensori;

import it.unisa.c10.beehAIve.misc.FlaskAdapter;

import java.io.File;
import java.util.Random;

public class TestSensors {

  public static void main (String[] args) {

    FlaskAdapter adapter = new FlaskAdapter();
    String newSpectrogram = "2022-06-06--21-18-35_2_spect.png";

    boolean newPresentQueen = adapter.predictQueenPresence(newSpectrogram);

    System.out.println(newPresentQueen);

  }

}
