package com.swayam.animalquizapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Set;

public class SharedPreferenceManager {

    public static String getString(Context context,String key,String defaultValue){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key,defaultValue);
    }

    public static Set getStrings(Context context,String key,Set defaultSet){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getStringSet(key,defaultSet);
    }
}
