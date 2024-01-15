import pandas as pd
import numpy as np
import random  # Per lanciare la probabilità che l'arnia sia soggetta a CCD o meno
import matplotlib.pyplot as plt  # Per visualizzare i grafici

# importiamo il nostro dataset
dataset = pd.read_csv("..\\..\\dataset\\hive_dataset.csv")

print(dataset)

# Guardiamo le colonne del nostro dataset
print(dataset.columns)

# Andiamo a controllare quanti valori nulli abbiamo
null_mask = dataset.isna()  # Creazione di una maschera booleana che è True se l'elemento è nullo
null_count = null_mask.sum()  # Calcolo della somma di tutti i valori True nella maschera

print(null_count)

# Rimuoviamo le righe di weather temp che sono valori nulli
dataset.dropna(subset=["weather temp"], inplace=True)
dataset = dataset.reset_index(drop=True)

# T = Temperatura
# U = Umidità
# e = Tensione di vapore dell'aria
# H = Temperatura percepita

# e = (6.112*10**(7.5*T/(237.7+T))*U/100)
# H = T + (5/9*(e-10))

# Calcolo della tensione di vapore interna ed esterna all'arnia e e salvataggio temporaneo nel dataset
dataset["hive vapor tension"] = 6.112*10**(7.5* dataset["hive temp"]/(237.7+ dataset["hive temp"]))*dataset["hive humidity"]/100
dataset["weather vapor tension"] = 6.112*10**(7.5* dataset["weather temp"]/(237.7+ dataset["weather temp"]))*dataset["weather humidity"]/100

# Calcolo e arrotondamento della temperatura percepita interna ed esterna
dataset["apparent hive temp"] = dataset["hive temp"] + (5/9*(dataset["hive vapor tension"] - 10))
dataset["apparent hive temp"] = round(dataset["apparent hive temp"], 2)

dataset["apparent weather temp"] = dataset["weather temp"] + (5/9*(dataset["weather vapor tension"] - 10))
dataset["apparent weather temp"] = round(dataset["apparent weather temp"], 2)

# Calcolo della differenza tra temperatura percepita interna ed esterna
dataset["apparent temp diff"] = dataset["apparent hive temp"] - dataset["apparent weather temp"]

print(dataset)

# Eliminiamo le colonne non inerenti
dataset = dataset.drop(["device", "hive number", "date", "hive pressure", "weather pressure", "wind speed",
"gust speed", "weatherID", "cloud coverage", "rain", "lat", "long","file name", "queen acceptance", "frames",
"target","time", "queen status"], axis=1)

# Eliminiamo le colonne utilizzate per computare le nostre feature
dataset = dataset.drop(["hive temp", "hive humidity", "weather temp", "weather humidity", "hive vapor tension",
"weather vapor tension", "apparent weather temp"], axis=1)

print(dataset)

# Definiamo la nostra Euristica di Labeling
def AristeoHeuristic(dataset):
    ccd_list = []

    # Iterazione sull'intero dataset
    for index, row in dataset.iterrows():

        # Calcolo inerente alla temperatura interna
        internal_temp_coeff = 0
        hive_temp = row["apparent hive temp"]

        if hive_temp < 32:  # Se la temperatura è al di sotto del range prestabilito...
            # Computazione del coefficiente in base a quanto la temperatura dista dall'estremo inferiore del range,
            # moltiplicato successivamente per una costante
            internal_temp_coeff = (32 - hive_temp) * 1.5
        elif hive_temp > 37:   # Se la temperatura è al di sopra del range prestabilito...
            # Computazione del coefficiente in base a quanto la temperatura dista dall'estremo superiore del range,
            # moltiplicato successivamente per una costante maggiore rispetto alla costante precedente, poiché
            # il rischio è maggiore all'aumento della temperatura
            internal_temp_coeff = (hive_temp - 37) * 2.5
        else:  # Se la temperatura si trova all'interno del range prestabilito...
            # Coefficiente nullo, poiché tale valore dovrebbe essere considerato solo nel momento in cui la
            # temperatura si trovi al di fuori del range
            internal_temp_coeff = 0


        # Calcolo inerente alla differenza di temperatura
        temp_diff_coeff = 0

        if hive_temp >= 32 and hive_temp <= 37:  # Se la temperatura si trova all'interno del range prestabilito...
            # Coefficiente nullo, poiché tale valore dovrebbe essere considerato solo nel momento in cui la
            # temperatura si trovi al di fuori del range
            temp_diff_coeff = 0
        else:  # Se la temperatura non si trova all'interno del range prestabilito...
            # Più la differenza di temperatura è vicina allo 0, maggiore sarà il valore del coefficiente
            temp_diff_coeff = 40 - abs(row["apparent temp diff"] * 0.8)


        # Calcolo inerente alla presenza della regina
        queen_coeff = (1 - row["queen presence"]) * 150  # Pari a 0 se la regina è presente, 150 altrimenti


        # Calcolo della probabilità sommando i tre coefficienti ottenuti al numeratore e i relativi "casi peggiori" al
        # denominatore. In altri termini, al denominatore sono riportati i pesi più alti
        ccd_probability = (internal_temp_coeff + temp_diff_coeff + queen_coeff) / (150 + 60 + 40)

        # Poichè l'euristica non è assoluta, abbiamo deciso di "lanciare" questa probabilità così da causare un
        # minimo di perturbazione all'interno della label. Così facendo, creiamo un'euristica non deterministica
        # che dà senso all'applicazione del Machine Learning
        random_num = random.random()
        ccd = random_num <= ccd_probability
        ccd_list.append(int(ccd))

    dataset["CCD"] = pd.Series(ccd_list)

