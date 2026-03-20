package com.datamarkets.app.model;

import com.google.gson.annotations.SerializedName;

public class RespuestaAlphaVantage {

    // El nombre del campo tiene un espacio, hay que ponerlo exactamente igual
    @SerializedName("Global Quote")
    private CotizacionAlphaVantage cotizacion;

    // Constructor vacío necesario para Gson
    public RespuestaAlphaVantage() {}

    public CotizacionAlphaVantage getCotizacion() { return cotizacion; }
}