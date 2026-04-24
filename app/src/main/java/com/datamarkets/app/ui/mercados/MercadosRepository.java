package com.datamarkets.app.ui.mercados;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.datamarkets.app.model.Activo;
import com.datamarkets.app.network.ApiClient;
import com.datamarkets.app.network.CoinGeckoApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MercadosRepository {

    // La interfaz de Retrofit para hacer las llamadas
    private final CoinGeckoApi api;

    public MercadosRepository() {
        api = ApiClient.getCoinGeckoApi();
    }

    // Devuelve un LiveData con la lista de activos
    // LiveData es un contenedor que avisa automáticamente cuando los datos cambian
    public LiveData<List<Activo>> obtenerActivos() {

        // MutableLiveData es un LiveData al que podemos cambiarle el valor
        MutableLiveData<List<Activo>> resultado = new MutableLiveData<>();

        // Llamada a CoinGecko: top 50 criptos ordenadas por capitalización, en euros
        Call<List<Activo>> llamada = api.getActivos(
                "eur",
                "",
                "market_cap_desc",
                50
        );

        // enqueue hace la llamada en segundo plano (no bloquea la pantalla)
        llamada.enqueue(new Callback<List<Activo>>() {

            // Se ejecuta cuando la API responde correctamente
            @Override
            public void onResponse(Call<List<Activo>> call, Response<List<Activo>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    resultado.setValue(response.body());
                } else {
                    resultado.setValue(null);
                }
            }

            // Se ejecuta cuando hay un error de red (sin internet, timeout, etc.)
            @Override
            public void onFailure(Call<List<Activo>> call, Throwable t) {
                resultado.setValue(null);
            }
        });

        return resultado;
    }
}