AristeoHeuristic(dataset)

# La seguente riga è commentata per evitare casualità nelle varie esecuzioni
#dataset.to_csv("hive_dataset_ccd_labeled.csv", index=False) # RANDOM
dataset = pd.read_csv("..\\..\\dataset\\hive_dataset_ccd_labeled.csv")

print(dataset)

# Creazione di due dataframe (uno i cui dati hanno label CDD true, l'altro false)
dataset_ccd = dataset[dataset["CCD"] == 1]
dataset_ccd_no = dataset[dataset["CCD"] == 0]

# Plottiamo la temperatura
plt.hist([dataset_ccd["apparent hive temp"],dataset_ccd_no["apparent hive temp"]], color=["#0C2626","#FFB800"], alpha=0.8, stacked=True, rwidth=0.8)

plt.xlabel("Temperatura Percepita dell'Arnia")
plt.ylabel("Frequenza")
plt.legend(["CCD", "No CCD"])

plt.show()

# Plottiamo la differenza tra temperature
plt.hist([dataset_ccd["apparent temp diff"],dataset_ccd_no["apparent temp diff"]], color=["#0C2626","#FFB800"], alpha=0.8, stacked=True, rwidth=0.8)

plt.xlabel("Differenza tra Temperatura Percepita dell'Arnia e l'Esterno")
plt.ylabel("Frequenza")
plt.legend(["CCD", "No CCD"])

plt.show()

# Cambiamo i dataset da plottare (Temperatura in range)
dataset_ccd = dataset[(dataset["CCD"] == 1) & (dataset["apparent hive temp"] >= 32) & (dataset["apparent hive temp"] <=37)]
dataset_ccd_no = dataset[(dataset["CCD"] == 0) & (dataset["apparent hive temp"] >= 32) & (dataset["apparent hive temp"] <=37)]

# Plottiamo la differenza tra temperatura quando la temperatura interna è in range
plt.hist([dataset_ccd["apparent temp diff"],dataset_ccd_no["apparent temp diff"]], color=["#0C2626","#FFB800"], alpha=0.8, stacked=True, rwidth=0.8)

plt.xlabel("Differenza tra Temp. dell'Arnia e l'Esterno (Temp. in Range)")
plt.ylabel("Frequenza")
plt.legend(["CCD", "No CCD"])

plt.show()

# Cambiamo i dataset da plottare (Temperatura fuori range)
dataset_ccd = dataset[(dataset["CCD"] == 1) & ((dataset["apparent hive temp"] < 32) | (dataset["apparent hive temp"] > 37))]
dataset_ccd_no = dataset[(dataset["CCD"] == 0) & ((dataset["apparent hive temp"] < 32) | (dataset["apparent hive temp"] > 37))]

# Plottiamo la differenza tra temperatura quando la temperatura interna è fuori range
plt.hist([dataset_ccd["apparent temp diff"],dataset_ccd_no["apparent temp diff"]], color=["#0C2626","#FFB800"], alpha=0.8, stacked=True, rwidth=0.8)

plt.xlabel("Differenza tra Temp. dell'Arnia e l'Esterno (Temp. fuori Range)")
plt.ylabel("Frequenza")
plt.legend(["CCD", "No CCD"])

plt.show()

# Creazione di due dataframe (uno i cui dati hanno label CDD true, l'altro false)
dataset_ccd = dataset[dataset["CCD"] == 1]
dataset_ccd_no = dataset[dataset["CCD"] == 0]

# Crea l'istogramma
plt.hist([dataset_ccd["queen presence"],dataset_ccd_no["queen presence"]], color=["#0C2626","#FFB800"], alpha=0.8, stacked=True, rwidth=0.6, bins=2)
plt.xticks([0, 1])
plt.xlabel("Presenza dell'ape Regina")
plt.ylabel("Frequenza")
plt.legend(["CCD", "No CCD"])

