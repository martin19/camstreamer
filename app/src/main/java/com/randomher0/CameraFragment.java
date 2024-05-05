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
import com.randomher0.ui.main.core.Camera;
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
                Camera camera = CamStreamer.getInstance().getCamera();
                if(camera == null) return;
                Network network = camera.getNetwork();
                if(network == null) return;
                displayLiveStream(network);
            }
        });
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}