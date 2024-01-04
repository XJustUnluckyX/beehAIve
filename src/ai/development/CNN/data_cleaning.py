import pandas as pd
import numpy as np
import os

#TODO rendere il codice leggibile

# Path del CSV del dataset
dataset_path = os.path.dirname(__file__) + "../../dataset/hive_dataset.csv"
merged_audio_dir = "D:\Desktop\\fafo\merge" # merged_sound_dir = "../resources/merge"

# Lettura CSV
ds = pd.read_csv(dataset_path)

ds["temp diff"] = ds["hive temp"] - ds["weather temp"]
ds["humidity diff"] = ds["hive humidity"] - ds["weather humidity"]

# Eliminiamo le colonne non necessarie al nostro problema
ds = ds.drop(["device", "hive number", "hive pressure", "weather pressure", "wind speed", "gust speed", "weatherID",
             "cloud coverage", "rain", "lat", "long", "queen acceptance", "frames", "target", "queen status",
              "date", "time", "hive temp", "weather temp", "hive humidity", "weather humidity"], axis=1)

# Pulitura nome file
ds["file name"] = ds["file name"].str[:-4] + ".wav"

# Rimozione righe del dataset di cui non possediamo il file audio, anche a conseguenza del merge dei file audio
merged_audio_names = os.listdir(merged_audio_dir)
ds_filename_list = ds['file name'].tolist()

filename_to_remove = set(ds_filename_list) - set(merged_audio_names)

# Produciamo un nuovo dataframe rimuovendo le righe di cui non abbiamo il file
ds2 = ds[~ds["file name"].isin(filename_to_remove)]

ds2.to_csv("../../dataset/cleaned_dataset.csv", index=False)

# Controlliamo quali righe abbiamo rimosso (Solo righe dove è presente la regina)
print(ds["queen presence"].value_counts())
print(ds2["queen presence"].value_counts())

# TEST 1: Undersampling della classe maggioritaria





# Le righe del dataset in cui la regina è assente, per dare un razionale dietro la scelta della rimozione di
# segment1 e segment0 dove singoli. Riscrivere codice TODO
# queen_absent_set = set(ds.loc[ds["queen presence"] == 0]["file name"])


