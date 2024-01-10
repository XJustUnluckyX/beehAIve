# beehAIve

# Overview

BeehAIve nasce con lo scopo di supportare gli apicoltori nel salvaguardare le api, identificando la presenza o l'assenza dell'ape regina in un'arnia tramite una CNN, per poi passare il dato, insieme ad altre metriche, ad un algoritmo di Machine Learning per prevedere un possibile caso di CCD (Colony Collapse Disorder) dell'arnia.

# Colony Collapse Disorder

È una delle maggiori cause di perdita delle arnie, è possibile prevederlo secondo diverse metriche, tra le più importanti: peso, umidità interna ed esterna dell'arnia, temperatura interna ed esterna e presenza o assenza dell'ape regina.

# Dataset

Le metriche per addestrare la nostra CNN e il nostro modello di Machine Learning le abbiamo prese da questo dataset:

https://www.kaggle.com/datasets/annajyang/beehive-sounds

il quale risulta essere abbastanza completo, contentendo diversi valori, anche se molti di questi non ci serviranno per i nostri modelli.

# Notebooks

Per parlare dell'intero processo di sviluppo della CNN e del modello di Machine Learning abbiamo usato due notebook, uno per ogni modello, trovabili ai seguenti link:

//TODO

//TODO

# Stuttura Repository

Per l'esame di FIA la cartella da visitare è una, "ai": è dove sono contenute tutti i file relativi ai modelli di Intelligenza Artificiale, suddivisi in due sottocartelle, una per la CNN e una per il modello di Machine Learning, suddivisi ulteriormente in un file per quasi ogni passo del CRISP-DM (Business Understanding esculso).

Mentre il file "src/main/resources/templates/driver-fia.html" è una pagina statica contentente due form per testare i due modelli.

# Come eseguire

Per poter eseguire la parte di CNN è necessario caricare prima una cartella contentente i file audio del dataset (reperibile al seguente link: //TODO), in modo da potersi ricavare gli spettogrammi e poter eseguire il modello sulle immagini, mentre nessuna procudeura particolare è richiesta per l'esecuzione del modello di Machine Learning
