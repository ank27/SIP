package com.example.ankurkhandelwal.sip;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by ankurkhandelwal on 14/12/15.
 */
public class Common extends Application {
    public static SharedPreferences prefs;
    public static SharedPreferences.Editor editor;

    public void onCreate(){
        super.onCreate();
        prefs= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor=prefs.edit();
    }
}
