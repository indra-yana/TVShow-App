package com.training.tvshowapp.network;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static Retrofit retrofit;
    private final static String BASE_URL = "https://www.episodate.com/api/";
    private final static String API_KEY = "your_secret_api_key";

    public static Retrofit getRetrofit() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }

    public static Retrofit getRetrofitWithAPIKey() {
        if (retrofit == null) {
            Interceptor interceptor = chain -> {
                HttpUrl httpUrl = chain.request()
                        .url()
                        .newBuilder()
                        .addQueryParameter("api_key", API_KEY)
                        .build();

                Request request = chain.request()
                        .newBuilder()
                        .url(httpUrl)
                        .build();

                return chain.proceed(request);
            };

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }
}
