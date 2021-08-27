#!/bin/bash
ffmpeg -i $1 -filter_complex \
"[0:a]aformat=channel_layouts=mono, \
 compand, \
 showwavespic=draw=full:filter=peak:s=2048x800,showinfo[fg]; \
 color=s=2048x800:color=#44582c, \
 drawgrid=width=iw/10:height=ih/5:color=#9cf42f@0.1[bg]; \
 [bg][fg]overlay=format=auto,showinfo,drawbox=x=(iw-w)/2:y=(ih-h)/2:w=iw:h=1:color=#9cf42f" \
-frames:v 1 $1.png

ffmpeg -i $1 -filter_complex "showwavespic=s=640x120,showinfo[fg];color=s=640x120:color=#ffff00[bg];[bg][fg]overlay=format=auto,showinfo" -frames:v 1 $1.jpg
