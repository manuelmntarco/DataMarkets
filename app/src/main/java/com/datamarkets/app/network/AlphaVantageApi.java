package com.datamarkets.app.network;

import com.datamarkets.app.model.RespuestaAlphaVantage;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AlphaVantageApi {

    // Corresponde a esta URL:
    // https://www.alphavantage.co/query
    //   ?function=GLOBAL_QUOTE
    //   &symbol=AAPL
    //   &apikey=TU_CLAVE
    @GET("query")
    Call<RespuestaAlphaVantage> getCotizacion(
            @Query("function") String funcion,
            @Query("symbol")   String simbolo,
            @Query("apikey")   String apiKey
    );
}