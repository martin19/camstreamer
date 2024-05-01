package com.randomher0.ui.main.core;

import androidx.media3.exoplayer.ExoPlayer;

public class PreviewPlayer {
    private ExoPlayer exoPlayer;

    private static PreviewPlayer INSTANCE;

    private PreviewPlayer() {
    }

    public static PreviewPlayer getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new PreviewPlayer();
        }

        return INSTANCE;
    }

    public ExoPlayer getExoPlayer() {
        return exoPlayer;
    }

    public void setExoPlayer(ExoPlayer exoPlayer) {
        this.exoPlayer = exoPlayer;
    }
}
