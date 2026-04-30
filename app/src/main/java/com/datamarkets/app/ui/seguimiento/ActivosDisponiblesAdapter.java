package com.datamarkets.app.ui.seguimiento;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.datamarkets.app.R;
import com.datamarkets.app.model.Activo;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ActivosDisponiblesAdapter extends
        RecyclerView.Adapter<ActivosDisponiblesAdapter.ActivoViewHolder> {

    // Lista completa de activos (todos los del catálogo)
    private List<Activo> listaCompleta;

    // Lista filtrada (la que realmente se muestra)
    private List<Activo> listaFiltrada;

    // Listener para avisar cuando se selecciona un activo
    public interface OnActivoClickListener {
        void onActivoClick(Activo activo);
    }

    private final OnActivoClickListener listener;

    public ActivosDisponiblesAdapter(List<Activo> lista,
                                     OnActivoClickListener listener) {
        this.listaCompleta = new ArrayList<>(lista);
        this.listaFiltrada = new ArrayList<>(lista);
        this.listener      = listener;
    }

    // Actualiza la lista completa (cuando llegan datos del backend)
    public void actualizarLista(List<Activo> nuevaLista) {
        this.listaCompleta = new ArrayList<>(nuevaLista);
        this.listaFiltrada = new ArrayList<>(nuevaLista);
        notifyDataSetChanged();
    }

    // Filtra la lista según el texto introducido en la búsqueda
    public void filtrar(String texto) {
        listaFiltrada.clear();

        if (texto == null || texto.trim().isEmpty()) {
            // Sin texto: mostrar todos
            listaFiltrada.addAll(listaCompleta);
        } else {
            // Filtrar por símbolo o nombre (insensible a mayúsculas)
            String filtro = texto.toLowerCase(Locale.ROOT).trim();
            for (Activo activo : listaCompleta) {
                String simbolo = activo.getSimbolo().toLowerCase(Locale.ROOT);
                String nombre  = activo.getNombre().toLowerCase(Locale.ROOT);
                if (simbolo.contains(filtro) || nombre.contains(filtro)) {
                    listaFiltrada.add(activo);
                }
            }
        }

        notifyDataSetChanged();
    }

    // Devuelve cuántos resultados hay tras filtrar
    public boolean estaVacia() {
        return listaFiltrada.isEmpty();
    }

    // ── Métodos obligatorios del adaptador ──────────────────

    @NonNull
    @Override
    public ActivoViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                               int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_activo_disponible, parent, false);
        return new ActivoViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivoViewHolder holder,
                                 int position) {
        Activo activo = listaFiltrada.get(position);
        holder.bind(activo, listener);
    }

    @Override
    public int getItemCount() {
        return listaFiltrada.size();
    }

    // ── ViewHolder ──────────────────────────────────────────

    static class ActivoViewHolder extends RecyclerView.ViewHolder {

        TextView tvSimbolo;
        TextView tvNombre;

        public ActivoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSimbolo = itemView.findViewById(R.id.tvSimbolo);
            tvNombre  = itemView.findViewById(R.id.tvNombre);
        }

        public void bind(Activo activo, OnActivoClickListener listener) {
            tvSimbolo.setText(activo.getSimbolo().toUpperCase(Locale.ROOT));
            tvNombre.setText(activo.getNombre());

            // Al pulsar la fila avisa al listener con el activo seleccionado
            itemView.setOnClickListener(v -> listener.onActivoClick(activo));
        }
    }
}