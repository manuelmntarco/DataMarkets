package com.datamarkets.app.network;

import com.datamarkets.app.model.GNewsResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GNewsApiService {

    @GET("search")
    Call<GNewsResponse> getNews(
            @Query("q") String query,
            @Query("lang") String lang,
            @Query("country") String country,
            @Query("max") int max,
            @Query("token") String token
    );
}