package com.randomher0;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

public class RootSettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }
}