package com.datamarkets.app.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    // URLs base de cada API
    private static final String BASE_URL_COINGECKO    =
            "https://api.coingecko.com/api/v3/";
    private static final String BASE_URL_ALPHAVANTAGE =
            "https://www.alphavantage.co/";

    // Instancias únicas (patrón Singleton)
    private static Retrofit retrofitCoinGecko    = null;
    private static Retrofit retrofitAlphaVantage = null;

    // Cliente HTTP compartido con logs para depuración
    private static OkHttpClient getHttpClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        return new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();
    }

    // Instancia de Retrofit para CoinGecko
    private static Retrofit getClientCoinGecko() {
        if (retrofitCoinGecko == null) {
            retrofitCoinGecko = new Retrofit.Builder()
                    .baseUrl(BASE_URL_COINGECKO)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(getHttpClient())
                    .build();
        }
        return retrofitCoinGecko;
    }

    // Instancia de Retrofit para Alpha Vantage
    private static Retrofit getClientAlphaVantage() {
        if (retrofitAlphaVantage == null) {
            retrofitAlphaVantage = new Retrofit.Builder()
                    .baseUrl(BASE_URL_ALPHAVANTAGE)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(getHttpClient())
                    .build();
        }
        return retrofitAlphaVantage;
    }

    // Métodos públicos que usa el resto del código
    public static CoinGeckoApi getCoinGeckoApi() {
        return getClientCoinGecko().create(CoinGeckoApi.class);
    }

    public static AlphaVantageApi getAlphaVantageApi() {
        return getClientAlphaVantage().create(AlphaVantageApi.class);
    }
}