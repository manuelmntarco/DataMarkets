package com.datamarkets.app.model;

import com.google.gson.annotations.SerializedName;

public class Noticia {

    @SerializedName("title")
    private String titulo;

    @SerializedName("description")
    private String descripcion;

    @SerializedName("source")
    private String fuente;

    @SerializedName("publishedAt")
    private String fechaPublicacion;

    @SerializedName("url")
    private String url;

    @SerializedName("urlToImage")
    private String imagenUrl;

    // Constructor vacío necesario para Gson
    public Noticia() {}

    // Getters
    public String getTitulo()           { return titulo; }
    public String getDescripcion()      { return descripcion; }
    public String getFuente()           { return fuente; }
    public String getFechaPublicacion() { return fechaPublicacion; }
    public String getUrl()              { return url; }
    public String getImagenUrl()        { return imagenUrl; }
}