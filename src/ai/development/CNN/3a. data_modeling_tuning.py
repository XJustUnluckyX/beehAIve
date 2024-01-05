import keras.metrics
from keras.models import Sequential
from keras.layers import Conv2D, MaxPooling2D, Flatten, Dense
import os
import pandas as pd
import numpy as np
from PIL import Image
from sklearn.model_selection import StratifiedKFold

# Recupero del dataset pulito e pronto all'uso
cnn_dataset_path = os.path.dirname(__file__) + "../../dataset/CNN_dataset.csv"
cnn_dataset = pd.read_csv(cnn_dataset_path)

# Recupero degli spettrogrammi contenuti nel dataset
spectrograms_dir = "D:\\Desktop\\fafo\\spectrograms"  # = "../resources/spectrograms"
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


# Inizializzazione delle liste contenenti i risultati ottenuti dal training, da utilizzare
# successivamente nella fase di validazione ed esaminare le prestazioni del modello

# Liste contenenti i valori di valutazione sul test set
val_loss_per_fold = []
val_accuracy_per_fold = []
val_precision_per_fold = []
val_AUC_per_fold = []

# Liste contenenti i valori di valutazione sul training set (per la diagnosi di overfitting)
training_loss_per_fold = []
training_accuracy_per_fold = []
training_precision_per_fold = []
training_AUC_per_fold = []

# Inizializzazione degli iperparametri da noi individuati per la CNN
pooling = "MaxPooling"  # L'Average Pooling risultava in prestazioni tendenzialmente peggiori
dense_neurons = 256  # Un numero minore di neuroni porta il modello all'overfitting, mentre un
# numero maggiore ha un costo computazionale troppo elevato
optimizer = "adam"  # Altri optimizers presentano prestazioni pessime o causano l'esplosione del
# gradiente e conseguentemente della loss function
epochs = 4  # Il numero ottimale di epoche si assesta tra 3 e 4, poiché un numero inferiore porta
# all'underfitting, mentre un numero maggiore porta molto velocemente all'overfitting
batch_size = 16  # Una dimensione minore del batch non restituiva prestazioni degne di nota,
# mentre una dimensione maggiore rende l'algoritmo lento e da un costo computazionale elevato


# Applicazione della stratified k-fold validation (k = 5)
skf = StratifiedKFold(n_splits=5)
fold_no = 1
for train, test in skf.split(X_train, y_train):
    # Creazione della rete neurale

    # Dalla nostra sperimentazione empirica siamo giunti alla conclusione di dover utilizzare
    # tre layer convoluzionali, di cui il primo con un kernel maggiore (le immagini sono grandi),
    # poiché ci portano ai risultati migliori

    model = Sequential()
    model.add(Conv2D(32, (5, 5), activation="relu", input_shape=(480, 640, 3)))
    model.add(MaxPooling2D(pool_size=(2, 2)))
    model.add(Conv2D(64, (3, 3), activation="relu"))
    model.add(MaxPooling2D(pool_size=(2, 2)))
    model.add(Conv2D(128, (3, 3), activation="relu"))
    model.add(MaxPooling2D(pool_size=(2, 2)))
    model.add(Flatten())
    model.add(Dense(dense_neurons, activation="relu"))
    model.add(Dense(1, activation="sigmoid"))

    # Compilazione della CNN
    metrics = ["accuracy", keras.metrics.Precision(name="precision"), keras.metrics.AUC(name="auc")]
    model.compile(optimizer=optimizer, loss="binary_crossentropy", metrics=metrics)

    # Addestramento del modello + Visualizzazione dei risultati a video per ogni fold
    print(f"--------- Fold n. {fold_no} ---------")
    history = model.fit(X_train[train], y_train[train], epochs=epochs, batch_size=batch_size,
                        validation_data=(X_train[test], y_train[test]))

    # Valutazione del modello per comprendere l'affidabilità della configurazione
    eval = model.evaluate(X_train[test], y_train[test], verbose=0)

    # Salvataggio delle metriche di testing attraverso l'oggetto eval
    val_loss_per_fold.append(eval[0])
    val_accuracy_per_fold.append(eval[1] * 100)
    val_precision_per_fold.append(eval[2] * 100)
    val_AUC_per_fold.append(eval[3])

    # Salvataggio delle metriche di training attraverso l'oggetto history
    training_loss_per_fold.append(history.history["loss"][epochs-1])
    training_accuracy_per_fold.append(history.history["accuracy"][epochs-1] * 100)
    training_precision_per_fold.append(history.history["precision"][epochs-1] * 100)
    training_AUC_per_fold.append(history.history["auc"][epochs-1])

    # Passaggio all'iterazione successiva, quindi al prossimo fold
    fold_no += 1

# Salvataggio dei risultati all'interno di un log per la configurazione di iperparametri utilizzata
try:
    with open("../../logs/CNN Evaluation Log.txt", "a") as file:
        file.write(f"-------------------\n"
                "STATO CONFIGURAZIONE: \n"
                f"Pooling: {pooling}, Dense_neurons: {dense_neurons}, Optimizer: {optimizer}, "
                f"Epochs: {epochs}, Batch_Size: {batch_size}\n\n"
                "VALUTAZIONE TRAINING:\n"
                f"Training_Loss: {np.mean(training_loss_per_fold):.4f},"
                   f"Training_Accuracy: {np.mean(training_accuracy_per_fold):.4f}%, "
                f"Training_Precision: {np.mean(training_precision_per_fold):.4f}%,"
                   f"Training_AUC: {np.mean(training_AUC_per_fold):.4f}\n\n"
                "VALUTAZIONE TESTING:\n"
                f"Test Loss: {np.mean(val_loss_per_fold):.4f},"
                   f"Test Accuracy: {np.mean(val_accuracy_per_fold):.4f}%, "
                f"Test Precision: {np.mean(val_precision_per_fold):.4f}%,"
                   f"Test AUC: {np.mean(val_AUC_per_fold):.4f}\n")
except FileNotFoundError:
    print("File not found.")
