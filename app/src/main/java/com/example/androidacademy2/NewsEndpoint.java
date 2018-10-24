package com.example.androidacademy2;

import com.example.androidacademy2.DTO.NewsDTO;

import java.util.List;

import androidx.annotation.NonNull;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NewsEndpoint {
        @NonNull
        @GET("")
        retrofit2.Call<List<NewsDTO>> search(String search);
}