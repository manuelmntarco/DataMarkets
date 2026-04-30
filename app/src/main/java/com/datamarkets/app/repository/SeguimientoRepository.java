package com.datamarkets.app.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.datamarkets.app.model.Activo;
import com.datamarkets.app.network.ApiClient;
import com.datamarkets.app.network.SeguimientoApi;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SeguimientoRepository {

    private static final String TAG = "SeguimientoRepo";
    private SeguimientoApi api;
    private Context context;

    // Constructor: recibe el contexto para poder leer SharedPreferences
    public SeguimientoRepository(Context context) {
        this.context = context;
        this.api = ApiClient.getSeguimientoApi();
    }

    // getToken() lee el token de sesión guardado en el login. Usa la clave de la parte de login session_token
    // y le añade el prefijo necesario "Bearer ":
    private String getToken() {
        SharedPreferences prefs = context.getSharedPreferences(
                "datamarkets_prefs", Context.MODE_PRIVATE);
        String token = prefs.getString("session_token", "");
        return "Bearer " + token;
    }

    // ── Interfaz para devolver resultados al ViewModel ──────
    public interface OnFavoritosListener {
        void onExito(List<Activo> favoritos);
        void onError(String mensaje);
    }

    public interface OnOperacionListener {
        void onExito(String mensaje);
        void onError(String mensaje);
    }

    // ── Obtener favoritos del usuario ───────────────────────
    public void obtenerFavoritos(OnFavoritosListener listener) {
        api.obtenerFavoritos(getToken()).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call,
                                   Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Activo> lista = parsearFavoritos(response.body());
                    listener.onExito(lista);
                } else {
                    Log.e(TAG, "Error obteniendo favoritos: " + response.code());
                    listener.onError("Error al obtener favoritos");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "Fallo de red: " + t.getMessage());
                listener.onError("Error de conexión con el servidor");
            }
        });
    }

    // ── Añadir favorito ─────────────────────────────────────
    public void anyadirFavorito(String idExterno,
                                OnOperacionListener listener) {
        JsonObject body = new JsonObject();
        body.addProperty("id_externo", idExterno);

        api.anyadirFavorito(getToken(), body).enqueue(
                new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call,
                                           Response<JsonObject> response) {
                        if (response.isSuccessful()) {
                            listener.onExito("Favorito añadido");
                        } else if (response.code() == 409) {
                            listener.onError("Este activo ya está en favoritos");
                        } else {
                            listener.onError("Error al añadir favorito");
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        listener.onError("Error de conexión con el servidor");
                    }
                });
    }

    // ── Eliminar favorito ───────────────────────────────────
    public void eliminarFavorito(String idExterno,
                                 OnOperacionListener listener) {
        api.eliminarFavorito(getToken(), idExterno).enqueue(
                new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call,
                                           Response<JsonObject> response) {
                        if (response.isSuccessful()) {
                            listener.onExito("Favorito eliminado");
                        } else if (response.code() == 404) {
                            listener.onError("Este activo no estaba en favoritos");
                        } else {
                            listener.onError("Error al eliminar favorito");
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        listener.onError("Error de conexión con el servidor");
                    }
                });
    }

    // ── Convierte el JSON del backend en lista de Activos ───
    private List<Activo> parsearFavoritos(JsonObject json) {
        List<Activo> lista = new ArrayList<>();

        if (!json.has("items")) return lista;

        JsonArray items = json.getAsJsonArray("items");
        for (JsonElement element : items) {
            JsonObject obj = element.getAsJsonObject();

            Activo activo = new Activo();
            activo.setId(getStringSeguro(obj, "id_externo"));
            activo.setSimbolo(getStringSeguro(obj, "simbolo"));
            activo.setNombre(getStringSeguro(obj, "nombre"));

            // Precio actual (puede venir como número o como null)
            if (obj.has("precio_actual") && !obj.get("precio_actual").isJsonNull()) {
                activo.setPrecioActual(obj.get("precio_actual").getAsDouble());
            }

            // Variación porcentual de las últimas 24h
            if (obj.has("variacion_pct_24h") && !obj.get("variacion_pct_24h").isJsonNull()) {
                activo.setVariacion24h(obj.get("variacion_pct_24h").getAsDouble());
            }

            // Cambio absoluto de las últimas 24h
            if (obj.has("cambio_24h") && !obj.get("cambio_24h").isJsonNull()) {
                activo.setCambio24h(obj.get("cambio_24h").getAsDouble());
            }

            // Máximo y mínimo de las últimas 24h (útiles para el detalle)
            if (obj.has("max_24h") && !obj.get("max_24h").isJsonNull()) {
                activo.setMaximo24h(obj.get("max_24h").getAsDouble());
            }
            if (obj.has("min_24h") && !obj.get("min_24h").isJsonNull()) {
                activo.setMinimo24h(obj.get("min_24h").getAsDouble());
            }

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