package com.datamarkets.app.repository;

import com.datamarkets.app.model.Usuario;
import com.datamarkets.app.network.ApiClient;
import com.datamarkets.app.network.UsuariosApi;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsuariosRepository {

    private final UsuariosApi api = ApiClient.getUsuariosApi();

    public void login(String email, String password, UsuarioCallback callback) {
        Map<String, String> credenciales = new HashMap<>();
        credenciales.put("email", email);
        credenciales.put("password", password);

        api.login(credenciales).enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onExito(response.body());
                } else {
                    callback.onError(extraerError(response, "Credenciales incorrectas"));
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                callback.onError("Error de conexión: " + t.getMessage());
            }
        });
    }

    public void registrar(String nombre, String email, String password,
                          UsuarioCallback callback) {
        Map<String, String> datos = new HashMap<>();
        datos.put("nombre", nombre);
        datos.put("email", email);
        datos.put("password", password);

        api.registrar(datos).enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onExito(response.body());
                } else {
                    callback.onError(extraerError(response, "No se pudo completar el registro"));
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                callback.onError("Error de conexión: " + t.getMessage());
            }
        });
    }

    // Intenta leer el campo "error" del JSON de respuesta
    private String extraerError(Response<?> response, String fallback) {
        try {
            String body = response.errorBody().string();
            JSONObject json = new JSONObject(body);
            return json.getString("error");
        } catch (Exception e) {
            return fallback;
        }
    }
}