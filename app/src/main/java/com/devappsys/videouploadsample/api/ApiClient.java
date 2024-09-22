package com.devappsys.videouploadsample.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static final String BASE_URL = "http://192.168.1.38:8080";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())  // GsonConverter for parsing JSON responses
                    .build();
        }
        return retrofit;
    }

    // Method to get the ApiService interface
    public static ApiService getApiService() {
        return getClient().create(ApiService.class);
    }
}
