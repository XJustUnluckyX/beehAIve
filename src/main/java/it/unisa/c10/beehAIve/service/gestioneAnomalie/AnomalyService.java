package it.unisa.c10.beehAIve.service.gestioneAnomalie;

import org.springframework.stereotype.Service;

/*
La seguente classe, dopo aver ricevuto l'input dalla classe SimulateSensorService, deve supportare le seguenti operazioni:
1. Controllare se esiste un'anomalia sul peso dell'arnia.
2. Controllare se esiste un'anomalia sulla temperatura dell'arnia.
3. Controllare se esiste un'anomalia sull'umidità dell'arnia.
4. Controllare se la regina è presente, consultando la rete neurale (quest'ultima sarà un microservizio Flask a parte,
quindi il metodo corrispondente riceverà in input il file audio). TODO spostare qua il codice
5. Controllare se l'arnia è a rischio CCD, interfacciandosi con l'Adapter.
 */

@Service
public class AnomalyService {

}
