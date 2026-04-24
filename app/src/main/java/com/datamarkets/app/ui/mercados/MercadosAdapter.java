package com.datamarkets.app.ui.mercados;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.datamarkets.app.R;
import com.datamarkets.app.model.Activo;

import java.util.ArrayList;
import java.util.List;

public class MercadosAdapter extends RecyclerView.Adapter<MercadosAdapter.ActivoViewHolder> {

    // La lista de activos que vamos a mostrar
    private List<Activo> listaActivos = new ArrayList<>();

    // Este método crea una nueva fila visual cuando el RecyclerView la necesita
    @NonNull
    @Override
    public ActivoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_activo, parent, false);
        return new ActivoViewHolder(vista);
    }

    // Este método rellena los datos de una fila concreta
    @Override
    public void onBindViewHolder(@NonNull ActivoViewHolder holder, int position) {
        Activo activo = listaActivos.get(position);

        holder.txtNombre.setText(activo.getNombre());
        holder.txtSimbolo.setText(activo.getSimbolo());

        // Formatear el precio con 2 decimales y símbolo €
        holder.txtPrecio.setText(String.format("€%.2f", activo.getPrecioActual()));

        // Formatear la variación con + o - y color verde/rojo
        double variacion = activo.getVariacion24h();
        holder.txtVariacion.setText(String.format("%.2f%%", variacion));
        if (variacion >= 0) {
            holder.txtVariacion.setTextColor(Color.parseColor("#4CAF50")); // verde
        } else {
            holder.txtVariacion.setTextColor(Color.parseColor("#F44336")); // rojo
        }

        // Cargar la imagen con Glide
        Glide.with(holder.itemView.getContext())
                .load(activo.getImagenUrl())
                .circleCrop()
                .into(holder.imgLogo);
    }

    // Este método le dice al RecyclerView cuántas filas hay en total
    @Override
    public int getItemCount() {
        return listaActivos.size();
    }

    // Método para actualizar la lista desde fuera (lo usará el Fragment)
    public void setActivos(List<Activo> activos) {
        this.listaActivos = activos;
        notifyDataSetChanged();
    }

    // El ViewHolder guarda las referencias a las vistas de cada fila
    // Así no tenemos que buscarlas cada vez, lo que mejora el rendimiento
    static class ActivoViewHolder extends RecyclerView.ViewHolder {
        ImageView imgLogo;
        TextView txtNombre;
        TextView txtSimbolo;
        TextView txtPrecio;
        TextView txtVariacion;

        ActivoViewHolder(@NonNull View itemView) {
            super(itemView);
            imgLogo = itemView.findViewById(R.id.imgLogo);
            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtSimbolo = itemView.findViewById(R.id.txtSimbolo);
            txtPrecio = itemView.findViewById(R.id.txtPrecio);
            txtVariacion = itemView.findViewById(R.id.txtVariacion);
        }
    }
}
