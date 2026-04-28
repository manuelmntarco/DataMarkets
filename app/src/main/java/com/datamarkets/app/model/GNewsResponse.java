package com.datamarkets.app.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class GNewsResponse {

    @SerializedName("totalArticles")
    private int totalArticles;

    @SerializedName("articles")
    private List<GNewsItem> articles;

    public int getTotalArticles() { return totalArticles; }
    public List<GNewsItem> getArticles() { return articles; }
}
