package com.osu.unitrade.fragment;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;
import com.osu.unitrade.R;

public class NewSettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }
}