package com.randomher0.rtsp;

public interface RTSPStreamDataEventListener {

    void onData(byte[] data, int bytes);
}