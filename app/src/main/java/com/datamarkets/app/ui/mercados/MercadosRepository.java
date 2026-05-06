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

    private final CoinGeckoApi api;

    public MercadosRepository() {
        api = ApiClient.getCoinGeckoApi();
    }

    public LiveData<List<Activo>> obtenerActivos() {

        MutableLiveData<List<Activo>> resultado = new MutableLiveData<>();

        Call<List<Activo>> llamada = api.getActivos(
                "eur",
                "",
                "market_cap_desc",
                50
        );

        llamada.enqueue(new Callback<List<Activo>>() {

            @Override
            public void onResponse(Call<List<Activo>> call, Response<List<Activo>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    resultado.setValue(response.body());
                } else {
                    resultado.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<Activo>> call, Throwable t) {
                resultado.setValue(null);
            }
        });

        return resultado;
    }
}