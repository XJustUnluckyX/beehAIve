import gc
import os
from multiprocessing import Process
import librosa.display
import matplotlib.pyplot as plt
import numpy as np
from pydub import AudioSegment
    
# Directory con le clip audio
dir = "D:\Desktop\\fafo\sound_files"
dir2 = "D:\Desktop\\fafo\merge"
dir3 = "D:\Desktop\\fafo\spectrograms"
THREAD_NO = 5
#dir = "D:\Desktop\dataset\sound_files\sound_files"

audio_list = os.listdir(dir)

# Definiamo dei set per trovare i file con meno di tre segmenti
# segment0_set = set()
# segment1_set = set()
# segment2_set = set()

# for file in audio_list:
#     if (file.endswith("segment0.wav")):
#         file = file[:-14]
#         segment0_set.add(file)
#     if (file.endswith("segment1.wav")):
#         file = file[:-14]
#         segment1_set.add(file)
#     if (file.endswith("segment2.wav")):
#         file = file[:-14]
#         segment2_set.add(file)

# Set di file da cancellare
# set_to_remove1 = segment0_set.difference(segment1_set)
# set_to_remove2 = segment0_set.difference(segment2_set)

# Loop per cancellare i segmenti 0 e 1 dove necessario
# for file in audio_list:
#     file = file[:-14]
#     if file in set_to_remove1:
#         if os.path.exists(dir + "\\" + file + "__segment0.wav"):
#             file = file + "__segment0.wav"
#             os.remove(dir + "\\" + file)
#     if file in set_to_remove2:
#         if os.path.exists(dir + "\\" + file + "__segment0.wav"):
#             file = file + "__segment0.wav"
#             os.remove(dir + "\\" + file)
#         if os.path.exists(dir + "\\" + file + "__segment1.wav"):
#             file = file + "__segment1.wav"
#             os.remove(dir + "\\" + file)

# Loop per cancellare tutti i segmenti 3, 4 e 5
# for file in audio_list:
#     file = file[:-14]
#     if os.path.exists(dir + "\\" + file + "__segment3.wav"):
#         file = file + "__segment3.wav"
#         os.remove(dir + "\\" + file)
#     if os.path.exists(dir + "\\" + file + "__segment4.wav"):
#         file = file + "__segment4.wav"
#         os.remove(dir + "\\" + file)
#     if os.path.exists(dir + "\\" + file + "__segment5.wav"):
#         file = file + "__segment5.wav"
#         os.remove(dir + "\\" + file)

# Unisce i vari segmenti in un unico file
# for i in (range(int(len(audio_list)/3))):
#     segment0 = AudioSegment.from_wav(dir + "\\" + audio_list[i * 3])
#     segment1 = AudioSegment.from_wav(dir + "\\" + audio_list[i * 3 + 1])
#     segment2 = AudioSegment.from_wav(dir + "\\" + audio_list[i * 3 + 2])
#     merged_file_name = audio_list[i * 3][:-14] + ".wav"
#     merged = segment0 + segment1 + segment2
#     merged.export(dir2 + "\\" + merged_file_name, format="wav")


def spectrograms(i):
    file_list = os.listdir(dir2)
    file_list2 = os.listdir(dir3)
    for item in range(i * (len(file_list)/THREAD_NO), (i+1) * (len(file_list)/THREAD_NO)):
        file = file_list[item]
        if (file[:-4] + "_spect.png") in file_list2:
            continue
        else:
            y, sr = librosa.load(dir2 + "\\" + file, sr=None) # Caricamento del file audio
            spect = librosa.amplitude_to_db(np.abs(librosa.stft(y)), ref=np.max) # Realizzazione dello spettrogramma
            librosa.display.specshow(spect, sr=sr, x_axis='time', y_axis='log') # Visualizzazione dello spettrogramma
            plt.axis("off")  # Eliminazione degli assi dall'immagine prodotta
            plt.subplots_adjust(left=0, right=1, top=1, bottom=0)  # Eliminazione del padding bianco attorno lo spettrogramma
            plt.savefig(dir3 + "\\" + file[:-4] + "_spect.png") # Salvataggio dello spettrogramma nella cartella
            del y, sr, spect
            gc.collect()


if __name__ == '__main__': # Si assicura l'esecuzione del seguente blocco solo dal processo principale, così
    # da non essere eseguito da alcun processo figlio
    p1 = Process(target=spectrograms, args=[0])
    p2 = Process(target=spectrograms, args=[1])
    p3 = Process(target=spectrograms, args=[2])
    p4 = Process(target=spectrograms, args=[3])
    p5 = Process(target=spectrograms, args=[4])
    p1.start()
    p2.start()
    p3.start()
    p4.start()
    p5.start()
    p1.join()
    p2.join()
    p3.join()
    p4.join()
    p5.join()

# if __name__ == '__main__':
#     with multiprocessing.Pool(5) as p:
#         p.map(spectrograms, [0,1,2,3,4])