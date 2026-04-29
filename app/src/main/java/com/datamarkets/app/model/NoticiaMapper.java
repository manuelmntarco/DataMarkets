package com.datamarkets.app.model;

public class NoticiaMapper {

    // Convierte un GNewsItem (modelo de red) a Noticia (modelo de la app)
    public static Noticia fromGNewsItem(GNewsItem item) {
        String fuente = item.getSource() != null ? item.getSource().getName() : "Fuente desconocida";

        return new Noticia(
                item.getTitle(),
                item.getDescription() != null ? item.getDescription() : "Sin descripción",
                fuente,
                item.getPublishedAt(),
                item.getUrl(),
                item.getImage(),
                item.getCategoria()
        );
    }
}