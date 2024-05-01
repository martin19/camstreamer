package com.randomher0.ui.main.core;

import android.util.Log;

import com.jcraft.jsch.Logger;

public class JSchLogger implements Logger {
    @Override
    public boolean isEnabled(int level) {
        return true;
    }

    @Override
    public void log(int level, String message) {
        Log.println(level, "jsch", message);
    }
}
