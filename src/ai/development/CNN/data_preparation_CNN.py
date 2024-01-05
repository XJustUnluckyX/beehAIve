import pandas as pd
import numpy as np
import os

dataset_path = os.path.dirname(__file__) + "../../dataset/cleaned_dataset.csv"

ds = pd.read_csv(dataset_path)

#
# ds = ds.drop(["temp diff", "humidity diff"], axis=1)
# ds["file name"] = ds["file name"].str[:-4] + "_spect.png"
#
# # Salviamo 100 Spettrogrammi a parte per la DEMO (92 dove vi è la regina e 8 dove non vi è)
# # TODO aumentare il numero di Spettrogrammi a 100, cambiare il codice di conseguenza
#
# demo_ds_first_half = ds.loc[ds["queen presence"] == 1].sample(n=92)
# demo_ds_second_half = ds.loc[ds["queen presence"] == 0].sample(n=8)
#
# demo_ds = pd.concat([demo_ds_first_half, demo_ds_second_half], axis=0)
#
# demo_ds.to_csv("dataset/Demo_Dataset.csv", index=False)
#
# ds = ds.drop(demo_ds.index)
#
# # TEST 1: Undersampling della classe maggioritaria
#
# ds_undersampled = ds.groupby("queen presence").apply(lambda x: x.sample(n=min(155, len(x))))
#
# ds_undersampled = ds_undersampled.reset_index(drop=True)
#
# ds_undersampled.to_csv("dataset/TEST1_CNN_Dataset.csv", index=False)

