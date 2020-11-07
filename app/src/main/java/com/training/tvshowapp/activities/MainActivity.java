package com.training.tvshowapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.training.tvshowapp.R;
import com.training.tvshowapp.adapters.TVShowsAdapter;
import com.training.tvshowapp.databinding.ActivityMainBinding;
import com.training.tvshowapp.models.TVShow;
import com.training.tvshowapp.viewmodels.MostPopularTVShowsViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private MostPopularTVShowsViewModel viewModel;
    private ActivityMainBinding activityMainBinding;
    private List<TVShow> tvShows = new ArrayList<>();
    private TVShowsAdapter tvShowsAdapter;
    private int currentPage = 1;
    private int totalAvailablePage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        doInitialization();
    }

    private void doInitialization() {
        viewModel = new ViewModelProvider(this).get(MostPopularTVShowsViewModel.class);

        tvShowsAdapter = new TVShowsAdapter(tvShows, (tvShow, position) -> {
            Intent intent = new Intent(getApplicationContext(), TVShowDetailsActivity.class);
            intent.putExtra("tvShow", tvShow);

            startActivity(intent);
        });

        activityMainBinding.rvTvShowList.setHasFixedSize(true);
        activityMainBinding.rvTvShowList.setAdapter(tvShowsAdapter);
        activityMainBinding.rvTvShowList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!activityMainBinding.rvTvShowList.canScrollVertically(1)) {
                    if (currentPage <= totalAvailablePage) {
                        currentPage++;
                        getMostPopularTVShows();

                        Log.d(TAG, "End of recyclerview!");
                    }
                }
            }
        });

        getMostPopularTVShows();
    }

    private void getMostPopularTVShows() {
        toggleLoading();

        viewModel.getMostPopularTVShows(currentPage).observe(this, tvShowResponse -> {
            toggleLoading();
            if (tvShowResponse != null) {
                totalAvailablePage = tvShowResponse.getTotalPages();
                if (tvShowResponse.getTvShows() != null) {
                    int oldCount = tvShows.size();

                    tvShows.addAll(tvShowResponse.getTvShows());
                    tvShowsAdapter.notifyItemRangeInserted(oldCount, tvShows.size());
                }

                Log.d(TAG, "Delivered result!");
            } else {
                Log.d(TAG, "Something went wrong!");
            }
        });
    }

    private void toggleLoading() {
        if (currentPage == 1) {
            if (activityMainBinding.getIsLoading() != null && activityMainBinding.getIsLoading()) {
                activityMainBinding.setIsLoading(false);
            } else {
                activityMainBinding.setIsLoading(true);
            }
        } else {
            if (activityMainBinding.getIsLoadingMore() != null && activityMainBinding.getIsLoadingMore()) {
                activityMainBinding.setIsLoadingMore(false);
            } else {
                activityMainBinding.setIsLoadingMore(true);
            }
        }
    }
}