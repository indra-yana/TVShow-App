package com.training.tvshowapp.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.training.tvshowapp.repositories.SearchTVShowRepository;
import com.training.tvshowapp.responses.TVShowResponse;

public class SearchTVShowViewModel extends ViewModel {

    private SearchTVShowRepository searchTVShowRepository;

    public SearchTVShowViewModel() {
        searchTVShowRepository = new SearchTVShowRepository();
    }

    public LiveData<TVShowResponse> searchTVShow(String query, int page) {
        return searchTVShowRepository.searchTVShow(query, page);
    }

}
