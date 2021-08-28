C:\data\apps\ffmpeg\bin\ffmpeg -i %1 -ac 1 -ar 16000 -acodec pcm_s16le %1.WAV
del %1
move "%1.WAV" "%1"