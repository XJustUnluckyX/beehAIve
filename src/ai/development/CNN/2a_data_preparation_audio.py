import gc
import os
import multiprocessing
import librosa.display
import matplotlib.pyplot as plt
import numpy as np
from pydub import AudioSegment
    
# Recupero delle directory relative ai segmenti audio, ai file audio uniti e relativi spettrogrammi
audio_dir = ""  # = "..\\..\\resources\\sound_files"
merged_audio_dir = ""  # = "..\\..\\resources\\merged_sound_files"
spectrograms_dir = "..\\..\\resources\\spectrograms"

THREAD_NO = 5  # Definizione del numero di thread per la parallelizzazione

audio_list = os.listdir(audio_dir)

# Definizione di tre insiemi per trovare file audio con meno di tre segmenti
segment0_set = set()
segment1_set = set()
segment2_set = set()

# Aggiunta dei segmenti audio nei relativi insiemi
for file in audio_list:
    if file.endswith("segment0.wav"):  # Aggiunta dei segmenti 0 all'insieme "segment0_set"
        file = file[:-14]
        segment0_set.add(file)
    if file.endswith("segment1.wav"):  # Aggiunta dei segmenti 1 all'insieme "segment1_set"
        file = file[:-14]
        segment1_set.add(file)
    if file.endswith("segment2.wav"):  # Aggiunta dei segmenti 2 all'insieme "segment2_set"
        file = file[:-14]
        segment2_set.add(file)

# Definizione di due insiemi contenenti i file audio da eliminare
set_to_remove1 = segment0_set.difference(segment1_set)  # Insieme contenente i file audio che
# presentano il segmento 0 ma non il segmento 1
set_to_remove2 = segment0_set.difference(segment2_set)  # Insieme contenente i file audio che
# presentano il segmento 0 e il segmento 1 ma non il segmento 2

# Eliminazione dei file audio che presentano solo i segmenti 0 e 1. In altre parole, si eliminano
# i file audio che non presentano almeno tre segmenti
for file in audio_list:
    file = file[:-14]
    if file in set_to_remove1:
        if os.path.exists(audio_dir + "\\" + file + "__segment0.wav"):
            file = file + "__segment0.wav"
            os.remove(audio_dir + "\\" + file)
    if file in set_to_remove2:
        if os.path.exists(audio_dir + "\\" + file + "__segment0.wav"):
            file = file + "__segment0.wav"
            os.remove(audio_dir + "\\" + file)
        if os.path.exists(audio_dir + "\\" + file + "__segment1.wav"):
            file = file + "__segment1.wav"
            os.remove(audio_dir + "\\" + file)

# Eliminazione di tutti i segmenti 3, 4 e 5
for file in audio_list:
    file = file[:-14]
    if os.path.exists(audio_dir + "\\" + file + "__segment3.wav"):
        file = file + "__segment3.wav"
        os.remove(audio_dir + "\\" + file)
    if os.path.exists(audio_dir + "\\" + file + "__segment4.wav"):
        file = file + "__segment4.wav"
        os.remove(audio_dir + "\\" + file)
    if os.path.exists(audio_dir + "\\" + file + "__segment5.wav"):
        file = file + "__segment5.wav"
        os.remove(audio_dir + "\\" + file)

# Unione dei segmenti 0, 1 e 2 in un unico file audio da tre minuti
for i in (range(int(len(audio_list)/3))):
    segment0 = AudioSegment.from_wav(audio_dir + "\\" + audio_list[i * 3])
    segment1 = AudioSegment.from_wav(audio_dir + "\\" + audio_list[i * 3 + 1])
    segment2 = AudioSegment.from_wav(audio_dir + "\\" + audio_list[i * 3 + 2])
    merged_file_name = audio_list[i * 3][:-14] + ".wav"
    merged = segment0 + segment1 + segment2
    merged.export(merged_audio_dir + "\\" + merged_file_name, format="wav")

merged_audio_list = os.listdir(merged_audio_dir)
spectrogram_list = os.listdir(spectrograms_dir)


# Creazione degli spettrogrammi relativi a ogni file audio
def spectrograms(i):
    # Ogni thread itera su un proprio range di file audio
    thread_range = range(i * (len(merged_audio_list)/THREAD_NO),
                         (i+1) * (len(merged_audio_list)/THREAD_NO))

    for item in thread_range:
        audio_file = merged_audio_list[item]
        if (audio_file[:-4] + "_spect.png") in spectrogram_list:
            continue  # Non aggiungiamo l'immagine se essa è già presente nella cartella
        else:
            # Caricamento del file audio
            y, sr = librosa.load(merged_audio_dir + "\\" + audio_file, sr=None)
            # Realizzazione dello spettrogramma
            spect = librosa.amplitude_to_db(np.abs(librosa.stft(y)), ref=np.max)
            # Visualizzazione dello spettrogramma
            librosa.display.specshow(spect, sr=sr, x_axis='time', y_axis='log')
            # Eliminazione degli assi dall'immagine prodotta
            plt.axis("off")
            # Eliminazione del padding bianco attorno lo spettrogramma
            plt.subplots_adjust(left=0, right=1, top=1, bottom=0)
            # Salvataggio dello spettrogramma nella cartella
            plt.savefig(spectrograms_dir + "\\" + audio_file[:-4] + "_spect.png")
            # Le variabili non sono più necessarie e vanno rimosse dalla memoria
            del y, sr, spect
            # Richiamo manuale del Garbage Collector per liberare la memoria occupata
            gc.collect()


# Chiamata alla funzione spectrograms su cinque thread diversi
if __name__ == '__main__':  # Si assicura l'esecuzione del blocco solo sul processo principale
    with multiprocessing.Pool(THREAD_NO) as p:
        p.map(spectrograms, [0, 1, 2, 3, 4])
