REM C:\gstreamer\1.0\msvc_x86_64\bin\gst-launch-1.0 -v udpsrc port=5000 ! rtpmp2tdepay ! decodebin ! autovideosink
REM C:\ffmpeg\bin\ffplay -protocol_whitelist rtp,udp -i "rtp://127.0.0.1:5000"
C:\ffmpeg\bin\ffplay -protocol_whitelist file,rtp,udp stream.sdp