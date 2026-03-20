package com.datamarkets.app.model;

import com.google.gson.annotations.SerializedName;

public class CotizacionAlphaVantage {

    // Los nombres incluyen los números del JSON tal cual
    @SerializedName("01. symbol")
    private String simbolo;

    @SerializedName("02. open")
    private String apertura;

    @SerializedName("03. high")
    private String maximo;

    @SerializedName("04. low")
    private String minimo;

    @SerializedName("05. price")
    private String precio;

    @SerializedName("06. volume")
    private String volumen;

    @SerializedName("07. latest trading day")
    private String ultimoDiaNegociacion;

    @SerializedName("08. previous close")
    private String cierrePrevio;

    @SerializedName("09. change")
    private String cambio;

    @SerializedName("10. change percent")
    private String variacion;

    // Constructor vacío necesario para Gson
    public CotizacionAlphaVantage() {}

    // Getters
    public String getSimbolo()              { return simbolo; }
    public String getApertura()             { return apertura; }
    public String getMaximo()               { return maximo; }
    public String getMinimo()               { return minimo; }
    public String getPrecio()               { return precio; }
    public String getVolumen()              { return volumen; }
    public String getUltimoDiaNegociacion() { return ultimoDiaNegociacion; }
    public String getCierrePrevio()         { return cierrePrevio; }
    public String getCambio()               { return cambio; }
    public String getVariacion()            { return variacion; }
}