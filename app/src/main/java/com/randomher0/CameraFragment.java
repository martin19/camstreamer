package com.randomher0;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.MacAddress;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.NetworkSpecifier;
import android.net.Uri;
import android.net.wifi.WifiNetworkSpecifier;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.common.util.Log;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.DefaultLoadControl;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.rtsp.RtspMediaSource;
import androidx.media3.exoplayer.util.EventLogger;
import androidx.media3.ui.PlayerView;
import androidx.preference.PreferenceManager;

import com.randomher0.databinding.FragmentCameraBinding;
import com.randomher0.ui.main.core.PreviewPlayer;

import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CameraFragment extends Fragment {

    public static final Logger Log = Logger.getLogger(CameraFragment.class.getName());

    private FragmentCameraBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentCameraBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(PreviewPlayer.getInstance().getExoPlayer() != null) {
            PlayerView playerView = getActivity().findViewById(R.id.player_view);
            playerView.setPlayer(PreviewPlayer.getInstance().getExoPlayer());
        }

        binding.buttonConnectCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectWifi();
            }
        });
    }

    public void connectWifi() {
        connectCam();
        //testPreferences();
    }

    private void displayDemoVideo() {
//TODO: this code worked to play a demo video in resources folder.
//
//        setContentView(R.layout.activity_camera);
//
//        VideoView videoView = getActivity().findViewById(R.id.idVideoView);
//
//        MediaController mediaController = new MediaController(getActivity());
//
//        mediaController.setAnchorView(videoView);
//
//        //Uri uri = Uri.parse("android.resource://"+getActivity().getPackageName()+"/"+R.raw.globe);
//
//        videoView.setMediaController(mediaController);
//
//        videoView.setVideoURI(uri);
//
//        videoView.requestFocus();
//        // on below line we are calling start  method to start our video view.
//        videoView.start();
    }

    @OptIn(markerClass = UnstableApi.class) private void displayLiveStream(Network network) {
        Context context = getActivity().getApplicationContext();

        PlayerView playerView = getActivity().findViewById(R.id.player_view);

        final boolean localTest = false;
        Uri uri;

        if(localTest) {
            PreviewPlayer.getInstance().setExoPlayer(new ExoPlayer.Builder(context).build());
            uri = Uri.parse("android.resource://"+getActivity().getPackageName()+"/"+R.raw.globe);
        } else {
            PreviewPlayer.getInstance().setExoPlayer(new ExoPlayer.Builder(context)
                .setMediaSourceFactory(new RtspMediaSource.Factory()
                        .setDebugLoggingEnabled(true)
                        .setSocketFactory(network.getSocketFactory())
                        .setTimeoutMs(5000)
                )
                .setLoadControl(new DefaultLoadControl.Builder()
                        .setBufferDurationsMs(100, 100, 100, 100)
                        .build()
                )
                .build());
            uri = Uri.parse("rtsp://192.168.42.1:554/live");
        }

        ExoPlayer exoPlayer = PreviewPlayer.getInstance().getExoPlayer();

        playerView.setUseController(true);
        playerView.requestFocus();
        playerView.setPlayer(exoPlayer);
        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                Log.log(Level.INFO, "playback state "+String.valueOf(playbackState));
            }

            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                Log.log(Level.INFO, "isPlaying changed");
            }

            @Override
            public void onSurfaceSizeChanged(int width, int height) {
                Log.log(Level.INFO, String.format(Locale.ENGLISH,"surfaceSizeChanged %d/%d",width, height));
            }
        });
        exoPlayer.setMediaItem(MediaItem.fromUri(uri));
        exoPlayer.addAnalyticsListener(new EventLogger());
        exoPlayer.prepare();
    }

    private void testPreferences() {
        Context context = getActivity().getApplicationContext();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String wifiSsid = sharedPreferences.getString("wifi_ssid", "");
        String wifiPassword = sharedPreferences.getString("wifi_password", "");
        Toast.makeText(context, String.format("Credentials %s/%s", wifiSsid, wifiPassword), Toast.LENGTH_LONG)
                .show();
    }

    public void connectCam() {
        Context context = getActivity().getApplicationContext();

        final NetworkSpecifier specifier =
                new WifiNetworkSpecifier.Builder()
                        .setSsidPattern(new PatternMatcher("Action one", PatternMatcher.PATTERN_PREFIX))
                        .setBssid(MacAddress.fromString("08:E9:F6:16:E1:48"))
                        .setWpa2Passphrase("12345678")
                        .build();

        final NetworkRequest request =
                new NetworkRequest.Builder()
                        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                        .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                        .setNetworkSpecifier(specifier)
                        .build();

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {

            @Override
            public void onAvailable(@NonNull Network network) {
                super.onAvailable(network);
                Toast.makeText(context, "network is available", Toast.LENGTH_LONG).show();

                FragmentActivity activity = getActivity();
                if (activity == null) return;
                activity.runOnUiThread(() -> {
                    displayLiveStream(network);
                    Toast.makeText(context, "streaming.", Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onUnavailable() {
                super.onUnavailable();
                Toast.makeText(context, "network is unavailable", Toast.LENGTH_LONG).show();
            }
        };
        connectivityManager.requestNetwork(request, networkCallback, 35000);

        // Release the request when done.
        //connectivityManager.unregisterNetworkCallback(networkCallback);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}