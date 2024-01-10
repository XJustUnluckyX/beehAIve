import pandas as pd
import numpy as np
import matplotlib.pyplot as plt  # Per visualizzare i grafici
from sklearn.tree import DecisionTreeClassifier # Classificatore Decision Tree
from sklearn.ensemble import RandomForestClassifier # Classificatore Random Forest
from sklearn.naive_bayes import GaussianNB # Classificatore Naive Bayes
from sklearn.neighbors import KNeighborsClassifier # Classificatore K Neighbors
from sklearn.metrics import accuracy_score, precision_score, recall_score, f1_score, confusion_matrix, ConfusionMatrixDisplay, roc_curve, auc # Metriche di valutazione
from sklearn.model_selection import StratifiedKFold, GridSearchCV # K Fold Validation e Grid Search
from matplotlib.colors import ListedColormap # Per cambiare i colori ai plot
import pickle # Per esportare il modello


# Andiamo a prendere i nostri dati di training (Data Modeling)
ccd_dataset_US = pd.read_csv("..\\..\\dataset\\CCD_dataset_US.csv")
ccd_dataset_OS = pd.read_csv("..\\..\\dataset\\CCD_dataset_OS.csv")

X_train_US = ccd_dataset_US[["queen presence", "apparent hive temp", "apparent temp diff"]].to_numpy()
y_train_US = ccd_dataset_US[["CCD"]].to_numpy()
X_train_OS = ccd_dataset_OS[["queen presence", "apparent hive temp", "apparent temp diff"]].to_numpy()
y_train_OS = ccd_dataset_OS["CCD"].to_numpy()

models = {}  #  Dictionary contenente i diversi classificatori

# Decision Tree
models['Decision Tree'] = DecisionTreeClassifier()

# Random Forest
models['Random Forest'] = RandomForestClassifier()

# Naive Bayes
models['Naive Bayes'] = GaussianNB()

# K-Nearest Neighbors
models['K-Nearest Neighbor'] = KNeighborsClassifier()

# Testiamo con l'undersampling

skf = StratifiedKFold(n_splits = 10)  #  Applicazione della Stratified K-Fold Validation (k=10)
accuracy, precision, recall, f1 = {}, {}, {}, {}  # Dictionary relativi alle metriche di valutazione

# Definiamo delle liste per salvare le metriche
for key in models.keys():
    accuracy[key] = []
    precision[key] = []
    recall[key] = []
    f1[key] = []

for train, test in skf.split(X_train_US, y_train_US):
    for key in models.keys():

        # Addestramento del modello
        models[key].fit(X_train_US[train], y_train_US[train].ravel())

        # Predizioni
        predictions = models[key].predict(X_train_US[test])

        # Calcolo delle metriche
        accuracy[key].append(accuracy_score(y_true = y_train_US[test], y_pred = predictions))
        precision[key].append(precision_score(y_true = y_train_US[test], y_pred = predictions))
        recall[key].append(recall_score(y_true = y_train_US[test], y_pred = predictions))
        f1[key].append(f1_score(y_true = y_train_US[test], y_pred = predictions))

mean_accuracy_US = []
mean_precision_US = []
mean_recall_US = []
mean_f1_US = []

# Calcolo delle medie delle metriche di valutazione
for key in models.keys():
    mean_accuracy_US.append(round(np.mean(accuracy[key]),4))
    mean_precision_US.append(round(np.mean(precision[key]),4))
    mean_recall_US.append(round(np.mean(recall[key]),4))
    mean_f1_US.append(round(np.mean(f1[key]),4))


# Testiamo con l'oversampling
# Applicazione della Stratified K-Fold Validation (k=10)
skf = StratifiedKFold(n_splits = 10)
accuracy, precision, recall, f1 = {}, {}, {}, {}

# Definiamo delle liste per salvare le metriche
for key in models.keys():
    accuracy[key] = []
    precision[key] = []
    recall[key] = []
    f1[key] = []

