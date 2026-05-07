package com.datamarkets.app.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static final String BASE_URL_COINGECKO = "https://api.coingecko.com/api/v3/";
    private static final String BASE_URL_BACKEND   = "http://10.0.2.2/DataMarkets/backend/public/";

    private static Retrofit retrofitCoinGecko = null;
    private static Retrofit retrofitBackend   = null;

    private static OkHttpClient getHttpClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        return new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();
    }

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

    private static Retrofit getClientBackend() {
        if (retrofitBackend == null) {
            retrofitBackend = new Retrofit.Builder()
                    .baseUrl(BASE_URL_BACKEND)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(getHttpClient())
                    .build();
        }
        return retrofitBackend;
    }

    public static CoinGeckoApi getCoinGeckoApi() {
        return getClientCoinGecko().create(CoinGeckoApi.class);
    }

    public static UsuariosApi getUsuariosApi() {
        return getClientBackend().create(UsuariosApi.class);
    }

    public static SeguimientoApi getSeguimientoApi() {
        return getClientBackend().create(SeguimientoApi.class);
    }

    public static ActivosApi getActivosApi() {
        return getClientBackend().create(ActivosApi.class);
    }
}