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
import androidx.media3.datasource.DefaultDataSource;
import androidx.media3.datasource.cronet.CronetDataSource;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.rtsp.RtspMediaSource;
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory;
import androidx.media3.ui.PlayerView;
import androidx.preference.PreferenceManager;

import com.randomher0.databinding.FragmentCameraBinding;

public class CameraFragment extends Fragment {

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
        //displayLiveStream();

        binding.buttonConnectCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectWifi();
            }
        });
    }

    public void connectWifi() {
        //connectCam1();
        connectCam2();
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
//        VideoView videoView = getActivity().findViewById(R.id.idVideoView);
//        MediaController mediaController = new MediaController(getActivity());
//        mediaController.setAnchorView(videoView);
//        Uri uri = Uri.parse("rtsp://192.168.42.1:554/live");
//        videoView.setMediaController(mediaController);
//        videoView.setVideoURI(uri);
//        videoView.requestFocus();
//        videoView.start();

        Context context = getActivity().getApplicationContext();

        // Given a CronetEngine and Executor, build a CronetDataSource.Factory.
//        CronetDataSource.Factory cronetDataSourceFactory =
//                new CronetDataSource.Factory(cronetEngine, executor);
//
//        DefaultDataSource.Factory dataSourceFactory =
//                new DefaultDataSource.Factory(
//                        context,
//                        /* baseDataSourceFactory= */ cronetDataSourceFactory);

        PlayerView playerView = getActivity().findViewById(R.id.player_view);
        ExoPlayer player = new ExoPlayer.Builder(context)
                .setMediaSourceFactory(new RtspMediaSource.Factory()
                        .setTimeoutMs(1000000)
                        .setDebugLoggingEnabled(true)
                        .setSocketFactory(network.getSocketFactory())
                )
//        .setMediaSourceFactory(
//                new DefaultMediaSourceFactory(context)
//                .setDataSourceFactory(dataSourceFactory))
                .build();
        playerView.setPlayer(player);
        Uri rtspUri = Uri.parse("rtsp://192.168.42.1:554/live");
        player.setMediaItem(MediaItem.fromUri(rtspUri));
        player.prepare();
    }

    private void testPreferences() {
        Context context = getActivity().getApplicationContext();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String wifiSsid = sharedPreferences.getString("wifi_ssid", "");
        String wifiPassword = sharedPreferences.getString("wifi_password", "");
        Toast.makeText(context, String.format("Credentials %s/%s", wifiSsid, wifiPassword), Toast.LENGTH_LONG)
                .show();
    }

    public void connectCam1() {
//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//        String wifiSsid = sharedPreferences.getString("wifi_ssid", "Action one_5656_5G");
//        String wifiPassword = sharedPreferences.getString("wifi_password", "12345678");
//
//        final WifiNetworkSuggestion suggestion1 =
//                new WifiNetworkSuggestion.Builder()
//                        .setSsid(wifiSsid)
//                        .setIsAppInteractionRequired(true) // Optional (Needs location permission)
//                        .build();
//
//        final WifiNetworkSuggestion suggestion2 =
//                new WifiNetworkSuggestion.Builder()
//                        .setSsid("test2")
//                        .setIsAppInteractionRequired(true) // Optional (Needs location permission)
//                        .build();
//
//        final WifiNetworkSuggestion suggestion3 =
//                new WifiNetworkSuggestion.Builder()
//                        .setSsid("test3")
//                        .setIsAppInteractionRequired(true) // Optional (Needs location permission)
//                        .build();
//
//
//        final List<WifiNetworkSuggestion> suggestionsList = new ArrayList<>();
//        suggestionsList.add(suggestion1);
//        suggestionsList.add(suggestion2);
//        suggestionsList.add(suggestion3);
//
//        WifiManager wifiManager = (WifiManager)getSystemService(WIFI_SERVICE);
//        final int status = wifiManager.addNetworkSuggestions(suggestionsList);
//        if (status != WifiManager.STATUS_NETWORK_SUGGESTIONS_SUCCESS) {
//            //TODO:
//            // do error handling here…
//        }
//
//        // Optional (Wait for post connection broadcast to one of your suggestions)
//        final IntentFilter intentFilter = new IntentFilter(WifiManager.ACTION_WIFI_NETWORK_SUGGESTION_POST_CONNECTION);
//
//        final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                if (!intent.getAction().equals(
//                        WifiManager.ACTION_WIFI_NETWORK_SUGGESTION_POST_CONNECTION)) {
//                    return;
//                }
//                //TODO:
//                // do post connect processing here...
//            }
//        };
//        getApplicationContext().registerReceiver(broadcastReceiver, intentFilter);

//        //remember current configuration
//        //WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//
//
////        String wifiSsid = sharedPreferences.getString("wifi_ssid", "Action one_5656_5G");
////        String wifiPassword = sharedPreferences.getString("wifi_password", "12345678");
//
//        WifiConfiguration wifiConfiguration = new WifiConfiguration();
//        wifiConfiguration.SSID = String.format("\"%s\"", wifiSsid);
//        wifiConfiguration.preSharedKey = String.format("\"%s\"", wifiPassword);
//
//        int netId = wifiManager.addNetwork(wifiConfiguration);
//        wifiManager.disconnect();
//        wifiManager.enableNetwork(netId, true);
//        wifiManager.reconnect();
    }

    public void connectCam2() {
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

        final ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        final ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {

            @Override
            public void onAvailable(@NonNull Network network) {
                super.onAvailable(network);
                Toast.makeText(context, "network is available", Toast.LENGTH_LONG).show();

                FragmentActivity activity = getActivity();
                if(activity == null) return;
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        displayLiveStream(network);
                    }
                });

                Toast.makeText(context, "streaming.", Toast.LENGTH_LONG).show();
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