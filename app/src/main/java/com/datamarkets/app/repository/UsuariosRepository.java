package com.datamarkets.app.repository;

import android.os.Handler;
import android.os.Looper;

import com.datamarkets.app.model.Usuario;

import java.lang.reflect.Field;

public class UsuariosRepository {

    private static final String MOCK_EMAIL = "demo@datamarkets.app";
    private static final String MOCK_PASSWORD = "Test1234!";
    private static final long DELAY_MS = 800;

    public void login(String email, String password, UsuarioCallback callback) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (MOCK_EMAIL.equalsIgnoreCase(email) && MOCK_PASSWORD.equals(password)) {
                callback.onExito(crearUsuarioMock(1, "Usuario Demo", email));
            } else {
                callback.onError("Credenciales incorrectas");
            }
            // TODO: sustituir mock por llamada real a Retrofit
        }, DELAY_MS);
    }

    public void registrar(String nombre, String email, String password,
                          UsuarioCallback callback) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (MOCK_EMAIL.equalsIgnoreCase(email)) {
                callback.onError("Ya existe una cuenta con ese email");
            } else {
                callback.onExito(crearUsuarioMock(99, nombre, email));
            }
            // TODO: sustituir mock por llamada real a Retrofit
        }, DELAY_MS);
    }

    private Usuario crearUsuarioMock(int id, String nombre, String email) {
        Usuario usuario = new Usuario();
        try {
            Field campoId = Usuario.class.getDeclaredField("id");
            campoId.setAccessible(true);
            campoId.setInt(usuario, id);

            Field campoNombre = Usuario.class.getDeclaredField("nombre");
            campoNombre.setAccessible(true);
            campoNombre.set(usuario, nombre);

            Field campoEmail = Usuario.class.getDeclaredField("email");
            campoEmail.setAccessible(true);
            campoEmail.set(usuario, email);

            Field campoToken = Usuario.class.getDeclaredField("token");
            campoToken.setAccessible(true);
            campoToken.set(usuario, "mock_token_" + System.currentTimeMillis());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return usuario;
    }
}