package com.datamarkets.app.ui.seguimiento;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.datamarkets.app.R;
import com.datamarkets.app.model.Activo;
import java.util.List;

public class SeguimientoAdapter extends
        RecyclerView.Adapter<SeguimientoAdapter.ActivoViewHolder> {

    // La lista de activos que va a mostrar el adaptador
    private List<Activo> listaActivos;

    // Interfaz para avisar al Fragment cuando el usuario
    // pulsa el botón de eliminar de un activo
    public interface OnEliminarClickListener {
        void onEliminarClick(Activo activo);
    }

    private OnEliminarClickListener listener;

    // Constructor: recibe la lista y el listener
    public SeguimientoAdapter(List<Activo> listaActivos,
                              OnEliminarClickListener listener) {
        this.listaActivos = listaActivos;
        this.listener     = listener;
    }

    // Método para actualizar la lista desde fuera
    // Se llamará cuando lleguen datos nuevos del ViewModel
    public void actualizarLista(List<Activo> nuevaLista) {
        this.listaActivos = nuevaLista;
        notifyDataSetChanged();
    }

    // ── Los tres métodos obligatorios del adaptador ──────────

    // 1. Crea la vista de cada fila inflando el XML del item
    @NonNull
    @Override
    public ActivoViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                               int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_activo_seguimiento, parent, false);
        return new ActivoViewHolder(vista);
    }

    // 2. Rellena cada fila con los datos del activo correspondiente
    @Override
    public void onBindViewHolder(@NonNull ActivoViewHolder holder,
                                 int position) {
        Activo activo = listaActivos.get(position);
        holder.bind(activo, listener);
    }

    // 3. Devuelve cuántos items tiene la lista
    @Override
    public int getItemCount() {
        return listaActivos != null ? listaActivos.size() : 0;
    }

    // ── ViewHolder: representa cada fila de la lista ─────────
    // Guarda las referencias a las vistas del item para no
    // tener que buscarlas cada vez (mejora el rendimiento)
    static class ActivoViewHolder extends RecyclerView.ViewHolder {

        TextView tvSimbolo;
        TextView tvNombre;
        TextView tvPrecio;
        TextView tvVariacion;
        ImageButton btnEliminar;

        public ActivoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSimbolo   = itemView.findViewById(R.id.tvSimbolo);
            tvNombre    = itemView.findViewById(R.id.tvNombre);
            tvPrecio    = itemView.findViewById(R.id.tvPrecio);
            tvVariacion = itemView.findViewById(R.id.tvVariacion);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }

        // Rellena las vistas con los datos del activo
        public void bind(Activo activo, OnEliminarClickListener listener) {

            tvSimbolo.setText(activo.getSimbolo());
            tvNombre.setText(activo.getNombre());

            // Formatea el precio con 2 decimales y símbolo €
            tvPrecio.setText(String.format("%.2f €", activo.getPrecioActual()));

            // Formatea la variación con flecha y color según suba o baje
            double variacion = activo.getVariacion24h();
            if (variacion >= 0) {
                tvVariacion.setText(String.format("▲ +%.2f%%", variacion));
                tvVariacion.setTextColor(
                        itemView.getContext().getColor(R.color.variacion_positiva));
            } else {
                tvVariacion.setText(String.format("▼ %.2f%%", variacion));
                tvVariacion.setTextColor(
                        itemView.getContext().getColor(R.color.variacion_negativa));
            }

            // Cuando se pulsa el botón eliminar avisa al Fragment
            btnEliminar.setOnClickListener(v -> listener.onEliminarClick(activo));
        }
    }
}