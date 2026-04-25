package com.datamarkets.app.network;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface SeguimientoApi {

    // Obtiene la lista de favoritos del usuario autenticado
    // Respuesta: { "items": [ ... ] }
    @GET("seguimiento.php")
    Call<JsonObject> obtenerFavoritos(
            @Header("Authorization") String token
    );

    // Añade un activo a favoritos
    // Body: { "id_externo": "bitcoin" }
    // Respuesta: { "mensaje": "Favorito anadido", "item": { ... } }
    @POST("seguimiento.php")
    Call<JsonObject> anyadirFavorito(
            @Header("Authorization") String token,
            @Body JsonObject body
    );

    // Elimina un activo de favoritos
    // Ejemplo: seguimiento.php?id_externo=bitcoin
    // Respuesta: { "mensaje": "Favorito eliminado" }
    @DELETE("seguimiento.php")
    Call<JsonObject> eliminarFavorito(
            @Header("Authorization") String token,
            @Query("id_externo") String idExterno
    );
}