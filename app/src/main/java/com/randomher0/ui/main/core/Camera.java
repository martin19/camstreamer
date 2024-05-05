package com.randomher0.ui.main.core;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.MacAddress;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.NetworkSpecifier;
import android.net.wifi.WifiNetworkSpecifier;
import android.os.PatternMatcher;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.randomher0.CamStreamer;

import java.util.logging.Logger;

public class Camera {

    public static final java.util.logging.Logger Log = Logger.getLogger(Camera.class.getName());

    private Network network;

    public Camera() {}

    public void connect() {
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

        Context context = CamStreamer.getInstance().getApplicationContext();
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {

            @Override
            public void onAvailable(@NonNull Network network1) {
                super.onAvailable(network1);
                Toast.makeText(context, "network is available", Toast.LENGTH_LONG).show();
                network = network1;

//                FragmentActivity activity = getActivity();
//                if (activity == null) return;
//                activity.runOnUiThread(() -> {
//                    displayLiveStream(network);
//                    Toast.makeText(context, "streaming.", Toast.LENGTH_LONG).show();
//                });
            }

            @Override
            public void onUnavailable() {
                super.onUnavailable();
                Toast.makeText(context, "network is unavailable", Toast.LENGTH_LONG).show();
                network = null;
            }
        };
        connectivityManager.requestNetwork(request, networkCallback, 35000);

        // Release the request when done.
        //connectivityManager.unregisterNetworkCallback(networkCallback);
    }

    public Network getNetwork() {
        return network;
    }
}
