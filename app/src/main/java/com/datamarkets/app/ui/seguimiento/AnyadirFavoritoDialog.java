package com.datamarkets.app.ui.seguimiento;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.datamarkets.app.R;
import com.datamarkets.app.model.Activo;
import com.datamarkets.app.repository.ActivosRepository;

import java.util.ArrayList;
import java.util.List;

public class AnyadirFavoritoDialog extends DialogFragment {

    // Vistas del layout
    private EditText etBuscar;
    private RecyclerView recyclerActivos;
    private TextView tvSinResultados;
    private Button btnCancelar;

    private ActivosDisponiblesAdapter adapter;
    private ActivosRepository repository;

    // Listener para avisar al Fragment cuando se selecciona un activo
    public interface OnFavoritoSeleccionadoListener {
        void onFavoritoSeleccionado(String idExterno);
    }

    private OnFavoritoSeleccionadoListener listener;

    public void setOnFavoritoSeleccionadoListener(
            OnFavoritoSeleccionadoListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialogo_anyadir_favorito);

        // Hacer el diálogo más ancho
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(
                    (int) (getResources().getDisplayMetrics().widthPixels * 0.9),
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        // 1. Obtener referencias a las vistas
        etBuscar         = dialog.findViewById(R.id.etBuscar);
        recyclerActivos  = dialog.findViewById(R.id.recyclerActivos);
        tvSinResultados  = dialog.findViewById(R.id.tvSinResultados);
        btnCancelar      = dialog.findViewById(R.id.btnCancelar);

        // 2. Inicializar el repositorio
        repository = new ActivosRepository(requireContext());

        // 3. Configurar la lista
        configurarRecyclerView();

        // 4. Configurar el campo de búsqueda
        configurarBuscador();

        // 5. Configurar el botón cancelar
        btnCancelar.setOnClickListener(v -> dismiss());

        // 6. Cargar los activos del backend
        cargarActivos();

        return dialog;
    }

    private void configurarRecyclerView() {
        adapter = new ActivosDisponiblesAdapter(
                new ArrayList<>(),
                this::seleccionarActivo);

        recyclerActivos.setAdapter(adapter);
        recyclerActivos.setLayoutManager(
                new LinearLayoutManager(getContext()));
    }

    private void configurarBuscador() {
        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                adapter.filtrar(s.toString());
                actualizarVisibilidad();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void cargarActivos() {
        repository.obtenerActivos(new ActivosRepository.OnActivosListener() {
            @Override
            public void onExito(List<Activo> activos) {
                if (!isAdded()) return; // diálogo cerrado mientras carga
                adapter.actualizarLista(activos);
                actualizarVisibilidad();
            }

            @Override
            public void onError(String mensaje) {
                if (!isAdded()) return;
                Toast.makeText(getContext(), mensaje,
                        Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
    }

    private void seleccionarActivo(Activo activo) {
        if (listener != null) {
            listener.onFavoritoSeleccionado(activo.getId());
        }
        dismiss();
    }

    private void actualizarVisibilidad() {
        if (adapter.estaVacia()) {
            recyclerActivos.setVisibility(View.GONE);
            tvSinResultados.setVisibility(View.VISIBLE);
        } else {
            recyclerActivos.setVisibility(View.VISIBLE);
            tvSinResultados.setVisibility(View.GONE);
        }
    }
}