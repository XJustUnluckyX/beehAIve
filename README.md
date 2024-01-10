# beehAIve

# Overview

BeehAIve nasce con lo scopo di supportare gli apicoltori nel loro lavoro e nella salvaguardia delle api, identificando la presenza o l'assenza dell'ape regina in un'arnia tramite una CNN, per poi passare questo dato, insieme ad altre metriche, ad un algoritmo di Machine Learning per prevedere un possibile caso di CCD (Colony Collapse Disorder) dell'arnia.

Il CCD è una delle maggiori cause dello spopolamento delle arnie. Ad oggi questo rappresenta uno dei maggiori fattori di rischio per le Api, in quanto una volta abbandonata l'arnia, sarebbero destinate a morire. Potenziali indicatori di questo fenomeno sono peso dell'arnia, umidità interna ed esterna dell'arnia, temperatura interna ed esterna e presenza o assenza dell'ape regina.

# Dataset

Per addestrare la nostra CNN e il nostro modello di Machine Learning abbiamo utilizzato il seguente dataset, il quale contiene anche i suoni del ronzio delle api, oltre che l'indicazione (inserita manualmente da degli esperti) sull'assenza o presenza della regina:

https://www.kaggle.com/datasets/annajyang/beehive-sounds

# Notebooks

Per seguire l'intero processo di sviluppo della CNN e del modello di Machine Learning sono stati prodotti due notebook, trovabili ail'interno della repo ai seguenti path:

//TODO

//TODO

# Stuttura Repository

Per l'esame di FIA la cartella da visitare è una, "ai": è dove sono contenute tutti i file relativi ai modelli di Intelligenza Artificiale, suddivisi in due sottocartelle, una per la CNN e una per il modello di Machine Learning, suddivisi ulteriormente in un file per quasi ogni passo del CRISP-DM (Business Understanding esculso).

Mentre il file "src/main/resources/templates/driver-fia.html" è una pagina statica contentente due form per testare i due modelli.

# Come eseguire

Per poter eseguire la parte di CNN è necessario caricare prima una cartella contentente i file audio del dataset (reperibile al seguente link: //TODO), in modo da potersi ricavare gli spettogrammi e poter eseguire il modello sulle immagini, mentre nessuna procudeura particolare è richiesta per l'esecuzione del modello di Machine Learning
