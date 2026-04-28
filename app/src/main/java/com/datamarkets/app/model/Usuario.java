package com.datamarkets.app.model;

import com.google.gson.annotations.SerializedName;

public class Usuario {

    @SerializedName("id_usuario")
    private int id;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("email")
    private String email;

    // El token de sesión que devuelve el backend PHP tras el login
    @SerializedName("token")
    private String token;

    // Constructor vacío necesario para Gson
    public Usuario() {}

    // Getters
    public int getId()        { return id; }
    public String getNombre() { return nombre; }
    public String getEmail()  { return email; }
    public String getToken()  { return token; }
}
