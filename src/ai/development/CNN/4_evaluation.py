import keras.metrics
import os
import pandas as pd
import numpy as np
from PIL import Image
from sklearn.metrics import confusion_matrix, ConfusionMatrixDisplay, roc_curve
import matplotlib.pyplot as plt

# Recupero del dataset utilizzato per la demo dell'applicazione
cnn_demo_dataset_path = os.path.dirname(__file__) + "\\..\\..\\dataset\\CNN_demo_dataset.csv"
cnn_demo_dataset = pd.read_csv(cnn_demo_dataset_path)

# Recupero degli spettrogrammi
spectrograms_dir = "..\\..\\resources\\spectrograms"
spectrogram_list = cnn_demo_dataset["file name"].tolist()

# Creazione delle variabili per la predizione e la valutazione della CNN
X_test = []  # array contenente le immagini di ogni spettrogramma
y_test = np.asarray(cnn_demo_dataset["queen presence"].tolist())  # array contenente le label

# Trasformazione delle immagini degli spettrogrammi in array NumPy da dare in input alla CNN
for file in spectrogram_list:
    image = Image.open(spectrograms_dir + "\\" + file)
    image_array = np.asarray(image)[:, :, :-1]  # Rimozione di una dimensione dell'immagine (alpha)
    X_test.append(image_array)
X_test = np.asarray(X_test)

# Caricamento della CNN sviluppata durante la fase di data modeling (data_modeling_training.py)
model = keras.models.load_model("CNN.keras")

# Esecuzione della previsione e visualizzazione dei risultati
prediction = model.predict(X_test)
prediction = prediction.flatten()
prediction = np.where(prediction > 0.5, 1, 0)
print(prediction)
print(y_test)

# Valutazione delle performance del modello
eval = model.evaluate(X_test, y_test, verbose=0)

print("VALUTAZIONE")
print(f"Loss: {eval[0]:.4f}, Accuracy: {(eval[1] * 100):.4f}, Precision: {(eval[2] * 100):.4f}, AUC: {eval[3]:.4f}")

# Produzione della Confusion Matrix relativa alla CNN. Dev'essere letta in questo modo:
# - Il riquadro in alto a sinistra riporta il numero di True Negative
# - Il riquadro in alto a destra riporta il numero di False Positive
# - Il riquadro in basso a sinistra riporta il numero di False Negative
# - Il riquadro in basso a destra riporta il numero di True Positive
# I riquadri a noi interessati sono quelli in alto. In particolare, ci interessa avere un alto
# numero di True Negative rispetto ai False Positive. Questo è dovuto al fatto che i falsi allarmi
# non rappresentano una problematica nel nostro sistema quanto il predire erroneamente che l'ape
# regina è presente nell'arnia, quando in realtà così non è.
# Predire erroneamente la presenza della regina porterebbe l'apicoltore a non effettuare alcun
# intervento, portando l'arnia al suo inevitabile peggioramento.
matrix = confusion_matrix(y_test, prediction)
disp = ConfusionMatrixDisplay(confusion_matrix=matrix)
disp.plot()
plt.show()

# Creazione della ROC Curve relativa alla CNN
false_positive_rate, true_positive_rate, threshold = roc_curve(y_test, prediction)
plt.plot([0, 1], [0, 1], 'k--')
plt.plot(false_positive_rate, true_positive_rate, label='AUC = {:.4f})'.format(eval[0]))
plt.title('ROC curve')
plt.xlabel('False positive rate')
plt.ylabel('True positive rate')
plt.legend(loc='best')
plt.show()