plt.show()

# Salviamo il demo dataset
demo_dataset_half_1 = dataset.loc[dataset["CCD"] == 1].sample(30)  # Righe in cui vi è il CCD
demo_dataset_half_2 = dataset.loc[dataset["CCD"] == 0].sample(120)  # Righe in cui non vi è il CCD
demo_ds = pd.concat([demo_dataset_half_1, demo_dataset_half_2], axis=0)  # Unione delle due metà del dataset
# La seguente riga è commentata per evitare casualità nelle varie esecuzioni
#demo_ds.to_csv("CCD_demo_dataset.csv", index=False) # RANDOM
demo_ds = pd.read_csv("..\\..\\dataset\\CCD_demo_dataset.csv")
dataset = dataset.drop(demo_ds.index)  # Eliminazione delle righe estratte dal dataset originale
print(demo_ds)


# Plottiamo la frequenza della label e mostriamo a schermo la conta dei valori
plt.pie(dataset['CCD'].value_counts(), labels=['CCD No', 'CCD Sì'], colors=["#FFB800","#CC9918"], explode=(0, 0.05), autopct="%0.2f")
plt.show()
print(dataset["CCD"].value_counts())


# Proviamo con undersampling
ccd_dataset_US = dataset.groupby("CCD").apply(lambda x: x.sample(n=min(dataset["CCD"].value_counts()[1], len(x))))
ccd_dataset_US = ccd_dataset_US.reset_index(drop=True)
# La seguente riga è commentata per evitare casualità nelle varie esecuzioni
#ccd_dataset_US.to_csv("CCD_dataset_US.csv", index=False) # RANDOM
ccd_dataset_US = pd.read_csv("..\\..\\dataset\\CCD_dataset_US.csv")
print(ccd_dataset_US["CCD"].value_counts())


# Proviamo con l'oversampling
# Definizione del numero di righe del dataset in cui il CCD ha valore 1
CCD_rows = dataset["CCD"].value_counts()[1]

while CCD_rows < 500:

    # Calcolo della presenza dell'ape regina
    if random.random() >= 0.80:
        queen_mock = 0
    else:
        queen_mock = 1

    # Calcolo della temperatura percepita
    if random.random() >= 0.60:  # Probabilità del 40% che la temperatura sia al di sopra del range
        temp_mock = 37 + random.randint(0, 15) + round(random.random(), 2)  # Valore al di sopra del range da 0 a 15
    elif random.random() <= 0.40:  # Probabilità del 40% che la temperatura sia al di sotto del range
        temp_mock = 32 - random.randint(0, 15) - round(random.random(), 2)  # Valore al di sotto del range da 0 a 15
    else:  # Probabilità del 20% che la temperatura sia all'interno del range
        temp_mock = random.randint(32, 37) + round(random.random(), 2)  # Valore all'interno del range da 32 a 36

    # Inutile continuare poiché valori del genere genererebbero una probabilità di CCD pari a 0
    if 32 <= temp_mock <= 37 and queen_mock == 1:
        continue

    # Calcolo della differenza tra temperatura interna ed esterna
    if 32 <= temp_mock <= 37:  # Se la temperatura generata è in range...
        if random.random() >= 0.30:  # Probabilità del 70% che la differenza di temperatura sia tra 0 e 1
            temp_diff_mock = random.randint(0, 1) + round(random.random(), 2)
        else:  # Probabilità del 30% che la differenza di temperatura sia tra 2 e 25
            temp_diff_mock = random.randint(2, 25) + round(random.random(), 2)
    else:  # Se la temperatura generata è fuori range...
        if random.random() >= 0.40:  # Probabilità del 60% che la differenza di temperatura sia tra 0 e 10
            temp_diff_mock = random.randint(0, 10) + round(random.random(), 2)
        elif random.random() <= 0.10:  # Probabilità del 10% che la differenza di temperatura sia tra 21 e 25
            temp_diff_mock = random.randint(21, 25) + round(random.random(), 2)
        else:  # Probabilità del 30% che la differenza di temperatura sia tra 11 e 20
            temp_diff_mock = random.randint(11, 20) + round(random.random(), 2)

    # Creazione di un dizionario con 3 chiavi associate ai 3 valori calcolati in precedenza
    row = {"queen presence": [queen_mock], "apparent hive temp": [temp_mock], "apparent temp diff": [temp_diff_mock]}
    # Conversione del dizionario in un DataFrame
    row = pd.DataFrame.from_dict(row)

    # Applicazione della nostra euristica
    AristeoHeuristic(row)

    # Concatenazione dei DataFrame per ottenere il dataset di campioni con CDD = True
    if int(row["CCD"]) == 1:
        dataset = pd.concat([dataset, row], axis=0)
        CCD_rows+=1