for train, test in skf.split(X_train_OS, y_train_OS):
    for key in models.keys():

        # Addestramento del modello
        models[key].fit(X_train_OS[train], y_train_OS[train])

        # Predizioni
        predictions = models[key].predict(X_train_OS[test])

        # Calcolo delle metriche
        accuracy[key].append(accuracy_score(y_true = y_train_OS[test], y_pred = predictions))
        precision[key].append(precision_score(y_true = y_train_OS[test], y_pred = predictions))
        recall[key].append(recall_score(y_true = y_train_OS[test], y_pred = predictions))
        f1[key].append(f1_score(y_true = y_train_OS[test], y_pred = predictions))

mean_accuracy_OS = []
mean_precision_OS = []
mean_recall_OS = []
mean_f1_OS = []

# Calcolo delle medie delle metriche di valutazione
for key in models.keys():
    mean_accuracy_OS.append(round(np.mean(accuracy[key]),4))
    mean_precision_OS.append(round(np.mean(precision[key]),4))
    mean_recall_OS.append(round(np.mean(recall[key]),4))
    mean_f1_OS.append(round(np.mean(f1[key]),4))

# Visualizzazione delle medie calcolate precedentemente
scores = pd.DataFrame(index=models.keys(), columns=["Accuracy US", "Accuracy OS", "Precision US", "Precision OS", "Recall US", "Recall OS", "F1 Score US", "F1 Score OS"])
scores["Accuracy US"] = mean_accuracy_US  # Media delle Accuracy ottenute con undersampling
scores["Accuracy OS"] = mean_accuracy_OS  # Media delle Accuracy ottenute con undersampling e oversampling
scores["Precision US"] = mean_precision_US  # Media delle Precision ottenute con undersampling
scores["Precision OS"] = mean_precision_OS  # Media delle Precision ottenute con undersampling e oversampling
scores["Recall US"] = mean_recall_US  # Media delle Recall ottenute con undersampling
scores["Recall OS"] = mean_recall_OS  # Media delle Recall ottenute con undersampling e oversampling
scores["F1 Score US"] = mean_f1_US  # Media degli F1 Score ottenuti con undersampling
scores["F1 Score OS"] = mean_f1_OS  # Media degli F1 Score ottenuti con undersampling e oversampling

print(scores)

# Selezioniamo la soluzione con l'oversampling
X_train = X_train_OS
y_train = y_train_OS


# Ottimizziamo Random Forest
rf = RandomForestClassifier()

n_estimators = [1,3,5,7,9,11,13] # Numero di alberi
max_depth = [10,20,30,40,50,60,70,80,90,100] # Profondità massima
min_samples_split = [2, 6, 10] #Numero minimo di sample per effettuare uno split
min_samples_leaf = [1, 3, 5] #Numero minimo di sample per generare una foglia
bootstrap = [True, False]

rf_grid = {
    "n_estimators": n_estimators,
    "max_depth": max_depth,
    "min_samples_split": min_samples_split,
    "min_samples_leaf": min_samples_leaf,
    "bootstrap": bootstrap
}

# Invochiamo la Grid Search per trovare i parametri ottimi
random_forest = GridSearchCV(estimator = rf, param_grid = rf_grid, scoring="accuracy", n_jobs=8, cv=5, verbose=2, refit="True")
# Una volta trovati trainiamo il modello sul nostro training set
random_forest.fit(X_train, y_train)

best_config_rf = random_forest.best_params_
print(f"Configurazione ottimale: {best_config_rf}")

# Ottimizziamo Naive Bayes
gNB = GaussianNB()

nb_grid = {
    "var_smoothing": np.logspace(0, -9, num=100)
}

