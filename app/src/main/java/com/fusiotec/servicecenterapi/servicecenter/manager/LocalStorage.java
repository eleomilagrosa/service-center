package com.fusiotec.servicecenterapi.servicecenter.manager;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * This is Manager for the Shared Preference
 * @author eleom
 * @author Eleojasmil Milagrosa
 * @version %I% %G%
 * @since 1.0
 */

public class LocalStorage {

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public static final String ACCESS_TOKEN = "access_token";
    public static final String TOKEN_TYPE = "token_type";
    public static final String TIME_DIFFERENCE_IN_SECONDS = "time_difference_in_seconds";

    public LocalStorage(Context context){
        preferences = android.preference.PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
    }

    public LocalStorage(Context context, SharedPreferences.OnSharedPreferenceChangeListener listener){
        preferences = android.preference.PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
        preferences.registerOnSharedPreferenceChangeListener(listener);
    }
    public SharedPreferences getPreference(){
        return preferences;
    }

    public String getString(String key,String default_value){
        return this.preferences.getString(key,default_value);
    }
    public int getInt(String key,int default_value){
        return this.preferences.getInt(key,default_value);
    }
    public boolean getBoolean(String key,boolean default_value){
        return this.preferences.getBoolean(key,default_value);
    }

    public void saveIntegerOnLocalStorage(String key, int value){
        editor.putInt(key, value);
        editor.apply();
    }
    public void saveBooleanOnLocalStorage(String key, boolean value){
        editor.putBoolean(key, value);
        editor.apply();
    }
    public void saveStringOnLocalStorage(String key, String value){
        editor.putString(key, value);
        editor.apply();
    }
    public void saveLongOnLocalStorage(String key, long value){
        editor.putLong(key, value);
        editor.apply();
    }
    public void logoutUser(){
        editor.clear();
        editor.commit();
    }
}
