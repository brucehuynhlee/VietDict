package com.example.mydictionary;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by huynhpro on 4/30/2016.
 */
public class SettingActivity extends PreferenceActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_setting);
    }
}
