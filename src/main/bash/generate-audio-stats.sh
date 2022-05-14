#!/bin/bash
ffprobe -v error -f lavfi -i "amovie=${1},asetnsamples=44100,astats=metadata=1:reset=1" -show_entries frame=pkt_pts_time:frame_tags=lavfi.astats.Overall.Peak_level -of json > $2/peak.json
ffprobe -v error -f lavfi -i "amovie=${1},asetnsamples=44100,astats=metadata=1:reset=1" -show_entries frame=pkt_pts_time:frame_tags=lavfi.astats.Overall.RMS_level -of json > $2/rms.json