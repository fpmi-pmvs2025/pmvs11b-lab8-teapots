package com.example.lab8_v2;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface CountryApi {
    @GET("all")
    Call<List<Country>> getCountries();
} 