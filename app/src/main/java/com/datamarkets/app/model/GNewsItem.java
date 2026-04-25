package com.datamarkets.app.model;

import com.google.gson.annotations.SerializedName;

public class GNewsItem {

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("content")
    private String content;

    @SerializedName("url")
    private String url;

    @SerializedName("image")
    private String image;

    @SerializedName("publishedAt")
    private String publishedAt;

    @SerializedName("source")
    private Source source;

    // Campo extra, no viene de la API, lo asignamos nosotros
    private String categoria;

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getContent() { return content; }
    public String getUrl() { return url; }
    public String getImage() { return image; }
    public String getPublishedAt() { return publishedAt; }
    public Source getSource() { return source; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public static class Source {
        @SerializedName("name")
        private String name;

        @SerializedName("url")
        private String url;

        public String getName() { return name; }
        public String getUrl() { return url; }
    }
}
