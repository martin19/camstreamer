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
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.rtsp.RtspMediaSource;
import androidx.media3.ui.PlayerView;
import androidx.preference.PreferenceManager;

import com.randomher0.databinding.FragmentCameraBinding;

public class CameraFragment extends Fragment {

    private FragmentCameraBinding binding;

    private ConnectivityManager connectivityManager;

    private ConnectivityManager.NetworkCallback networkCallback;

    private ExoPlayer exoPlayer;

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
        //displayLiveStream();

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
            exoPlayer = new ExoPlayer.Builder(context).build();
            uri = Uri.parse("android.resource://"+getActivity().getPackageName()+"/"+R.raw.globe);
        } else {
            exoPlayer = new ExoPlayer.Builder(context)
                .setMediaSourceFactory(new RtspMediaSource.Factory()
                        .setDebugLoggingEnabled(true)
                        .setSocketFactory(network.getSocketFactory())
                        .setTimeoutMs(5000))
                .build();
            uri = Uri.parse("rtsp://192.168.42.1:554/live");
        }

        playerView.setPlayer(exoPlayer);
        exoPlayer.setMediaItem(MediaItem.fromUri(uri));
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

        connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        networkCallback = new ConnectivityManager.NetworkCallback() {

            @Override
            public void onAvailable(@NonNull Network network) {
                super.onAvailable(network);
                Toast.makeText(context, "network is available", Toast.LENGTH_LONG).show();

                FragmentActivity activity = getActivity();
                if(activity == null) return;
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
        connectivityManager.requestNetwork(request, networkCallback, 15000);

        // Release the request when done.
        //connectivityManager.unregisterNetworkCallback(networkCallback);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}