C:\data\apps\ffmpeg\bin\ffmpeg -i %1 -filter_complex "[0:a]aformat=channel_layouts=mono, showwavespic=draw=full:filter=peak:s=128x128,showinfo[fg]; color=s=128x128:color=#ffffff, drawgrid=width=iw/10:height=ih/5:color=#3366ff@0.1[bg]; [bg][fg]overlay=format=auto,showinfo,drawbox=x=(iw-w)/2:y=(ih-h)/2:w=iw:h=1:color=#3366ff" -frames:v 1 %1.png