package com.datamarkets.app.repository;

import android.content.Context;
import android.content.SharedPreferences;

public class GestorSesion {

    private static final String PREFS_NAME = "datamarkets_prefs";
    private static final String KEY_TOKEN = "session_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_NOMBRE = "user_nombre";
    private static final String KEY_EMAIL = "user_email";

    private final SharedPreferences prefs;

    public GestorSesion(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void guardarSesion(int id, String nombre, String email, String token) {
        prefs.edit()
                .putInt(KEY_USER_ID, id)
                .putString(KEY_NOMBRE, nombre)
                .putString(KEY_EMAIL, email)
                .putString(KEY_TOKEN, token)
                .apply();
    }

    public boolean haySesion() {
        return prefs.getString(KEY_TOKEN, null) != null;
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    public int getUserId() {
        return prefs.getInt(KEY_USER_ID, -1);
    }

    public void cerrarSesion() {
        prefs.edit().clear().apply();
    }
}