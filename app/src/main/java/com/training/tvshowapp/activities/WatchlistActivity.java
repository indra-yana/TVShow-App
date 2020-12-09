package com.training.tvshowapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import com.training.tvshowapp.R;
import com.training.tvshowapp.adapters.WatchlistAdapter;
import com.training.tvshowapp.databinding.ActivityWatchlistBinding;
import com.training.tvshowapp.listeners.WatchlistListener;
import com.training.tvshowapp.models.TVShow;
import com.training.tvshowapp.utilities.TempDataHolder;
import com.training.tvshowapp.viewmodels.WatchlistViewModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class WatchlistActivity extends AppCompatActivity {

    private ActivityWatchlistBinding activityWatchlistBinding;
    private WatchlistViewModel viewModel;
    private WatchlistAdapter watchlistAdapter;
    private List<TVShow> watchList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityWatchlistBinding = DataBindingUtil.setContentView(this, R.layout.activity_watchlist);

        doInitialization();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (TempDataHolder.IS_WATCHLIST_UPDATED) {
            loadWatchlist();
            TempDataHolder.IS_WATCHLIST_UPDATED = false;
        }
    }

    private void doInitialization() {
        viewModel = new ViewModelProvider(this).get(WatchlistViewModel.class);
        activityWatchlistBinding.ivBackButton.setOnClickListener(v -> onBackPressed());
        watchList = new ArrayList<>();
        loadWatchlist();
    }

    private void loadWatchlist() {
        activityWatchlistBinding.setIsLoading(true);
        CompositeDisposable compositeDisposable = new CompositeDisposable();

        Disposable disposable = viewModel.loadWatchlist()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tvShows -> {
                    activityWatchlistBinding.setIsLoading(false);

                    if (watchList.size() > 0) watchList.clear();

                    watchList.addAll(tvShows);
                    watchlistAdapter = new WatchlistAdapter(watchList, new WatchlistListener() {
                        @Override
                        public void onTVShowItemClicked(TVShow tvShow, int position) {
                            Intent intent = new Intent(getApplicationContext(), TVShowDetailsActivity.class);
                            intent.putExtra("tvShow", tvShow);

                            startActivity(intent);
                        }

                        @Override
                        public void onRemoveTVShowFromWatchlist(TVShow tvShow, int position) {
                            removeTVShowFromWatchlist(tvShow, position);
                        }
                    });

                    activityWatchlistBinding.rvWatchlist.setAdapter(watchlistAdapter);
                    compositeDisposable.dispose();
                });

        compositeDisposable.add(disposable);
    }

    private void removeTVShowFromWatchlist(TVShow tvShow, int position) {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        Disposable disposable = viewModel.removeTVShowFromWatchlist(tvShow)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    watchList.remove(position);
                    watchlistAdapter.notifyItemRemoved(position);
                    watchlistAdapter.notifyItemRangeChanged(position, watchlistAdapter.getItemCount());
                    compositeDisposable.dispose();
                });

        compositeDisposable.add(disposable);
    }
}