# Invochiamo la Grid Search per trovare i parametri ottimi
naive_bayes = GridSearchCV(estimator = gNB, param_grid = nb_grid, scoring="accuracy", n_jobs=8, cv=5, verbose=2, refit="True")
# Una volta trovati trainiamo il modello sul nostro training set
naive_bayes.fit(X_train, y_train)

best_config_nb = naive_bayes.best_params_
print(f"Configurazione ottimale: {best_config_nb}")


# Andiamo a valutare sul demo dataset (Evaluation)
ccd_demo_dataset = pd.read_csv("..\\..\\dataset\\CCD_demo_dataset.csv")
print(ccd_demo_dataset)

# Convertiamo i dati in un formato leggibile al modello
X_test = ccd_demo_dataset[["queen presence", "apparent hive temp", "apparent temp diff"]].to_numpy()
y_test = ccd_demo_dataset["CCD"].to_numpy()

# Utilizzo del Random Forest
prediction = random_forest.predict(X_test)

# Valutazione del modello in base alle metriche
accuracy_val_rf = accuracy_score(y_true = y_test, y_pred = prediction)
precision_val_rf = precision_score(y_true = y_test, y_pred = prediction)
recall_val_rf = recall_score(y_true = y_test, y_pred = prediction)
f1_val_rf = f1_score(y_true = y_test, y_pred = prediction)

print(f"Random Forest Accuracy: {accuracy_val_rf*100:.4f}%, Precision: {precision_val_rf*100:.4f}%, Recall: {recall_val_rf*100:.4f}%, F1 Score: {f1_val_rf*100:.4f}%")

# Creazione della matrice di confusione
matrix = confusion_matrix(y_true = y_test, y_pred = prediction)

# Definizione dei colori da utilizzare nella matrice con relativa color map e color matrix
colors=['#FFB800', '#CC9918']
color_map = ListedColormap('white', name='colormap_list')
color_matrix = [['#FFB800', '#805711'], ['#805711', '#FFB800']]
color_text_matrix = [['black', 'white'], ['white', 'black']]

# Visualizzazione della matrice di confusione
plt.imshow(matrix, cmap=color_map, origin='upper')

for i in range(matrix.shape[0]):
    for j in range(matrix.shape[1]):
        # Definizione dei dettagli in merito a colore e testo per le celle della matrice
        plt.text(j, i, str(matrix[i, j]), color=color_text_matrix[i][j])
        plt.fill_between([j-0.5, j+0.5], i-0.5, i+0.5, color=color_matrix[i][j], alpha=1)

# Set dei valori e delle labels presenti su asse x e asse y
plt.xticks([0, 1])
plt.yticks([0, 1])
plt.xlabel('Predicted label')
plt.ylabel('True label')

plt.show()

# Calcolo del tasso di falsi positivi, veri positivi e le soglie
false_positive_rate, true_positive_rate, threshold = roc_curve(y_test, prediction)
# Disegno della linea di riferimento
plt.plot([0, 1], [0, 1], 'k--')
# Disegno della ROC Curve, etichettandola con il valore dell'AUC (più vicino è a 1, migliore è il modello)
plt.plot(false_positive_rate, true_positive_rate, label='AUC = {:.4f})'.format(auc(false_positive_rate, true_positive_rate)), color="#FFB800")

# Definizione del titolo e delle labels
plt.title('ROC curve Random Forest')
plt.xlabel('False positive rate')
plt.ylabel('True positive rate')
plt.legend(loc='best')

plt.show()

# Utilizzo del Naive Bayes
prediction = naive_bayes.predict(X_test)

# Valutazione del modello in base alle metriche
accuracy_val_nb = accuracy_score(y_true = y_test, y_pred = prediction)
precision_val_nb = precision_score(y_true = y_test, y_pred = prediction)
recall_val_nb = recall_score(y_true = y_test, y_pred = prediction)
f1_val_nb = f1_score(y_true = y_test, y_pred = prediction)

