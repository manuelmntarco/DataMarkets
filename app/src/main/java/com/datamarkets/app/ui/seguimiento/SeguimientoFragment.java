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
import com.datamarkets.app.model.Activo;
import com.datamarkets.app.viewmodel.SeguimientoViewModel;
import java.util.ArrayList;

public class SeguimientoFragment extends Fragment {

    // Referencias a las vistas del layout
    private RecyclerView recyclerSeguimiento;
    private LinearLayout layoutVacio;
    private ImageButton btnAnyadirFavorito;

    // El ViewModel y el adaptador
    private SeguimientoViewModel viewModel;
    private SeguimientoAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Infla el layout XML de la pantalla
        return inflater.inflate(
                R.layout.fragment_seguimiento, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Obtiene las referencias a las vistas del layout
        recyclerSeguimiento = view.findViewById(R.id.recyclerSeguimiento);
        layoutVacio         = view.findViewById(R.id.layoutVacio);
        btnAnyadirFavorito  = view.findViewById(R.id.btnAnyadirFavorito);

        // 2. Configura el RecyclerView
        configurarRecyclerView();

        // 3. Obtiene el ViewModel
        viewModel = new ViewModelProvider(this)
                .get(SeguimientoViewModel.class);

        // 4. Observa el LiveData del ViewModel
        // Cada vez que cambien los datos se ejecuta este bloque
        viewModel.getFavoritos().observe(getViewLifecycleOwner(),
                activos -> {
                    if (activos == null || activos.isEmpty()) {
                        // Sin datos: muestra el mensaje de pantalla vacía
                        recyclerSeguimiento.setVisibility(View.GONE);
                        layoutVacio.setVisibility(View.VISIBLE);
                    } else {
                        // Con datos: muestra la lista y oculta el mensaje
                        recyclerSeguimiento.setVisibility(View.VISIBLE);
                        layoutVacio.setVisibility(View.GONE);
                        adapter.actualizarLista(activos);
                    }
                });

        // 5. Pide los datos al ViewModel
        viewModel.cargarFavoritos();

        // 6. Configura el botón de añadir favorito
        // Por ahora muestra un mensaje, luego abrirá una pantalla
        btnAnyadirFavorito.setOnClickListener(v ->
                Toast.makeText(getContext(),
                        "Próximamente: añadir favorito",
                        Toast.LENGTH_SHORT).show());
    }

    private void configurarRecyclerView() {
        // Crea el adaptador con lista vacía inicial
        // y el listener para eliminar favoritos
        adapter = new SeguimientoAdapter(
                new ArrayList<>(),
                activo -> {
                    // Cuando se pulsa eliminar avisa al ViewModel
                    viewModel.eliminarFavorito(activo);
                    Toast.makeText(getContext(),
                            activo.getNombre() + " eliminado de favoritos",
                            Toast.LENGTH_SHORT).show();
                });

        // Asigna el adaptador al RecyclerView
        recyclerSeguimiento.setAdapter(adapter);

        // Indica que los items se ordenan verticalmente
        recyclerSeguimiento.setLayoutManager(
                new LinearLayoutManager(getContext()));

        // Añade el separador entre items
        recyclerSeguimiento.addItemDecoration(
                new DividerItemDecoration(
                        getContext(),
                        DividerItemDecoration.VERTICAL));
    }
}