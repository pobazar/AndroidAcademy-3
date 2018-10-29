package com.example.androidacademy2.Net;

import com.example.androidacademy2.DTO.NewsDTO;

import java.util.List;

import androidx.annotation.NonNull;
import io.reactivex.Single;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NewsEndpoint {
    @GET("food.json")
    //Single<Response<List<NewsDTO>>> search(String category);
    Single<Response<List<NewsDTO>>> search();
}