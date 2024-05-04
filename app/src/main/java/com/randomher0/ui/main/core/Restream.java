package com.randomher0.ui.main.core;
import static android.content.ContentValues.TAG;

import static com.arthenica.ffmpegkit.Packages.getPackageName;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.StrictMode;
import android.util.Log;
import android.util.Pair;

import androidx.preference.Preference;
import androidx.preference.PreferenceManager;

import com.arthenica.ffmpegkit.FFmpegKit;
import com.arthenica.ffmpegkit.FFmpegSession;
import com.arthenica.ffmpegkit.ReturnCode;

import com.jcraft.jsch.*;
import com.randomher0.MainActivity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Restream {

    public static final java.util.logging.Logger Log = Logger.getLogger(Restream.class.getName());

    Context context;

    public Restream(Context context, RestreamOptions options) {
        this.context = context;
    }

    private String readTextfileFromUri(Uri uri) {
        String content;
        try {
            InputStream in = context.getContentResolver().openInputStream(uri);

            BufferedReader r = new BufferedReader(new InputStreamReader(in));
            StringBuilder total = new StringBuilder();
            for (String line; (line = r.readLine()) != null; ) {
                total.append(line).append('\n');
            }

            content = total.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return content;
    }

    public void initForwarding() {
        //need to redirect a few ports to local machine as they are not public accessible from outside
        //ssh -L 3390:127.0.0.1:3389 -L 8554:127.0.0.1:8554 [username]@[obshub]

        JSch.setLogger(new JSchLogger());
        JSch jsch = new JSch();
        try {
            //TODO: quick hack to run network on main thread, remove later! run as async task.
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            String sshPrivateKeyFile = sharedPreferences.getString("ssh_private_key_file", "");
            String sshPublicKeyFile = sharedPreferences.getString("ssh_public_key_file", "");
            String sshUser = sharedPreferences.getString("ssh_user", "");
            String sshHost = sharedPreferences.getString("ssh_host", "");
            String sshPortForwarding = sharedPreferences.getString("ssh_port_forwarding", "");

            Uri publicKeyUri = Uri.parse(sshPublicKeyFile);
            String publicKey = readTextfileFromUri(publicKeyUri);
            Uri privateKeyUri = Uri.parse(sshPrivateKeyFile);
            String privateKey = readTextfileFromUri(privateKeyUri);
            String passphrase = "";

            jsch.addIdentity("martin", privateKey.getBytes(StandardCharsets.UTF_8),
                    publicKey.getBytes(StandardCharsets.UTF_8),
                    passphrase.getBytes(StandardCharsets.UTF_8));
            final Session session = jsch.getSession(sshUser, sshHost);
            session.setConfig("StrictHostKeyChecking", "no");
//            session.setHostKeyRepository(new BlindHostKeyRepository());
            session.connect();

            // create port forwardings
            if(!sshPortForwarding.trim().isEmpty()) {
                String[] sshPortForwardings = sshPortForwarding.trim().split(",");
                for (String portForwarding : sshPortForwardings) {
                    if(portForwarding.split(":").length != 3) continue;
                    int lport=Integer.parseInt(portForwarding.substring(0, portForwarding.indexOf(':')));
                    portForwarding=portForwarding.substring(portForwarding.indexOf(':')+1);
                    String rhost=portForwarding.substring(0, portForwarding.indexOf(':'));
                    int rport=Integer.parseInt(portForwarding.substring(portForwarding.indexOf(':')+1));
                    int assinged_port = session.setPortForwardingL(null, lport, rhost, rport, null);
                    Log.log(Level.INFO,  "localhost:"+assinged_port+" -> "+rhost+":"+rport);
                }
            }
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
//            Log.d(TAG, String.format("Command failed with state %s and rc %s.%s", session.getState(), session.getReturnCode(), session.getFailStackTrace()));
        }
    }

    public void stop() {

    }
}


