package com.randomher0;

import android.os.Bundle;

import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class SettingsFragment extends PreferenceFragmentCompat {

    static HashMap<String, String> networkInterfaceTypes =  new HashMap();
    static {
        networkInterfaceTypes = new HashMap<>();
        networkInterfaceTypes.put("wlan", "Wlan");
        networkInterfaceTypes.put("lo", "loopback");
        networkInterfaceTypes.put("rmnet", "cellular/tether");
        networkInterfaceTypes.put("sit", "tunnel");
        networkInterfaceTypes.put("p2p", "wifi direct");
        networkInterfaceTypes.put("dummy", "loopback");
    }

    static String matchFirstOrDefault(String s, HashMap<String, String> matchMap, String defaultValue) {
        for (Map.Entry<String, String> entry : matchMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if(s.contains(key)) {
                return value;
            }
        }
        return defaultValue;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        //discover all available network interfaces
        try {
            List<String> targetNetworkDisplayNames = new ArrayList<>();
            List<String> targetNetworkNames = new ArrayList<>();

            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while(networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                String networkInterfaceType = matchFirstOrDefault(networkInterface.getName(),
                        networkInterfaceTypes, "unknown");

                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                String networkInterfaceIps = StreamSupport.stream(
                        Spliterators.spliteratorUnknownSize(inetAddresses.asIterator(), Spliterator.ORDERED),
                        false
                ).map(InetAddress::getHostAddress).collect(Collectors.joining(" , "));

                targetNetworkDisplayNames.add(String.format("%s ( type = %s, ips = %s )",
                        networkInterface.getDisplayName(),
                        networkInterfaceType,
                        networkInterfaceIps
                ));
                targetNetworkNames.add(networkInterface.getName());
            }
            ListPreference listPreference = findPreference("target_network_name");
            listPreference.setEntries(targetNetworkDisplayNames.toArray(new String[0]));
            listPreference.setEntryValues(targetNetworkNames.toArray(new String[0]));
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }
}