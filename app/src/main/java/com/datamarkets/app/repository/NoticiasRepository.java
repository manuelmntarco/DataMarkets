package com.datamarkets.app.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.datamarkets.app.model.GNewsItem;
import com.datamarkets.app.model.GNewsResponse;
import com.datamarkets.app.model.Noticia;
import com.datamarkets.app.model.NoticiaMapper;
import com.datamarkets.app.network.GNewsApiService;
import com.datamarkets.app.network.RetrofitClient;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NoticiasRepository {

    private static final String API_KEY = "baea2e98f9811581c71bb822e9101cec";

    private static final String[] QUERIES = {
            "bolsa mercados financieros",
            "criptomonedas bitcoin",
            "materias primas oro petroleo",
            "economia finanzas"
    };

    private static final int[] MAX_POR_QUERY = {2, 3, 3, 2};

    private final GNewsApiService apiService;
    private final MutableLiveData<List<Noticia>> noticiasLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> cargandoLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    private final List<Noticia> todasLasNoticias = new ArrayList<>();
    private int queryIndex = 0;

    public NoticiasRepository() {
        apiService = RetrofitClient.getInstance().getApiService();
    }

    public LiveData<List<Noticia>> getNoticias() { return noticiasLiveData; }
    public LiveData<Boolean> getCargando() { return cargandoLiveData; }
    public LiveData<String> getError() { return errorLiveData; }

    public void cargarNoticias() {
        todasLasNoticias.clear();
        queryIndex = 0;
        cargandoLiveData.postValue(true);
        cargarSiguienteQuery();
    }

    private void cargarSiguienteQuery() {
        if (queryIndex >= QUERIES.length) {
            cargandoLiveData.postValue(false);
            if (todasLasNoticias.isEmpty()) {
                errorLiveData.postValue("No se encontraron noticias");
            } else {
                noticiasLiveData.postValue(new ArrayList<>(todasLasNoticias));
            }
            return;
        }

        final int indiceActual = queryIndex;
        final String queryActual = QUERIES[indiceActual];

        Call<GNewsResponse> call = apiService.getNews(queryActual, "es", "es", MAX_POR_QUERY[indiceActual], API_KEY);

        call.enqueue(new Callback<GNewsResponse>() {
            @Override
            public void onResponse(Call<GNewsResponse> call, Response<GNewsResponse> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().getArticles() != null) {
                    for (GNewsItem item : response.body().getArticles()) {
                        item.setCategoria(queryActual);
                        todasLasNoticias.add(NoticiaMapper.fromGNewsItem(item));
                    }
                }
                queryIndex++;
                new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(
                        () -> cargarSiguienteQuery(), 1000
                );
            }

            @Override
            public void onFailure(Call<GNewsResponse> call, Throwable t) {
                android.util.Log.e("GNews", "Error: " + queryActual + " | " + t.getMessage());
                queryIndex++;
                new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(
                        () -> cargarSiguienteQuery(), 1000
                );
            }
        });
    }
}
