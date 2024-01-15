from flask import Flask, jsonify, request
import json
import numpy as np
import pickle

# Inizializziamo Flask
app = Flask(__name__)
# Carichiamo il modello prima di Flask così da ridurre il tempo di computazione una volta attivato il server
f = open("Naive_Bayes.pickle", "rb")
model = pickle.load(f)
f.close()

# Ascoltiamo sull'url http://127.0.0.1:5000/predict_ccd_fia per permetterci di comunicare attraverso HTTP
@app.route("/predict_ccd_fia",methods=["POST"])
def predict_CCD():
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
        prediction = model.predict(predict_array)
        prediction = prediction.flatten()
        prediction = np.where(prediction > 0.5, 1, 0)

        # Restituiamo la previsione
        return jsonify ({"ccd_result": int(prediction[0])})

if __name__ == "__main__":
    app.run()