print(f"Naive Bayes Accuracy: {accuracy_val_nb*100:.4f}%, Precision: {precision_val_nb*100:.4f}%, Recall: {recall_val_nb*100:.4f}%, F1 Score: {f1_val_nb*100:.4f}%")

# Creazione della matrice di confusione
matrix = confusion_matrix(y_true = y_test, y_pred = prediction)

# Definizione dei colori da utilizzare nella matrice con relativa color map e color matrix
colors=['#FFB800', '#805711']
color_map = ListedColormap('white', name='colormap_list')
color_matrix = [['#FFB800', '#805711'], ['#805711', '#FFB800']]
color_text_matrix = [['black', 'white'], ['white', 'black']]

# Visualizzazione della matrice di confusione
plt.imshow(matrix, cmap=color_map, origin='upper')

for i in range(matrix.shape[0]):
    for j in range(matrix.shape[1]):
        # Definizione dei dettagli in merito a colore e testo per le celle della matrice
        plt.text(j, i, str(matrix[i, j]), color=color_text_matrix[i][j])
        plt.fill_between([j-0.5, j+0.5], i-0.5, i+0.5, color=color_matrix[i][j], alpha=1)

# Set dei valori e delle labels presenti su asse x e asse y
plt.xticks([0, 1])
plt.yticks([0, 1])
plt.xlabel('Predicted label')
plt.ylabel('True label')

plt.show()

# Calcolo del tasso di falsi positivi, veri positivi e le soglie
false_positive_rate, true_positive_rate, threshold = roc_curve(y_test, prediction)
# Disegno della linea di riferimento
plt.plot([0, 1], [0, 1], 'k--')
# Disegno della ROC Curve, etichettandola con il valore dell'AUC (più vicino è a 1, migliore è il modello)
plt.plot(false_positive_rate, true_positive_rate, label='AUC = {:.4f})'.format(auc(false_positive_rate, true_positive_rate)), color="#FFB800")

# Definizione del titolo e delle labels
plt.title('ROC curve Naive Bayes')
plt.xlabel('False positive rate')
plt.ylabel('True positive rate')
plt.legend(loc='best')

plt.show()

# Mostriamo le differenze tra i due modelli
algorithms = ['Random Forest', 'Naive Bayes']

# Risultati delle prestazioni racchiusi in una lista (una per metrica)
accuracy_results = [accuracy_val_rf, accuracy_val_nb]
precision_results = [precision_val_rf, precision_val_nb]
recall_results = [recall_val_rf, recall_val_nb]
f1_results = [f1_val_rf, f1_val_nb]

# Posizioni delle barre
index = np.arange(len(algorithms))
# Larghezza delle barre
bar_width = 0.15

# Dimensioni della figura
plt.figure(figsize=(9, 6))
# Barre relative a Accuracy, Precision, Recall e F1 Score
accuracy_bar = plt.bar(index, accuracy_results, bar_width, label='Accuracy', color='#FFE087', edgecolor='black')
precision_bar = plt.bar(index + bar_width, precision_results, bar_width, label='Precision', color='#FFB800', edgecolor='black')
recall_bar = plt.bar(index + 2 * bar_width, recall_results, bar_width, label='Recall', color='#CC9918', edgecolor='black')
f1_bar = plt.bar(index + 3 * bar_width, f1_results, bar_width, label='F1 score', color='#805711', edgecolor='black')

# Dettagli del grafico
plt.xlabel('Classificatore')
plt.ylabel('Prestazioni')
plt.title("Confronto di Accuracy, Precision, Recall e F1 score tra classificatori")
plt.xticks(index + 1.5 * bar_width, algorithms)
plt.legend()

plt.show()

# Il seguente codice è commentato per evitare casualità nelle varie esecuzioni.
# Il modello sarà da noi fornito all'interno della repo sotto src/ai/development/ML
# f = open("Naive_Bayes.pickle", "wb") # RANDOM
# pickle.dump(naive_bayes, f)
# f.close()