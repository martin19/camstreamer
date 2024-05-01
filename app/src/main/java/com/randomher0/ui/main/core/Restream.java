package com.randomher0.ui.main.core;
import static android.content.ContentValues.TAG;

import android.util.Log;
import android.util.Pair;

import com.arthenica.ffmpegkit.FFmpegKit;
import com.arthenica.ffmpegkit.FFmpegSession;
import com.arthenica.ffmpegkit.ReturnCode;

import com.jcraft.jsch.*;

import java.util.List;

public class Restream {

    private String publicKey;
    private List<Pair<Integer, Integer>> ports;

    public Restream(String srcUrl, String dstUrl, RestreamOptions options) {
    }

    private void initForwarding() {
        //need to redirect a few ports to local machine as they are not public accessible from outside
        //ssh -L 3390:127.0.0.1:3389 -L 8554:127.0.0.1:8554 [username]@[obshub]

        JSch.setLogger(new JSchLogger());
        JSch jsch = new JSch();
        try {
//            final Session session = jsch.getSession("serveo.net");
//            session.setHostKeyRepository(new NoSecurityRepo());
//            session.connect();
//
//            // Establish a shell channel to receive messages from serveo
//            ChannelShell cc = (ChannelShell)session.openChannel("shell");
//            cc.setPty(false);
//            cc.setInputStream(System.in);
//            cc.setOutputStream(System.out);
//            cc.setExtOutputStream(System.err);
//            cc.connect();
//
//            // Now open a remote forward
//            session.setPortForwardingR(null, 80, "localhost", 3000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() {
        FFmpegKit fFmpegkit;
        //ffmpeg -i /dev/video0 -c:v libx264 -f rtsp rtsp://127.0.0.1:8554/tuxedo-front-1
        FFmpegSession session = FFmpegKit.execute("-i file1.mp4 -c:v mpeg4 file2.mp4");
        if (ReturnCode.isSuccess(session.getReturnCode())) {
            // SUCCESS
        } else if (ReturnCode.isCancel(session.getReturnCode())) {
            // CANCEL
        } else {
            // FAILURE
            Log.d(TAG, String.format("Command failed with state %s and rc %s.%s", session.getState(), session.getReturnCode(), session.getFailStackTrace()));
        }
    }

    public void stop() {

    }
}


