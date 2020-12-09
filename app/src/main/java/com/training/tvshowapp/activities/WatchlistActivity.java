package com.training.tvshowapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.widget.Toast;

import com.training.tvshowapp.R;
import com.training.tvshowapp.databinding.ActivityWatchlistBinding;
import com.training.tvshowapp.viewmodels.WatchlistViewModel;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class WatchlistActivity extends AppCompatActivity {

    private ActivityWatchlistBinding activityWatchlistBinding;
    private WatchlistViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityWatchlistBinding = DataBindingUtil.setContentView(this, R.layout.activity_watchlist);

        doInitialization();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadWatchlist();
    }

    private void doInitialization() {
        viewModel = new ViewModelProvider(this).get(WatchlistViewModel.class);
        activityWatchlistBinding.ivBackButton.setOnClickListener(v -> onBackPressed());
    }

    private void loadWatchlist() {
        activityWatchlistBinding.setIsLoading(true);

        Disposable disposable = viewModel.loadWatchlist()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tvShows -> {
                    activityWatchlistBinding.setIsLoading(false);
                    Toast.makeText(WatchlistActivity.this, "Watchlist: " +tvShows.size(), Toast.LENGTH_SHORT).show();
                });

        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(disposable);
    }

}