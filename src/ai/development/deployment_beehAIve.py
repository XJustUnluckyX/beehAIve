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

# Prendiamo il dataset per prendere spettrogrammi casuali
cnn_dataset = pd.read_csv("..\\dataset\\CNN_dataset_cleaned.csv")

# Utilizza il modello per predire la presenza di CCD
@app.route("/predict_ccd",methods=["POST"])
def predict_CCD_no_CNN():
    if request.method == "POST":
        # Prendiamo i dati inviati attraverso HTTP
        decoded_data = request.data.decode("utf-8")
        input_json = json.loads(decoded_data)

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
        return jsonify (int(prediction[0]))

# Utilizzo della CNN
@app.route("/use_cnn",methods=["POST"])
def useCNN():
    if request.method == "POST":
        decoded_data = request.data.decode("utf-8")
        input = json.loads(decoded_data)

        # Poiché non abbiamo modo di associare il suono a una misurazione, ne prendiamo uno casuale
        # Lavoriamo con i nomi degli spettrogrammi per questione di semplicità, idealmente qui avremmo passato
        # il file audio generato dai sensori per convertirlo in uno spettrogramma.
        # Questo non cambia in alcun modo le performance del modello
        file_name = input["file name"]

        # Apriamo l'immagine e la convertiamo in un formato leggibile alla CNN
        image = Image.open("..\\resources\\spectrograms\\" + file_name)
        image_array = np.asarray(image)[:, :, :-1]
        to_predict = []
        to_predict.append(image_array)
        to_predict = np.asarray(to_predict)

        # Effettuiamo la previsione sullo spettrogramma
        predicted_queen = cnn_model.predict(to_predict, batch_size = 1)
        predicted_queen = predicted_queen.flatten()
        predicted_queen = np.where(predicted_queen > 0.5, 1, 0)

        # Restituiamo la previsione
        return jsonify(int(predicted_queen[0]))

# Prende uno spettrogramma casuale
@app.route("/get_spectrogram", methods=["POST"])
def getRandomSpectrogram():
    if request.method == "POST":
        return jsonify(cnn_dataset.sample(1).iloc[0]["file name"])


if __name__ == "__main__":
    app.run()