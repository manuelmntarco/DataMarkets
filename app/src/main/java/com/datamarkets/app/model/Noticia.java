package com.datamarkets.app.model;

public class Noticia {

    private String titulo;
    private String descripcion;
    private String fuente;
    private String fechaPublicacion;
    private String url;
    private String imagenUrl;
    private String categoria;

    public Noticia(String titulo, String descripcion, String fuente,
                   String fechaPublicacion, String url, String imagenUrl, String categoria) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fuente = fuente;
        this.fechaPublicacion = fechaPublicacion;
        this.url = url;
        this.imagenUrl = imagenUrl;
        this.categoria = categoria;
    }

    public String getTitulo() { return titulo; }
    public String getDescripcion() { return descripcion; }
    public String getFuente() { return fuente; }
    public String getFechaPublicacion() { return fechaPublicacion; }
    public String getUrl() { return url; }
    public String getImagenUrl() { return imagenUrl; }
    public String getCategoria() { return categoria; }
}