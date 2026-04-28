package com.datamarkets.app.ui.noticias;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.datamarkets.app.R;
import java.util.ArrayList;

public class NoticiasFragment extends Fragment {

    private NoticiasViewModel viewModel;
    private NoticiaAdapter adapter;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_noticias, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewNoticias);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));

        adapter = new NoticiaAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        progressBar = view.findViewById(R.id.progressBar);

        // ViewModel
        viewModel = new ViewModelProvider(this).get(NoticiasViewModel.class);

        // Observar noticias
        viewModel.getNoticias().observe(getViewLifecycleOwner(), noticias -> {
            adapter.updateNoticias(noticias);
        });

        // Observar estado de carga
        viewModel.getCargando().observe(getViewLifecycleOwner(), cargando -> {
            progressBar.setVisibility(cargando ? View.VISIBLE : View.GONE);
        });

        // Observar errores
        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        // Cargar noticias solo la primera vez
        if (viewModel.getNoticias().getValue() == null) {
            viewModel.cargarNoticias();
        }
    }
}