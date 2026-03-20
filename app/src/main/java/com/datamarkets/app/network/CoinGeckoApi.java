package com.datamarkets.app.network;

import com.datamarkets.app.model.Activo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CoinGeckoApi {

    // Corresponde a esta URL:
    // https://api.coingecko.com/api/v3/coins/markets
    //   ?vs_currency=eur
    //   &ids=bitcoin,ethereum,solana
    //   &order=market_cap_desc
    //   &per_page=50
    @GET("coins/markets")
    Call<List<Activo>> getActivos(
            @Query("vs_currency") String moneda,
            @Query("ids")         String ids,
            @Query("order")       String orden,
            @Query("per_page")    int cantidad
    );
}