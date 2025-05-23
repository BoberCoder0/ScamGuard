package com.example.testapp2.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;

import java.util.Locale;

public class LocaleHelper {

    private static final String PREFERENCES_NAME = "language_pref";
    private static final String KEY_LANGUAGE = "selected_language";
    private static final String DEFAULT_LANGUAGE = "ru";

    public static void setLocale(Context context, String languageCode) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_LANGUAGE, languageCode);
        editor.apply();

        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    public static void loadLocale(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        String languageCode = preferences.getString(KEY_LANGUAGE, DEFAULT_LANGUAGE);
        setLocale(context, languageCode);
    }

    public static String getCurrentLanguage(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return preferences.getString(KEY_LANGUAGE, DEFAULT_LANGUAGE);
    }
}