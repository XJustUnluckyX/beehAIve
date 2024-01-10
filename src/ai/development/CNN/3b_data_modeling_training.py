import keras.metrics
from keras.models import Sequential
from keras.layers import Conv2D, MaxPooling2D, Flatten, Dense
import os
import pandas as pd
import numpy as np
from PIL import Image

# Recupero del dataset pulito e pronto all'uso
cnn_dataset_path = os.path.dirname(__file__) + "\\..\\..\\dataset\\CNN_dataset.csv"
cnn_dataset = pd.read_csv(cnn_dataset_path)

# Recupero degli spettrogrammi contenuti nel dataset
spectrograms_dir = "..\\..\\resources\\spectrograms"
spectrogram_list = cnn_dataset["file name"].tolist()

# Creazione delle variabili per l'addestramento della CNN
X_train = []  # array contenente le immagini di ogni spettrogramma
y_train = np.asarray(cnn_dataset["queen presence"].tolist())  # array contenente le label

# Trasformazione delle immagini degli spettrogrammi in array NumPy da dare in input alla CNN
for file in spectrogram_list:
    image = Image.open(spectrograms_dir + "\\" + file)
    image_array = np.asarray(image)[:, :, :-1]  # Rimozione di una dimensione dell'immagine (alpha)
    X_train.append(image_array)
X_train = np.asarray(X_train)

# Creazione della rete neurale
model = Sequential()
model.add(Conv2D(32, (5, 5), activation="relu", input_shape=(480, 640, 3)))
model.add(MaxPooling2D(pool_size=(2, 2)))
model.add(Conv2D(64, (3, 3), activation="relu"))
model.add(MaxPooling2D(pool_size=(2, 2)))
model.add(Conv2D(128, (3, 3), activation="relu"))
model.add(MaxPooling2D(pool_size=(2, 2)))
model.add(Flatten())
model.add(Dense(256, activation="relu"))
model.add(Dense(1, activation="sigmoid"))

# Compilazione della CNN
metrics = ["accuracy", keras.metrics.Precision(name="precision"), keras.metrics.AUC(name="auc")]
model.compile(optimizer="adam", loss="binary_crossentropy", metrics=metrics)

# Addestramento del modello + Visualizzazione dei risultati a video per ogni fold
model.fit(X_train, y_train, epochs=4, batch_size=16)

# Salvataggio del modello
# model.save("CNN.keras")

