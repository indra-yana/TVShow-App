package com.training.tvshowapp.repositories;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.training.tvshowapp.network.ApiClient;
import com.training.tvshowapp.network.ApiService;
import com.training.tvshowapp.responses.TVShowResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MostPopularTVShowsRepository {

    private static String TAG = MostPopularTVShowsRepository.class.getSimpleName();

    private ApiService apiService;

    public MostPopularTVShowsRepository() {
        this.apiService = ApiClient.getRetrofit().create(ApiService.class);
    }

    public LiveData<TVShowResponse> getMostPopularTVShows(int page) {
        MutableLiveData<TVShowResponse> data = new MutableLiveData<>();

        apiService.getMostPopularTVShows(page).enqueue(new Callback<TVShowResponse>() {
            @Override
            public void onResponse(@NonNull Call<TVShowResponse> call, @NonNull Response<TVShowResponse> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<TVShowResponse> call, @NonNull Throwable t) {
                data.setValue(null);
            }
        });

        return data;
    }

    public LiveData<TVShowResponse> getMostPopularTVShowsAsJsonString(int page) {
        MutableLiveData<TVShowResponse> data = new MutableLiveData<>();
        Gson gson = new Gson();

        apiService.getMostPopularTVShowsAsJsonString(page).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                TVShowResponse tvShowResponse = gson.fromJson(response.body(), TVShowResponse.class);

                data.setValue(tvShowResponse);
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                data.setValue(null);
            }
        });

        return data;
    }
}
