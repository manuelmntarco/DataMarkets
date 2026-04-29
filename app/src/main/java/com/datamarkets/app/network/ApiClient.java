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

    // Backend propio en XAMPP
    // 10.0.2.2 es la IP que el emulador usa para llegar al localhost del PC
    private static final String BASE_URL_BACKEND =
            "http://10.0.2.2/DataMarkets/backend/public/";

    // Instancias únicas (patrón Singleton)
    private static Retrofit retrofitCoinGecko    = null;
    private static Retrofit retrofitAlphaVantage = null;
    private static Retrofit retrofitBackend      = null;

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

    // Instancia de Retrofit para el backend propio en XAMPP
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

    // ── Métodos públicos que usa el resto del código ──────

    public static CoinGeckoApi getCoinGeckoApi() {
        return getClientCoinGecko().create(CoinGeckoApi.class);
    }

    public static AlphaVantageApi getAlphaVantageApi() {
        return getClientAlphaVantage().create(AlphaVantageApi.class);
    }

    public static UsuariosApi getUsuariosApi() {
        return getClientBackend().create(UsuariosApi.class);
    }

    public static SeguimientoApi getSeguimientoApi() {
        return getClientBackend().create(SeguimientoApi.class);
    }
}