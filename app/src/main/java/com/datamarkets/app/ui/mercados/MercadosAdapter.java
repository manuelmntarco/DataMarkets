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

    private List<Activo> listaActivos = new ArrayList<>();

    @NonNull
    @Override
    public ActivoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_activo, parent, false);
        return new ActivoViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivoViewHolder holder, int position) {
        Activo activo = listaActivos.get(position);

        holder.txtNombre.setText(activo.getNombre());
        holder.txtSimbolo.setText(activo.getSimbolo());

        holder.txtPrecio.setText(String.format("€%.2f", activo.getPrecioActual()));

        double variacion = activo.getVariacion24h();
        holder.txtVariacion.setText(String.format("%.2f%%", variacion));
        if (variacion >= 0) {
            holder.txtVariacion.setTextColor(holder.itemView.getContext()
                    .getColor(R.color.variacion_positiva));
        } else {
            holder.txtVariacion.setTextColor(holder.itemView.getContext()
                    .getColor(R.color.variacion_negativa));
        }

        Glide.with(holder.itemView.getContext())
                .load(activo.getImagenUrl())
                .circleCrop()
                .into(holder.imgLogo);
    }

    @Override
    public int getItemCount() {
        return listaActivos.size();
    }

    public void setActivos(List<Activo> activos) {
        this.listaActivos = activos;
        notifyDataSetChanged();
    }

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