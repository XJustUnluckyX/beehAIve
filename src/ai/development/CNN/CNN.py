import keras.metrics
from keras.models import Sequential
from keras.layers import Conv2D, MaxPooling2D, AveragePooling2D, Flatten, Dense
import pandas as pd
import numpy as np
from PIL import Image
from sklearn.model_selection import StratifiedKFold

# Prendiamo i dati da dare al modello
ds = pd.read_csv("../../dataset/TEST1_CNN_dataset.csv")
spectrogram_dir = "D:\Desktop\\fafo\spectrograms" # spect_dir = "../resources/spectrograms"

file_list = ds["file name"].tolist()

X_train = []
y_train = np.asarray(ds["queen presence"].tolist())

for file in file_list:
    image = Image.open(spectrogram_dir + "\\" + file)
    image_array = np.asarray(image)[:, :, :-1] #Facciamo [ :, :, :-1] per eliminare la dimensione dell'alpha non essendo questa significativa al problema
    X_train.append(image_array)

X_train = np.asarray(X_train)

# Parametri da sperimentare
# pooling = ["MaxPooling", "AveragePooling"]
# dense_neurons = [32, 64, 128, 256]
# optimizer = ["SGD", "rmsprop", "adam", "adamw", "adadelta", "adagrad", "adamax", "adafactor", "nadam", "ftrl"]
# epochs = [3, 4, 5, 6, 7, 8, 9, 10]
# batch_size = [8, 16, 32]
pooling = "MaxPooling" # {MaxPooling, AveragePooling}
dense_neurons = 128 # {32, 64, 128, 256}
optimizer = "adam" # {SGD, rmsprop, adam, adamw, adadelta, adagrad, adamax, adafactor, nadam, ftrl}
epochs = 3 # { 3, 4, 5, 6, 7, 8, 9, 10 }
batch_size = 16 # {8, 16, 32}

val_loss_per_fold = []
val_accuracy_per_fold = []
val_precision_per_fold = []
val_AUC_per_fold = []

training_loss_per_fold = []
training_accuracy_per_fold = []
training_precision_per_fold = []
training_AUC_per_fold = []


# Prepariamo la KFold Validation
skf = StratifiedKFold(n_splits=5)

fold_no = 1
for train, test in skf.split(X_train, y_train):
    # Creazione del modello
    model = Sequential()
    model.add(Conv2D(32, (3, 3), activation="relu", input_shape=(480, 640, 3)))
    model.add(MaxPooling2D(pool_size=(2, 2)))
    model.add(Conv2D(64, (3, 3), activation="relu"))
    model.add(MaxPooling2D(pool_size=(2, 2)))
    model.add(Flatten())
    model.add(Dense(dense_neurons, activation="relu"))
    model.add(Dense(1, activation="sigmoid"))

    # Compilazione del modello
    model.compile(optimizer=optimizer, loss="binary_crossentropy", metrics=[keras.metrics.BinaryAccuracy(), keras.metrics.Precision(name="precision"), keras.metrics.AUC(name="auc"),])

    print(f"--------- Fold n. {fold_no} ---------")
    # Addestramento del modello
    history = model.fit(X_train[train], y_train[train], epochs = epochs, batch_size = batch_size, validation_data=(X_train[test], y_train[test]))

    # Valutazione del modello
    eval = model.evaluate(X_train[test], y_train[test], verbose = 0)

    # Utilizziamo l'oggetto eval per ottenere le metriche di testing
    val_loss_per_fold.append(eval[0])
    val_accuracy_per_fold.append(eval[1] * 100)
    val_precision_per_fold.append(eval[2] * 100)
    val_AUC_per_fold.append(eval[3])

    # Utilizziamo la history per salvare le metriche di training (per diagnosticare eventuale overfitting)
    training_loss_per_fold.append(history.history["loss"][epochs-1])
    training_accuracy_per_fold.append(history.history["binary_accuracy"][epochs-1] * 100)
    training_precision_per_fold.append(history.history["precision"][epochs-1] * 100)
    training_AUC_per_fold.append(history.history["auc"][epochs-1])

    # Iterazione k-fold validation
    fold_no+=1


try:
    with open("../../logs/CNN Evaluation Log.txt", "a") as file:
        file.write(f"-------------------\n"
                "STATO CONFIGURAZIONE: \n"
                f"Pooling: {pooling}, Dense_neurons: {dense_neurons}, Optimizer: {optimizer}, "
                f"Epochs: {epochs}, Batch_Size: {batch_size}\n\n"
                "VALUTAZIONE TRAINING:\n"
                f"Training_Loss: {np.mean(training_loss_per_fold):.4f}, Training_Accuracy: {np.mean(training_accuracy_per_fold):.4f}%, "
                f"Training_Precision: {np.mean(training_precision_per_fold):.4f}%, Training_AUC: {np.mean(training_AUC_per_fold):.4f}\n\n"
                "VALUTAZIONE TESTING:\n"
                f"Test Loss: {np.mean(val_loss_per_fold):.4f}, Test Accuracy: {np.mean(val_accuracy_per_fold):.4f}%, "
                f"Test Precision: {np.mean(val_precision_per_fold):.4f}%, Test AUC: {np.mean(val_AUC_per_fold):.4f}\n")
except FileNotFoundError:
    print("File not found.")


# SPERIMENTAZIONE EMPIRICA:
# 1. Pooling (Max vs Average)
# 2. Parametro "units" (numero di neuroni) della classe Dense
# 3. Ottimizzatori (SGD, rmsprop, adam, adamw, adadelta, adagrad, adamax, adafactor, nadam, ftrl)
# 4. Numero di epoche
# 5. Dimensioni del batch