package com.datamarkets.app.ui.noticias;

import android.content.Intent;
import android.net.Uri;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.datamarkets.app.R;
import com.datamarkets.app.model.Noticia;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class NoticiaAdapter extends RecyclerView.Adapter<NoticiaAdapter.NoticiaViewHolder> {

    private List<Noticia> noticias;

    public NoticiaAdapter(List<Noticia> noticias) {
        this.noticias = noticias;
    }

    public void updateNoticias(List<Noticia> nuevasNoticias) {
        this.noticias = nuevasNoticias;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NoticiaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_noticia, parent, false);
        return new NoticiaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoticiaViewHolder holder, int position) {
        Noticia item = noticias.get(position);

        // Título con subrayado
        SpannableString spannableTitle = new SpannableString(item.getTitulo());
        spannableTitle.setSpan(new UnderlineSpan(), 0, spannableTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.textTitulo.setText(spannableTitle);

        // Descripción
        holder.textResumen.setText(item.getDescripcion());

        // Fuente + tiempo relativo
        holder.textFuente.setText(item.getFuente() + " | " + getTiempoRelativo(item.getFechaPublicacion()));

        // Imagen con Glide, icono local como placeholder según categoría
        int iconoLocal = getIconoPorCategoria(item.getCategoria());
        if (item.getImagenUrl() != null && !item.getImagenUrl().isEmpty()) {
            Glide.with(holder.imageIcon.getContext())
                    .load(item.getImagenUrl())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .placeholder(iconoLocal)
                    .error(iconoLocal)
                    .centerCrop()
                    .into(holder.imageIcon);
        } else {
            holder.imageIcon.setImageResource(iconoLocal);
        }

        // Abrir noticia en el navegador al pulsar
        holder.itemView.setOnClickListener(v -> {
            if (item.getUrl() != null && !item.getUrl().isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.getUrl()));
                v.getContext().startActivity(intent);
            }
        });
    }

    private int getIconoPorCategoria(String categoria) {
        if (categoria == null) return R.drawable.ic_mercados;
        if (categoria.contains("cripto")) return R.drawable.ic_not_crypto;
        if (categoria.contains("bolsa")) return R.drawable.ic_mercados;
        if (categoria.contains("materias")) return R.drawable.ic_not_materias;
        return R.drawable.ic_not_inversion;
    }

    @Override
    public int getItemCount() {
        return noticias != null ? noticias.size() : 0;
    }

    private String getTiempoRelativo(String fechaPublicacion) {
        if (fechaPublicacion == null) return "fecha desconocida";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date fecha = sdf.parse(fechaPublicacion);
            if (fecha == null) return "fecha desconocida";

            long diferencia = System.currentTimeMillis() - fecha.getTime();
            long minutos = diferencia / (1000 * 60);
            long horas = diferencia / (1000 * 60 * 60);
            long dias = diferencia / (1000 * 60 * 60 * 24);

            if (minutos < 60) return "hace " + minutos + " min";
            else if (horas < 24) return horas == 1 ? "hace 1 hora" : "hace " + horas + " horas";
            else if (dias == 1) return "ayer";
            else if (dias < 7) return "hace " + dias + " días";
            else {
                SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                return formato.format(fecha);
            }
        } catch (ParseException e) {
            return "fecha desconocida";
        }
    }

    static class NoticiaViewHolder extends RecyclerView.ViewHolder {
        ImageView imageIcon;
        TextView textTitulo, textResumen, textFuente;

        public NoticiaViewHolder(@NonNull View itemView) {
            super(itemView);
            imageIcon = itemView.findViewById(R.id.imageIcon);
            textTitulo = itemView.findViewById(R.id.textTitulo);
            textResumen = itemView.findViewById(R.id.textResumen);
            textFuente = itemView.findViewById(R.id.textFuente);
        }
    }
}
