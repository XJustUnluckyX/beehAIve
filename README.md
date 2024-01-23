# beehAIve

## Team Composition

Il team di beehAIve è composto da:
-  [Nicolò Delogu](https://github.com/XJustUnluckyX) (PM)
-  [Dario Mazza](https://github.com/xDaryamo) (PM)
-  [Francesco Festa](https://github.com/MonTheDog) (TM)
-  [Nicolò Gallotta](https://github.com/gnicolo00) (TM)
-  [Sara Valente](https://github.com/svalent3) (TM)
-  [Andrea De Pasquale](https://github.com/AndreaDePasquale) (TM)
-  [Lorenzo Milione](https://github.com/LorenzoMilione) (TM)
-  [Carmine Boninfante](https://github.com/dragon0302) (TM)

## Overview

La piattaforma beehAIve nasce con lo scopo di **supportare gli apicoltori nel loro lavoro** e nella 
salvaguardia delle api, permettendo di registrare e monitorare le loro arnie, visualizzandone i 
parametri di salute misurati in tempo reale da appositi sensori e riconoscere le anomalie attraverso
l'Intelligenza Artificiale. In particolare, viene identificata la presenza o l'assenza dell'ape 
regina in un'arnia tramite una CNN, per poi passare questo dato, insieme alle altre metriche 
menzionate, a un algoritmo di Machine Learning per prevedere un possibile caso di **_CCD 
(Colony Collapse Disorder)_** dell'arnia.

Il CCD è una delle maggiori cause dello spopolamento delle arnie. Ad oggi questo rappresenta uno dei
maggiori fattori di rischio per le api, in quanto una volta abbandonata l'arnia, sarebbero destinate
a morire. Potenziali indicatori di questo fenomeno sono il peso dell'arnia, l'umidità interna ed 
esterna dell'arnia, la temperatura interna ed esterna e la presenza o assenza dell'ape regina.

## Struttura dei File

Per quanto riguarda la piattaforma vera e propria, la cartella da visitare è "**_src_**". In 
questa cartella sono contenuti tutti i file relativi al back-end e al front-end della piattaforma, 
suddivisi in altre sotto-cartelle. Questa suddivisione è stata creata allo scopo di seguire la 
struttura di Java Spring, utilizzato per lo sviluppo del progetto. Le cartelle principali sono le 
seguenti: 
- "_**main/java/it/unisa/c10/beehaive/controller**_": in questa cartella sono presenti le classi 
  responsabili di gestire le richieste degli utenti e restituire la risposta appropriata;
- "**_main/java/it/unisa/c10/beehaive/service_**": in questa cartella sono presenti le classi che 
  contengono la logica di business;
- "**_main/java/it/unisa/c10/beehaive/persistence_**": in questa cartella sono presenti le classi 
  che implementano le repository basate su Java Persistence API per accedere al database;
- "**_main/java/it/unisa/c10/beehaive/misc_**": in questa cartella sono presenti tutti i file di 
  configurazione, tra cui quelli
  riguardanti Java Spring Security e l'API di PayPal;
- "**_main/resources_**": in questa cartella sono presenti tutti i file relativi al front-end e gli 
  script JavaScript;
- "**_test_**": in questa cartella sono presenti i file relativi al testing di unità e di sistema;
- "**_db_**": in questa cartella è presente lo schema SQL e gli inserimenti di default.

Per quando riguarda l'Intelligenza Artificiale, la cartella da visitare è "**_src/ai_**". In questa 
cartella sono contenute tutti i file relativi ai modelli, suddivisi in due sottocartelle, una per la
CNN e una per il modello di Machine Learning, suddivisi ulteriormente in un file per ogni passo del 
CRISP-DM, fatta eccezione per il Business Understanding. All'interno della cartella, inoltre, è 
possibile trovare altre risorse utili, come gli spettrogrammi già computati, i *log* di valutazione
delle configurazioni della CNN e il deploy dei modelli attraverso l'utilizzo di Flask.

## Documentazione

In questa repository è possibile trovare tutta la documentazione riguardante lo sviluppo del 
progetto sotto la cartella "**_src/project_docs_**". In particolare, al suo interno vi sono:
- **_Documenti di Management_**;
- **_RAD_**;
- **_SDD_**;
- **_ODD_**;
- **_Documenti di Testing_**;
- **_Manuale di installazione e utente_**;
- **_Matrice di tracciabilità_**.

Per quanto riguarda l'Intelligenza Artificiale, il file interessato si trova al seguente path:
**_src/ai/beehAIve_documentazione.pdf_**. Il documento ricalca tutti gli step effettuati in forma 
più dettagliata e discorsiva.

Infine, per consultare la documentazione Javadoc, è possibile visitare il seguente link:
https://xjustunluckyx.github.io/beehAIve/.

## Dataset AI

Per addestrare la nostra CNN e il nostro modello di Machine Learning abbiamo utilizzato il seguente
dataset, il quale contiene anche i suoni del ronzio delle api, oltre che l'indicazione (inserita
manualmente da degli esperti) sull'assenza o presenza della regina:

https://www.kaggle.com/datasets/annajyang/beehive-sounds

![logo-beehAIve](https://github.com/XJustUnluckyX/beehAIve/assets/126207669/1e0ee410-9da0-42c1-b1d2-4cc793595e9c)
