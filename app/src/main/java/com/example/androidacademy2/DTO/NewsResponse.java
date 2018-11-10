package com.example.androidacademy2.DTO;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import androidx.annotation.Nullable;

public class NewsResponse {
    private List<NewsDTO> results;

    @Nullable
    public List<NewsDTO> getData() {
        return results;
    }
}