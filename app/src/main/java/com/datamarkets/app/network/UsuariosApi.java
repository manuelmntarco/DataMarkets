package com.datamarkets.app.network;

import com.datamarkets.app.model.Usuario;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UsuariosApi {

    @POST("api/auth/login.php")
    Call<Usuario> login(@Body Map<String, String> credenciales);

    @POST("api/auth/register.php")
    Call<Usuario> registrar(@Body Map<String, String> datos);
}
