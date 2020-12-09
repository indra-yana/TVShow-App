package com.training.tvshowapp.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.training.tvshowapp.database.TVShowsDatabase;
import com.training.tvshowapp.models.TVShow;

import java.util.List;

import io.reactivex.Flowable;

public class WatchlistViewModel extends AndroidViewModel {

    private TVShowsDatabase tvShowsDatabase;

    public WatchlistViewModel(@NonNull Application application) {
        super(application);
        tvShowsDatabase = TVShowsDatabase.getTvShowsDatabase(application);
    }

    public Flowable<List<TVShow>> loadWatchlist() {
        return tvShowsDatabase.tvShowsDao().getWatchList();
    }
}
