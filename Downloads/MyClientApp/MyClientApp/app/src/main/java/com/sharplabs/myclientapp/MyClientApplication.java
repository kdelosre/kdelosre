package com.sharplabs.myclientapp;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by syamamura on 10/10/2016.
 */
public class MyClientApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        setDefaultPreferences();
    }

    private void setDefaultPreferences() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        sp.edit().putString(Prefs.SERVER_URL, Prefs.SERVER_URL_DEFAULT).commit();
    }
}
