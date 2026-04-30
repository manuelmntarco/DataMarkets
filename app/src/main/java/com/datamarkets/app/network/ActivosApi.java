package com.datamarkets.app.network;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface ActivosApi {

    // Obtiene la lista completa de activos disponibles en el catálogo
    // Respuesta: { "items": [ ... ] }
    @GET("api/activos.php")
    Call<JsonObject> obtenerActivos(
            @Header("Authorization") String token
    );
}