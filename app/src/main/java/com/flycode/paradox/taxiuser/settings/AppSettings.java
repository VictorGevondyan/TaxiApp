package com.flycode.paradox.taxiuser.settings;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Locale;

/**
 * Created by victor on 12/8/15.
 */
public class AppSettings {
    public static class LANGUAGES {
        public static final String EN = "en";
        public static final String HY = "hy";
        public static final String RU = "ru";
    }

    private static AppSettings sharedSettings;

    private final String PREFERENCES_NAME = "appSettingsPreferences";
    private final String IS_USER_LOGGED_IN = "isUserLoggedIn";
    private final String TOKEN = "token";
    private final String LANGUAGE = "language";

    private final SharedPreferences.Editor settingsEditor;

    private boolean isUserLoggedIn;

    private String token;
    private String language;

    public static AppSettings sharedSettings(Context context) {
        if (sharedSettings == null) {
            sharedSettings = new AppSettings(context);
        }

        return sharedSettings;
    }

    private AppSettings(Context context) {
        SharedPreferences settingsPreferences = context.getSharedPreferences(PREFERENCES_NAME, 0);
        settingsEditor = settingsPreferences.edit();

        isUserLoggedIn = settingsPreferences.getBoolean(IS_USER_LOGGED_IN, false);
        token = settingsPreferences.getString(TOKEN, "");
        language = settingsPreferences.getString(LANGUAGE, Locale.getDefault().getLanguage());
    }

    public boolean isUserLoggedIn() {
        return isUserLoggedIn;
    }

    public void setIsUserLoggedIn(boolean isUserLoggedIn) {
        this.isUserLoggedIn = isUserLoggedIn;
        settingsEditor.putBoolean(IS_USER_LOGGED_IN, isUserLoggedIn).commit();
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
        settingsEditor.putString(TOKEN, token).commit();
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
        settingsEditor.putString(LANGUAGE, language).commit();
    }
}
