REM C:\gstreamer\1.0\msvc_x86_64\bin\gst-launch-1.0 -v ksvideosrc do-stats=TRUE ! avenc_mpeg4 ! rtpmp4vpay config-interval=1 ! udpsink host=127.0.0.1 port=5000
REM C:\ffmpeg\bin\ffmpeg -re -f vfwcap -s:v 640x480 -r 25 -i 0 -c:v libx264 -b:v 8000K -an -f rtsp rtsp://127.0.0.1:5000 -sdp_file video.sdp
"C:\Program Files\VideoLAN\VLC\vlc.exe" --network-caching=50 rtsp://localhost:18554/live.stream