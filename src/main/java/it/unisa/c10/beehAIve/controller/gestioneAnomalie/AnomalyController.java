package it.unisa.c10.beehAIve.controller.gestioneAnomalie;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

// Gestisce la comunicazione con l'adapter per il modello e i vari controlli arbitari sulla misurazione, ovvero
// temperatura, umidità, peso, presenza regina
// e invia l'anomalia al database
@Controller
public class AnomalyController {

  // TODO: Change this after Flask Test


}