from flask import Flask, jsonify, request
from PIL import Image
import pandas as pd
import numpy as np
import keras.metrics
import json
import pickle

# Inizializziamo Flask
app = Flask(__name__)
# Carichiamo i modelli prima di Flask così da ridurre il tempo di computazione una volta attivato il server
cnn_model = keras.models.load_model("CNN\\CNN.keras")

f = open("ML\\Naive_Bayes.pickle", "rb")
ccd_model = pickle.load(f)
f.close()

# Carichiamo inoltre il dataset per simulare i suoni
cnn_dataset = pd.read_csv("..\\dataset\\CNN_dataset_cleaned.csv")

# Definiamo la funzione euristica così da confrontare i risultati
def AristeoHeuristic(row):

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

    return ccd_probability


# Modalità d'uso 1, SENZA CNN
@app.route("/ccd_no_cnn",methods=["POST"])
def predict_CCD_no_CNN():
    if request.method == "POST":
        # Prendiamo i dati inviati attraverso HTTP
        decoded_data = request.data.decode("utf-8")
        input_json = json.loads(decoded_data)

        # Calcoliamo la probabilità effettiva di CCD seguendo l'euristica
        ccd_probability = AristeoHeuristic(input_json)

        # In ordine abbiamo queen presence, apparent hive temp, apparent temp diff
        to_predict = np.fromiter(input_json.values(), dtype=float)

        # Formattiamo l'input in un formato leggibile al nostro modello
        list = []
        list.append(to_predict)
        predict_array = np.asarray(list)

        # Effettuiamo la previsione
        prediction = ccd_model.predict(predict_array)
        prediction = prediction.flatten()
        prediction = np.where(prediction > 0.5, 1, 0)

        # Restituiamo la previsione
        return jsonify ({"ccd_result": int(prediction[0]), "ccd_probability": ccd_probability})

# Modalità d'uso 2, CON CNN
@app.route("/ccd",methods=["POST"])
def predict_CCD():
    if request.method == "POST":
        decoded_data = request.data.decode("utf-8")
        input = json.loads(decoded_data)

        # Poiché non abbiamo modo di associare il suono a una misurazione, ne prendiamo uno casuale
        # Lavoriamo con i nomi degli spettrogrammi per questione di semplicità, idealmente qui avremmo passato
        # il file audio generato dai sensori per convertirlo in uno spettrogramma.
        # Questo non cambia in alcun modo le performance del modello
        random_row = cnn_dataset.sample(1)
        actual_queen = random_row.iloc[0]["queen presence"]

        # Apriamo l'immagine e la convertiamo in un formato leggibile alla CNN
        image = Image.open("..\\resources\\spectrograms\\" + random_row.iloc[0]["file name"])
        image_array = np.asarray(image)[:, :, :-1]
        to_predict = []
        to_predict.append(image_array)
        to_predict = np.asarray(to_predict)

        # Andiamo ad aggiungere la queen presence così da calcolare l'euristica
        input["queen presence"] = actual_queen
        ccd_true_probability = AristeoHeuristic(input)

        # Effettuiamo la previsione sullo spettrogramma
        predicted_queen = cnn_model.predict(to_predict, batch_size = 1)
        predicted_queen = predicted_queen.flatten()
        predicted_queen = np.where(predicted_queen > 0.5, 1, 0)

        # Sostituiamo la queen presence vera con quella
        input["queen presence"] = int(predicted_queen[0])

        # In ordine abbiamo queen presence, apparent hive temp, apparent temp diff
        to_predict = np.fromiter(input.values(), dtype=float)

        # Formattiamo l'input in un formato leggibile al nostro modello
        list = []
        list.append(to_predict)
        predict_array = np.asarray(list)

        # Effettuiamo la previsione
        prediction = ccd_model.predict(predict_array)
        prediction = prediction.flatten()
        prediction = np.where(prediction > 0.5, 1, 0)

        # Restituiamo la previsione
        return jsonify ({"ccd_result": int(prediction[0]), "ccd_true_probability": ccd_true_probability, "predicted_queen": int(predicted_queen[0]), "actual_queen": actual_queen})


if __name__ == "__main__":
    app.run()