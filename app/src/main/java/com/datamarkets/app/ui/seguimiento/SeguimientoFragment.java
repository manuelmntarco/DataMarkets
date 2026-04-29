package com.datamarkets.app.ui.seguimiento;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.datamarkets.app.R;
import com.datamarkets.app.viewmodel.SeguimientoViewModel;

import java.util.ArrayList;

public class SeguimientoFragment extends Fragment {

    private RecyclerView recyclerSeguimiento;
    private LinearLayout layoutVacio;
    private ImageButton btnAnyadirFavorito;

    private SeguimientoViewModel viewModel;
    private SeguimientoAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(
                R.layout.fragment_seguimiento, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Referencias a las vistas
        recyclerSeguimiento = view.findViewById(R.id.recyclerSeguimiento);
        layoutVacio         = view.findViewById(R.id.layoutVacio);
        btnAnyadirFavorito  = view.findViewById(R.id.btnAnyadirFavorito);

        // 2. Configura el RecyclerView
        configurarRecyclerView();

        // 3. Obtiene el ViewModel
        viewModel = new ViewModelProvider(this)
                .get(SeguimientoViewModel.class);

        // 4. Observa la lista de favoritos
        viewModel.getFavoritos().observe(getViewLifecycleOwner(),
                activos -> {
                    if (activos == null || activos.isEmpty()) {
                        recyclerSeguimiento.setVisibility(View.GONE);
                        layoutVacio.setVisibility(View.VISIBLE);
                    } else {
                        recyclerSeguimiento.setVisibility(View.VISIBLE);
                        layoutVacio.setVisibility(View.GONE);
                        adapter.actualizarLista(activos);
                    }
                });

        // 5. Observa mensajes de éxito
        viewModel.getMensaje().observe(getViewLifecycleOwner(),
                msg -> {
                    if (msg != null) {
                        Toast.makeText(getContext(), msg,
                                Toast.LENGTH_SHORT).show();
                    }
                });

        // 6. Observa mensajes de error
        viewModel.getError().observe(getViewLifecycleOwner(),
                err -> {
                    if (err != null) {
                        Toast.makeText(getContext(), err,
                                Toast.LENGTH_SHORT).show();
                    }
                });

        // 7. Pide los favoritos al backend
        viewModel.cargarFavoritos();

        // 8. Botón añadir favorito (provisional)
        btnAnyadirFavorito.setOnClickListener(v ->
                Toast.makeText(getContext(),
                        "Próximamente: añadir favorito",
                        Toast.LENGTH_SHORT).show());
    }

    private void configurarRecyclerView() {
        adapter = new SeguimientoAdapter(
                new ArrayList<>(),
                activo -> {
                    // Llama al ViewModel con el id del activo
                    viewModel.eliminarFavorito(activo.getId());
                });

        recyclerSeguimiento.setAdapter(adapter);
        recyclerSeguimiento.setLayoutManager(
                new LinearLayoutManager(getContext()));
        recyclerSeguimiento.addItemDecoration(
                new DividerItemDecoration(
                        getContext(),
                        DividerItemDecoration.VERTICAL));
    }
}
