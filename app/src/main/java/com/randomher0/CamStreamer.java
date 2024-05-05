package com.randomher0;

import android.app.Application;

import com.randomher0.ui.main.core.Camera;

public class CamStreamer extends Application {
    private static CamStreamer sInstance;

    private Camera camera;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        sInstance.setCamera(new Camera());
    }

    public static CamStreamer getInstance() {
        return CamStreamer.sInstance;
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }
}
