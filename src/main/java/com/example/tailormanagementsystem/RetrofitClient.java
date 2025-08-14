package com.example.tailormanagementsystem;

import retrofit2.Retrofit;
import retrofit2.Call;
import retrofit2.Response;
import okhttp3.ResponseBody;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "https://tailor-management-system-1d795.web.app"; // change this
    private static Retrofit retrofit;

    public static Retrofit getInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}