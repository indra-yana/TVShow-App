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
    private int prevPage = 1;
    private int nextPage = prevPage;
    private int totalAvailablePage = prevPage;

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
                    if (prevPage <= totalAvailablePage) {
                        getMostPopularTVShows(nextPage);
                        Log.d(TAG, "Pagination: End of page!");
                    }
                }

                if (activityMainBinding.rvTvShowList.canScrollVertically(-1)) {
                    activityMainBinding.setNetWorkStateError(false);
                }
            }
        });
        activityMainBinding.actionWatchlist.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), WatchlistActivity.class)));

        getMostPopularTVShows(prevPage);
    }

    private void getMostPopularTVShows(int page) {
        toggleLoading();
        activityMainBinding.setNetWorkStateError(false);

        viewModel.getMostPopularTVShows(page).observe(this, tvShowResponse -> {
            toggleLoading();
            if (tvShowResponse != null) {
                nextPage++;
                Log.d(TAG, "Pagination: PrevPage = " + prevPage);
                Log.d(TAG, "Pagination: NextPage = " + nextPage);

                prevPage = nextPage;

                totalAvailablePage = tvShowResponse.getTotalPages();
                Log.d(TAG, "Pagination: TotalAvailablePage = " + totalAvailablePage);

                if (tvShowResponse.getTvShows() != null) {
                    int oldCount = tvShows.size();

                    tvShows.addAll(tvShowResponse.getTvShows());
                    tvShowsAdapter.notifyItemRangeInserted(oldCount, tvShows.size());
                }
            } else {
                activityMainBinding.setNetWorkStateError(true);
                retryRequest();
                Log.d(TAG, "Something went wrong!");
            }
        });
    }

    private void toggleLoading() {
        if (prevPage == 1) {
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

    private void retryRequest() {
        activityMainBinding.btnRetryNetwork.setOnClickListener(v -> getMostPopularTVShows(nextPage));
    }
}