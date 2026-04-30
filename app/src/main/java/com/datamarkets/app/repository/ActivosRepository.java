package com.datamarkets.app.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.datamarkets.app.model.Activo;
import com.datamarkets.app.network.ActivosApi;
import com.datamarkets.app.network.ApiClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivosRepository {

    private static final String TAG = "ActivosRepo";
    private final ActivosApi api;
    private final Context context;

    public ActivosRepository(Context context) {
        this.context = context;
        this.api     = ApiClient.getActivosApi();
    }

    // Lee el token de sesión guardado tras el login
    private String getToken() {
        SharedPreferences prefs = context.getSharedPreferences(
                "datamarkets_prefs", Context.MODE_PRIVATE);
        String token = prefs.getString("session_token", "");
        return "Bearer " + token;
    }

    // Interfaz para devolver el resultado al ViewModel o al diálogo
    public interface OnActivosListener {
        void onExito(List<Activo> activos);
        void onError(String mensaje);
    }

    // Obtiene la lista completa de activos del catálogo
    public void obtenerActivos(OnActivosListener listener) {
        api.obtenerActivos(getToken()).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call,
                                   Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Activo> lista = parsearActivos(response.body());
                    listener.onExito(lista);
                } else {
                    Log.e(TAG, "Error obteniendo activos: " + response.code());
                    listener.onError("Error al obtener el catálogo de activos");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "Fallo de red: " + t.getMessage());
                listener.onError("Error de conexión con el servidor");
            }
        });
    }

    // Convierte el JSON del backend en lista de objetos Activo
    private List<Activo> parsearActivos(JsonObject json) {
        List<Activo> lista = new ArrayList<>();

        if (!json.has("items")) return lista;

        JsonArray items = json.getAsJsonArray("items");
        for (JsonElement element : items) {
            JsonObject obj = element.getAsJsonObject();

            Activo activo = new Activo();
            activo.setId(getStringSeguro(obj, "id_externo"));
            activo.setSimbolo(getStringSeguro(obj, "simbolo"));
            activo.setNombre(getStringSeguro(obj, "nombre"));

            lista.add(activo);
        }

        return lista;
    }

    // Lee un campo String del JSON sin que crashee si es null
    private String getStringSeguro(JsonObject obj, String campo) {
        if (obj.has(campo) && !obj.get(campo).isJsonNull()) {
            return obj.get(campo).getAsString();
        }
        return "";
    }
}