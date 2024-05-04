package com.randomher0;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

        //ssh private key file
        Preference sshPrivateKeyFile = (Preference) findPreference("ssh_private_key_file");
        sshPrivateKeyFile.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent()
                        .setType("*/*")
                        .setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select a file (e.g. ~/.ssh/id_rsa)"), 123);
                return true;
            }
        });

        //ssh public key file
        Preference sshPublicKeyFile = (Preference) findPreference("ssh_public_key_file");
        sshPublicKeyFile.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent()
                        .setType("*/*")
                        .setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select a file (e.g. ~/.ssh/id_rsa.pub)"), 124);
                return true;
            }
        });

        //demo video file
        Preference demoVideoFile = (Preference) findPreference("demo_video_file");
        demoVideoFile.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent()
                        .setType("*/*")
                        .setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select a video file"), 125);
                return true;
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 123 && resultCode == RESULT_OK) {
            Uri selectedfile = Objects.requireNonNull(data).getData();
            EditTextPreference preference = findPreference("ssh_private_key_file");
            if(preference != null && selectedfile != null) {
                preference.setText(selectedfile.toString());
            }
        }

        if(requestCode == 124 && resultCode == RESULT_OK) {
            Uri selectedfile = Objects.requireNonNull(data).getData();
            EditTextPreference preference = findPreference("ssh_public_key_file");
            if(preference != null && selectedfile != null) {
                preference.setText(selectedfile.toString());
            }
        }

        if(requestCode == 125 && resultCode == RESULT_OK) {
            Uri selectedfile = Objects.requireNonNull(data).getData();
            EditTextPreference preference = findPreference("demo_video_file");
            if(preference != null && selectedfile != null) {
                preference.setText(selectedfile.toString());
            }
        }
    }
}