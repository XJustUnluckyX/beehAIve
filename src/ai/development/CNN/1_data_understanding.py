import pandas as pd
import os

# Lettura del dataset contenente le informazioni relative alle arnie
dataset_path = os.path.dirname(__file__) + "\\..\\..\\dataset\\hive_dataset.csv"
dataset = pd.read_csv(dataset_path)
print(dataset)

# Conteggio delle righe del dataset
print(f"Dataset samples: {len(dataset)}")

# Analisi delle colonne del dataset
print(dataset.columns)

# Analisi del formato dei file audio contenuti nel dataset
print(dataset["file name"].sample(n=1))

# Verifica del bilanciamento/sbilanciamento della classe da predire (presenza della regina)
print(dataset["queen presence"].value_counts())

# Mostriamo un grafico a torta per contare le label
plt.pie(dataset["queen presence"].value_counts(), labels=["Queen Present", "Queen Not Present"], colors=["#FFB800","#CC9918"], explode=(0,0.05), autopct="%0.2f")
plt.show()

# Ricerca di eventuali valori nulli all'interno del dataset
null_values = dataset.isna()
null_values_count = null_values.sum()
print(null_values_count)
