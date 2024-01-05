import gc
import os
import multiprocessing
import librosa.display
import matplotlib.pyplot as plt
import numpy as np
from pydub import AudioSegment
    
# Directory con le clip audio
audio_dir = "D:\Desktop\\fafo\sound_files" # sound_dir = "../resources/sound_files"
merged_audio_dir = "D:\Desktop\\fafo\merge" # merged_sound_dir = "../resources/merge"
spectrograms_dir = "D:\Desktop\\fafo\spectrograms"  #  spect_dir = "../resources/spectrograms"
THREAD_NO = 5
#dir = "D:\Desktop\dataset\sound_files\sound_files"

audio_list = os.listdir(audio_dir)

# Definiamo dei set per trovare i file con meno di tre segmenti
segment0_set = set()
segment1_set = set()
segment2_set = set()

for file in audio_list:
    if (file.endswith("segment0.wav")):
        file = file[:-14]
        segment0_set.add(file)
    if (file.endswith("segment1.wav")):
        file = file[:-14]
        segment1_set.add(file)
    if (file.endswith("segment2.wav")):
        file = file[:-14]
        segment2_set.add(file)

# Set di file da cancellare
set_to_remove1 = segment0_set.difference(segment1_set)
set_to_remove2 = segment0_set.difference(segment2_set)

# Loop per cancellare i segmenti 0 e 1 dove necessario
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

# Loop per cancellare tutti i segmenti 3, 4 e 5
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

# Unisce i vari segmenti in un unico file
for i in (range(int(len(audio_list)/3))):
    segment0 = AudioSegment.from_wav(audio_dir + "\\" + audio_list[i * 3])
    segment1 = AudioSegment.from_wav(audio_dir + "\\" + audio_list[i * 3 + 1])
    segment2 = AudioSegment.from_wav(audio_dir + "\\" + audio_list[i * 3 + 2])
    merged_file_name = audio_list[i * 3][:-14] + ".wav"
    merged = segment0 + segment1 + segment2
    merged.export(merged_audio_dir + "\\" + merged_file_name, format="wav")

merged_audio_list = os.listdir(merged_audio_dir)
spectrogram_list = os.listdir(spectrograms_dir)

def spectrograms(i):
    for item in range(i * (len(merged_audio_list)/THREAD_NO), (i+1) * (len(merged_audio_list)/THREAD_NO)):
        file = merged_audio_list[item]
        if (file[:-4] + "_spect.png") in spectrogram_list:
            continue
        else:
            y, sr = librosa.load(merged_audio_dir + "\\" + file, sr=None) # Caricamento del file audio
            spect = librosa.amplitude_to_db(np.abs(librosa.stft(y)), ref=np.max) # Realizzazione dello spettrogramma
            librosa.display.specshow(spect, sr=sr, x_axis='time', y_axis='log') # Visualizzazione dello spettrogramma
            plt.axis("off")  # Eliminazione degli assi dall'immagine prodotta
            plt.subplots_adjust(left=0, right=1, top=1, bottom=0)  # Eliminazione del padding bianco attorno lo spettrogramma
            plt.savefig(spectrograms_dir + "\\" + file[:-4] + "_spect.png") # Salvataggio dello spettrogramma nella cartella
            del y, sr, spect
            gc.collect()


# Si assicura l'esecuzione del seguente blocco solo dal processo principale, così
# da non essere eseguito da alcun processo figlio
if __name__ == '__main__':
    with multiprocessing.Pool(5) as p:
        p.map(spectrograms, [0,1,2,3,4])