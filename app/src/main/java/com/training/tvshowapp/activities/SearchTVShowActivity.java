package com.training.tvshowapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import com.training.tvshowapp.R;
import com.training.tvshowapp.adapters.TVShowsAdapter;
import com.training.tvshowapp.databinding.ActivitySearchTvShowBinding;
import com.training.tvshowapp.listeners.TVShowListener;
import com.training.tvshowapp.models.TVShow;
import com.training.tvshowapp.viewmodels.SearchTVShowViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SearchTVShowActivity extends AppCompatActivity {

    private static final String TAG = SearchTVShowActivity.class.getSimpleName();

    private ActivitySearchTvShowBinding activitySearchTvShowBinding;
    private SearchTVShowViewModel viewModel;
    private List<TVShow> tvShows = new ArrayList<>();
    private TVShowsAdapter tvShowsAdapter;
    private int prevPage = 1;
    private int nextPage = prevPage;
    private int totalAvailablePage = prevPage;
    private Timer timerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySearchTvShowBinding = DataBindingUtil.setContentView(this, R.layout.activity_search_tv_show);
        doInitialization();
    }

    private void doInitialization() {
        viewModel = new ViewModelProvider(this).get(SearchTVShowViewModel.class);

        activitySearchTvShowBinding.ivBackButton.setOnClickListener(v -> onBackPressed());
        tvShowsAdapter = new TVShowsAdapter(tvShows, new TVShowListener() {
            @Override
            public void onTVShowItemClicked(TVShow tvShow, int position) {
                Intent intent = new Intent(getApplicationContext(), TVShowDetailsActivity.class);
                intent.putExtra("tvShow", tvShow);

                startActivity(intent);
            }
        });
        activitySearchTvShowBinding.rvTVShowSearch.setHasFixedSize(true);
        activitySearchTvShowBinding.rvTVShowSearch.setAdapter(tvShowsAdapter);

        activitySearchTvShowBinding.etInputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (timerTask != null) {
                    timerTask.cancel();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().trim().isEmpty()) {
                    timerTask = new Timer();
                    timerTask.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            new Handler(Looper.getMainLooper()).post(() -> {
                                prevPage = 1;
                                nextPage = prevPage;
                                totalAvailablePage = prevPage;
                                searchTVShow(s.toString(), prevPage);
                            });
                        }
                    }, 800);
                } else {
                    tvShows.clear();
                    tvShowsAdapter.notifyDataSetChanged();

                    prevPage = 1;
                    nextPage = prevPage;
                    totalAvailablePage = prevPage;
                }
            }
        });

        activitySearchTvShowBinding.rvTVShowSearch.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!activitySearchTvShowBinding.rvTVShowSearch.canScrollVertically(1)) {
                    if (!activitySearchTvShowBinding.etInputSearch.getText().toString().isEmpty()) {
                        if (prevPage <= totalAvailablePage) {
                            searchTVShow(activitySearchTvShowBinding.etInputSearch.getText().toString(), nextPage);
                            Log.d(TAG, "Pagination: End of page!");
                        }
                    }
                }

                if (activitySearchTvShowBinding.rvTVShowSearch.canScrollVertically(-1)) {
                    activitySearchTvShowBinding.setNetWorkStateError(false);
                }
            }
        });

        activitySearchTvShowBinding.etInputSearch.requestFocus();
    }

    private void searchTVShow(String query, int page) {
        toggleLoading();
        activitySearchTvShowBinding.setNetWorkStateError(false);

        viewModel.searchTVShow(query, page).observe(this, tvShowResponse -> {
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
                activitySearchTvShowBinding.setNetWorkStateError(true);
                retryRequest();
                Log.d(TAG, "Something went wrong!");
            }
        });
    }

    private void toggleLoading() {
        if (prevPage == 1) {
            if (activitySearchTvShowBinding.getIsLoading() != null && activitySearchTvShowBinding.getIsLoading()) {
                activitySearchTvShowBinding.setIsLoading(false);
            } else {
                activitySearchTvShowBinding.setIsLoading(true);
            }
        } else {
            if (activitySearchTvShowBinding.getIsLoadingMore() != null && activitySearchTvShowBinding.getIsLoadingMore()) {
                activitySearchTvShowBinding.setIsLoadingMore(false);
            } else {
                activitySearchTvShowBinding.setIsLoadingMore(true);
            }
        }
    }

    private void retryRequest() {
        activitySearchTvShowBinding.btnRetryNetwork.setOnClickListener(v -> searchTVShow(activitySearchTvShowBinding.etInputSearch.getText().toString(), nextPage));
    }
}