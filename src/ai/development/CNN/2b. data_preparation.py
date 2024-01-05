import pandas as pd
import os

# Recupero del dataset contenente le informazioni relative alle arnie
dataset_path = os.path.dirname(__file__) + "../../dataset/hive_dataset.csv"
dataset = pd.read_csv(dataset_path)

# Recupero degli spettrogrammi corrispettivi a ogni file audio
spectrograms_dir = "D:\\Desktop\\fafo\\spectrograms"  # = "../resources/spectrograms"

# Eliminazione delle colonne non necessarie al nostro problema
dataset = dataset.drop(["device", "hive number", "hive pressure", "weather pressure",
                        "wind speed", "gust speed", "weatherID", "cloud coverage", "rain", "lat",
                        "long", "queen acceptance", "frames", "target", "queen status", "date",
                        "time", "hive temp", "weather temp", "hive humidity", "weather humidity"],
                       axis=1)

# Eliminazione delle righe del dataset di cui il file audio è inesistente o è stato eliminato
dataset["file name"] = dataset["file name"].str[:-4] + "_spect.png"
spectrograms_list = os.listdir(spectrograms_dir)
ds_filename_list = dataset["file name"].tolist()
files_to_remove = set(ds_filename_list) - set(spectrograms_list)
dataset = dataset[~dataset["file name"].isin(files_to_remove)]

# Estrazione delle righe del dataset da utilizzare come demo dell'applicazione
demo_dataset_half_1 = dataset.loc[dataset["queen presence"] == 1].sample(92)  # Righe con regina
demo_dataset_half_2 = dataset.loc[dataset["queen presence"] == 0].sample(8)  # Righe senza regina
demo_ds = pd.concat([demo_dataset_half_1, demo_dataset_half_2], axis=0)  # Unione delle due metà
demo_ds.to_csv("../../dataset/CNN_demo_dataset.csv", index=False)  # Esportazione in CSV
dataset = dataset.drop(demo_ds.index)  # Eliminazione delle righe estratte dal dataset originale

# Undersampling della classe maggioritaria (regina presente) a 150 campioni, poiché corrisponde al
# numero delle istanze della classe minoritaria (regina non presente)
cnn_dataset = dataset.groupby("queen presence").apply(lambda x: x.sample(n=min(150, len(x))))
cnn_dataset = cnn_dataset.reset_index(drop=True)
cnn_dataset.to_csv("../../dataset/CNN_dataset.csv", index=False)