print(dataset)

# Controlliamo se l'oversampling è sensato rimostrando i grafici
#
dataset_ccd = dataset[dataset["CCD"] == 1]
dataset_ccd_no = dataset[dataset["CCD"] == 0]

# Creazione dell'istogramma
# Creazione di due dataframe (uno i cui dati hanno label CDD true, l'altro false)
dataset_ccd = dataset[dataset["CCD"] == 1]
dataset_ccd_no = dataset[dataset["CCD"] == 0]

# Plottiamo la temperatura
plt.hist([dataset_ccd["apparent hive temp"],dataset_ccd_no["apparent hive temp"]], color=["#0C2626","#FFB800"], alpha=0.8, stacked=True, rwidth=0.8)

plt.xlabel("Temperatura Percepita dell'Arnia")
plt.ylabel("Frequenza")
plt.legend(["CCD", "No CCD"])

plt.show()

# Plottiamo la differenza tra temperature
plt.hist([dataset_ccd["apparent temp diff"],dataset_ccd_no["apparent temp diff"]], color=["#0C2626","#FFB800"], alpha=0.8, stacked=True, rwidth=0.8)

plt.xlabel("Differenza tra Temperatura Percepita dell'Arnia e l'Esterno")
plt.ylabel("Frequenza")
plt.legend(["CCD", "No CCD"])

plt.show()

# Cambiamo i dataset da plottare (Temperatura in range)
dataset_ccd = dataset[(dataset["CCD"] == 1) & (dataset["apparent hive temp"] >= 32) & (dataset["apparent hive temp"] <=37)]
dataset_ccd_no = dataset[(dataset["CCD"] == 0) & (dataset["apparent hive temp"] >= 32) & (dataset["apparent hive temp"] <=37)]

# Plottiamo la differenza tra temperatura quando la temperatura interna è in range
plt.hist([dataset_ccd["apparent temp diff"],dataset_ccd_no["apparent temp diff"]], color=["#0C2626","#FFB800"], alpha=0.8, stacked=True, rwidth=0.8)

plt.xlabel("Differenza tra Temp. dell'Arnia e l'Esterno (Temp. in Range)")
plt.ylabel("Frequenza")
plt.legend(["CCD", "No CCD"])

plt.show()

# Cambiamo i dataset da plottare (Temperatura fuori range)
dataset_ccd = dataset[(dataset["CCD"] == 1) & ((dataset["apparent hive temp"] < 32) | (dataset["apparent hive temp"] > 37))]
dataset_ccd_no = dataset[(dataset["CCD"] == 0) & ((dataset["apparent hive temp"] < 32) | (dataset["apparent hive temp"] > 37))]

# Plottiamo la differenza tra temperatura quando la temperatura interna è fuori range
plt.hist([dataset_ccd["apparent temp diff"],dataset_ccd_no["apparent temp diff"]], color=["#0C2626","#FFB800"], alpha=0.8, stacked=True, rwidth=0.8)

plt.xlabel("Differenza tra Temp. dell'Arnia e l'Esterno (Temp. fuori Range)")
plt.ylabel("Frequenza")
plt.legend(["CCD", "No CCD"])

plt.show()

# Creazione di due dataframe (uno i cui dati hanno label CDD true, l'altro false)
dataset_ccd = dataset[dataset["CCD"] == 1]
dataset_ccd_no = dataset[dataset["CCD"] == 0]

# Crea l'istogramma
plt.hist([dataset_ccd["queen presence"],dataset_ccd_no["queen presence"]], color=["#0C2626","#FFB800"], alpha=0.8, stacked=True, rwidth=0.6, bins=2)
plt.xticks([0, 1])
plt.xlabel("Presenza dell'ape Regina")
plt.ylabel("Frequenza")
plt.legend(["CCD", "No CCD"])

plt.show()

# Contiamo i valori
print(dataset["CCD"].value_counts())

# Andiamo a fare undersampling della classe maggioritaria
ccd_dataset_OS = dataset.groupby("CCD").apply(lambda x: x.sample(n=min(500, len(x))))
ccd_dataset_OS = ccd_dataset_OS.reset_index(drop=True)
# La seguente riga è commentata per evitare discrepanze tra i risultati
#ccd_dataset_OS.to_csv("CCD_dataset_OS.csv", index=False)  # RANDOM
ccd_dataset_OS = pd.read_csv("..\\..\\dataset\\CCD_dataset_OS.csv")
print(ccd_dataset_OS["CCD"].value_counts())



















