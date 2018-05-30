package com.rosebay.odds.util;

import android.content.SharedPreferences;

public class SharedPreferencesClient {

    private SharedPreferences sharedPreferences;

    public SharedPreferencesClient(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public void saveUsername(String key, String data) {
        sharedPreferences.edit().putString(key, data).apply();
    }

    public String getUsername(String key) {
        return sharedPreferences.getString(key, null);
    }

}
