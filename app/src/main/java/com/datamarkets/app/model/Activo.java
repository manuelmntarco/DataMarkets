package com.datamarkets.app.model;

import com.google.gson.annotations.SerializedName;

public class Activo {

    @SerializedName("id")
    private String id;

    @SerializedName("symbol")
    private String simbolo;

    @SerializedName("name")
    private String nombre;

    @SerializedName("image")
    private String imagenUrl;

    @SerializedName("current_price")
    private double precioActual;

    @SerializedName("price_change_24h")
    private double cambio24h;

    @SerializedName("price_change_percentage_24h")
    private double variacion24h;

    @SerializedName("market_cap")
    private long capitalizacion;

    @SerializedName("high_24h")
    private double maximo24h;

    @SerializedName("low_24h")
    private double minimo24h;

    @SerializedName("last_updated")
    private String ultimaActualizacion;

    // Constructor vacío necesario para Gson
    public Activo() {}

    // Getters
    public String getId()                  { return id; }
    public String getSimbolo()             { return simbolo; }
    public String getNombre()              { return nombre; }
    public String getImagenUrl()           { return imagenUrl; }
    public double getPrecioActual()        { return precioActual; }
    public double getCambio24h()           { return cambio24h; }
    public double getVariacion24h()        { return variacion24h; }
    public long getCapitalizacion()        { return capitalizacion; }
    public double getMaximo24h()           { return maximo24h; }
    public double getMinimo24h()           { return minimo24h; }
    public String getUltimaActualizacion() { return ultimaActualizacion; }

    public void setId(String id)           { this.id = id; }
}