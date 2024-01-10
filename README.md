# beehAIve

# Overview

BeehAIve nasce con lo scopo di supportare gli apicoltori nel loro lavoro e nella salvaguardia delle api, identificando la presenza o l'assenza dell'ape regina in un'arnia tramite una CNN, per poi passare questo dato, insieme ad altre metriche, ad un algoritmo di Machine Learning per prevedere un possibile caso di CCD (Colony Collapse Disorder) dell'arnia.

Il CCD è una delle maggiori cause dello spopolamento delle arnie. Ad oggi questo rappresenta uno dei maggiori fattori di rischio per le Api, in quanto una volta abbandonata l'arnia, sarebbero destinate a morire. Potenziali indicatori di questo fenomeno sono peso dell'arnia, umidità interna ed esterna dell'arnia, temperatura interna ed esterna e presenza o assenza dell'ape regina.

# Per l'esame di Fondamenti di Intelligenza Artificiale

## Dataset

Per addestrare la nostra CNN e il nostro modello di Machine Learning abbiamo utilizzato il seguente dataset, il quale contiene anche i suoni del ronzio delle api, oltre che l'indicazione (inserita manualmente da degli esperti) sull'assenza o presenza della regina:

https://www.kaggle.com/datasets/annajyang/beehive-sounds

## Notebooks e Documentazione

Per seguire l'intero processo di sviluppo della CNN e del modello di Machine Learning sono stati prodotti due notebook, che riassumono quanto da noi fatto. Essi sono situati ail'interno della repository ai seguenti path: <br/>
CNN: src/ai/development/CNN/CNN_development_notebook.ipynb <br/>
ML: src/ai/development/ML/ML_development_notebook.ipynb

Nonostante ciò, è **fortemente sconsigliato** eseguirli, a causa di alcuni passaggi nella nostra pipeline che implicano un ampio utilizzo di randomicità, oltre che delle computazioni particolarmente onerose (In particolare la conversione da file audio a spettrogrammi). Si consiglia invece di leggere la documentazione da noi prodotta, che ricalca gli step in forma più dettagliata e discorsiva.
È possibile trovarla all'interno della repository al seguente path: <br/>
//TODO

## Struttura dei File 

Per l'esame di FIA la cartella da visitare è una, "ai": è dove sono contenute tutti i file relativi ai modelli di Intelligenza Artificiale, suddivisi in due sottocartelle, una per la CNN e una per il modello di Machine Learning, suddivisi ulteriormente in un file per quasi ogni passo del CRISP-DM (Business Understanding esculso).

Mentre il file "src/main/resources/templates/driver-fia.html" è una pagina statica contentente due form per testare i due modelli.

Tutto il codice e il materiale da noi prodotto per l'esame di FIA relativo ai modelli è situato sotto src/ai. In particolare qui è possibile usare i dataset da noi utilizzati, i vari file di sviluppo che ricalcano le fasi del CRISP-DM e altre risorse utili (Come gli spettrogrammi già computati o i log di valutazione delle configurazioni della CNN). É inoltre possibile trovare il deploy dei modelli attraverso Flask.

Per mostrare quanto da noi prodotto, è stato inoltre creato un piccolo driver, il quale codice è costruito sull'architettura già esistente di beehAIve. I file di nostro interesse sono i seguenti: <br/>
1. src/main/java/it/unisa/c10/beehAIve/driverFia (Per trovare tutto il backend relativo al driver) <br/>
2. src/main/java/it/unisa/c10/beehAIve/misc/FlaskAdapter.java (Per trovare la nostra implementazione dell'adapter per comunicare con Flask attraverso HTTP) <br/>
3. src/main/resources/templates/driver-fia.html (Per trovare il template front end del nostro driver)

## Come eseguire

Per poter eseguire la parte di CNN è necessario caricare prima una cartella contentente i file audio del dataset (reperibile al seguente link: //TODO), in modo da potersi ricavare gli spettogrammi e poter eseguire il modello sulle immagini, mentre nessuna procudeura particolare è richiesta per l'esecuzione del modello di Machine Learning

Per poter eseguire il nostro driver basterà eseguire i seguenti passaggi:
1. Scaricare il modello della nostra CNN situato nel [seguente Drive](https://drive.google.com/drive/u/0/folders/1-9QYqQ02ekceGdyQ61xjTOjKwhGBPvom), all'interno della cartella CNN Model e denominato CNN.keras. All'interno dello stesso drive è possibile trovare anche altri file presenti all'interno del progetto, come i Dataset dell'ultima esecuzione, il modello di Machine Learning o gli spettrogrammi. Questi sono già situati all'interno della repository


