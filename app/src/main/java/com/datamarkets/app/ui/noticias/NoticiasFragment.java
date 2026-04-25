package com.datamarkets.app.ui.noticias;

import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.datamarkets.app.R;
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

public class NoticiasFragment extends Fragment {

    private static final String API_KEY = "baea2e98f9811581c71bb822e9101cec";

    private static final String[] QUERIES = {
            "bolsa mercados financieros",
            "criptomonedas bitcoin",
            "materias primas oro petroleo",
            "economia finanzas"
    };

    private static final int[] MAX_POR_QUERY = {2, 3, 3, 2};

    private NoticiaAdapter adapter;
    private ProgressBar progressBar;
    private final List<Noticia> todasLasNoticias = new ArrayList<>();
    private int queryIndex = 0;
    private boolean isViewAlive = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_noticias, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isViewAlive = true;

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewNoticias);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));

        adapter = new NoticiaAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        progressBar = view.findViewById(R.id.progressBar);

        cargarTodasLasNoticias();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isViewAlive = false;
    }

    private void cargarTodasLasNoticias() {
        progressBar.setVisibility(View.VISIBLE);
        todasLasNoticias.clear();
        queryIndex = 0;
        cargarSiguienteQuery();
    }

    private void cargarSiguienteQuery() {
        if (!isViewAlive) return;

        if (queryIndex >= QUERIES.length) {
            progressBar.setVisibility(View.GONE);
            if (todasLasNoticias.isEmpty()) {
                Toast.makeText(getContext(), "No se encontraron noticias", Toast.LENGTH_SHORT).show();
            } else {
                adapter.updateNoticias(todasLasNoticias);
            }
            return;
        }

        final int indiceActual = queryIndex;
        final String queryActual = QUERIES[indiceActual];

        GNewsApiService apiService = RetrofitClient.getInstance().getApiService();
        Call<GNewsResponse> call = apiService.getNews(queryActual, "es", "es", MAX_POR_QUERY[indiceActual], API_KEY);

        call.enqueue(new Callback<GNewsResponse>() {
            @Override
            public void onResponse(Call<GNewsResponse> call, Response<GNewsResponse> response) {
                // Usar Looper.getMainLooper() en lugar de requireActivity().getMainLooper()
                new android.os.Handler(Looper.getMainLooper()).post(() -> {
                    if (!isViewAlive) return;

                    if (response.isSuccessful() && response.body() != null
                            && response.body().getArticles() != null) {
                        for (GNewsItem item : response.body().getArticles()) {
                            item.setCategoria(queryActual);
                            todasLasNoticias.add(NoticiaMapper.fromGNewsItem(item));
                        }
                    }
                    queryIndex++;
                    new android.os.Handler(Looper.getMainLooper()).postDelayed(
                            () -> cargarSiguienteQuery(), 1000
                    );
                });
            }

            @Override
            public void onFailure(Call<GNewsResponse> call, Throwable t) {
                new android.os.Handler(Looper.getMainLooper()).post(() -> {
                    if (!isViewAlive) return;
                    queryIndex++;
                    new android.os.Handler(Looper.getMainLooper()).postDelayed(
                            () -> cargarSiguienteQuery(), 1000
                    );
                });
            }
        });
    }
}