#!/bin/bash
ffmpeg -i $1 -ac 1 -ar 16000 -acodec pcm_s16le $1.WAV
rm -Rf $1
mv $1.WAV $1