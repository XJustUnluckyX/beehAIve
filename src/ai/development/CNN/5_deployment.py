from flask import Flask, jsonify, request
from PIL import Image
import json
import numpy as np
import keras.metrics

# Inizializziamo Flask
app = Flask(__name__)
# Carichiamo il modello prima di Flask così da ridurre il tempo di computazione una volta attivato il server
model = keras.models.load_model("CNN.keras")

# Ascoltiamo sull'url http://127.0.0.1:5000/predict_cnn_fia per permetterci di comunicare attraverso HTTP
@app.route("/predict_cnn_fia",methods=["POST"])
def predict_CNN():
    if request.method == "POST":
        # Lavoriamo con i nomi degli spettrogrammi per questione di semplicità, idealmente qui avremmo passato il file
        # audio generato dai sensori per convertirlo in uno spettrogramma
        decoded_data = request.data.decode("utf-8")
        input = json.loads(decoded_data)
        file_name = input["file"]

        # Apriamo l'immagine e la convertiamo in un formato leggibile alla CNN
        image = Image.open("..\\..\\resources\\spectrograms\\" + file_name)
        image_array = np.asarray(image)[:, :, :-1]
        to_predict = []
        to_predict.append(image_array)
        to_predict = np.asarray(to_predict)

        # Effettuiamo la previsione
        prediction = model.predict(to_predict, batch_size = 1)
        prediction = prediction.flatten()
        prediction = np.where(prediction > 0.5, 1, 0)

        # Restituiamo la previsione
        return jsonify ({"cnn_result": int(prediction[0])})

if __name__ == "__main__":
    app.run()