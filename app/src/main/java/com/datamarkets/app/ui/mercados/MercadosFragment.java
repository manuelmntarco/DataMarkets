package com.datamarkets.app.ui.mercados;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.datamarkets.app.R;
import com.datamarkets.app.model.Activo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MercadosFragment extends Fragment {

    private MercadosViewModel viewModel;
    private MercadosAdapter adapter;
    private ProgressBar progressBar;
    private List<Activo> listaCompleta = new ArrayList<>();

    private TextView btnTodas, btnGanadoras, btnPerdedoras, btnTopCap;

    public MercadosFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_mercados, container, false);

        progressBar = vista.findViewById(R.id.progressBar);
        RecyclerView recyclerView = vista.findViewById(R.id.recyclerActivos);
        btnTodas = vista.findViewById(R.id.btnTodas);
        btnGanadoras = vista.findViewById(R.id.btnGanadoras);
        btnPerdedoras = vista.findViewById(R.id.btnPerdedoras);
        btnTopCap = vista.findViewById(R.id.btnTopCap);

        adapter = new MercadosAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(MercadosViewModel.class);

        cargarActivos();

        btnTodas.setOnClickListener(v -> {
            activarBoton(btnTodas);
            adapter.setActivos(listaCompleta);
        });

        btnGanadoras.setOnClickListener(v -> {
            activarBoton(btnGanadoras);
            List<Activo> ganadoras = listaCompleta.stream()
                    .filter(a -> a.getVariacion24h() > 0)
                    .sorted(Comparator.comparingDouble(Activo::getVariacion24h).reversed())
                    .collect(Collectors.toList());
            adapter.setActivos(ganadoras);
        });

        btnPerdedoras.setOnClickListener(v -> {
            activarBoton(btnPerdedoras);
            List<Activo> perdedoras = listaCompleta.stream()
                    .filter(a -> a.getVariacion24h() < 0)
                    .sorted(Comparator.comparingDouble(Activo::getVariacion24h))
                    .collect(Collectors.toList());
            adapter.setActivos(perdedoras);
        });

        btnTopCap.setOnClickListener(v -> {
            activarBoton(btnTopCap);
            List<Activo> topCap = listaCompleta.stream()
                    .sorted(Comparator.comparingLong(Activo::getCapitalizacion).reversed())
                    .collect(Collectors.toList());
            adapter.setActivos(topCap);
        });

        return vista;
    }

    private void cargarActivos() {
        progressBar.setVisibility(View.VISIBLE);

        viewModel.obtenerActivos().observe(getViewLifecycleOwner(), activos -> {
            progressBar.setVisibility(View.GONE);

            if (activos != null) {
                listaCompleta = activos;
                adapter.setActivos(listaCompleta);
                activarBoton(btnTodas);
            } else {
                Toast.makeText(getContext(),
                        "Error al cargar los datos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Pone el botón pulsado en azul y los demás en blanco con contorno
    private void activarBoton(TextView botonActivo) {
        List<TextView> botones = new ArrayList<>();
        botones.add(btnTodas);
        botones.add(btnGanadoras);
        botones.add(btnPerdedoras);
        botones.add(btnTopCap);

        for (TextView boton : botones) {
            if (boton == botonActivo) {
                boton.setBackground(ContextCompat.getDrawable(
                        requireContext(), R.drawable.fondo_boton_activo));
                boton.setTextColor(ContextCompat.getColor(
                        requireContext(), R.color.black));
            } else {
                boton.setBackground(ContextCompat.getDrawable(
                        requireContext(), R.drawable.fondo_boton_inactivo));
                boton.setTextColor(ContextCompat.getColor(
                        requireContext(), R.color.black));
            }
        }
    }
}