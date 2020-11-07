package com.training.tvshowapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import com.training.tvshowapp.R;
import com.training.tvshowapp.adapters.TVShowsAdapter;
import com.training.tvshowapp.databinding.ActivityMainBinding;
import com.training.tvshowapp.models.TVShow;
import com.training.tvshowapp.viewmodels.MostPopularTVShowsViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MostPopularTVShowsViewModel viewModel;
    private ActivityMainBinding activityMainBinding;
    private List<TVShow> tvShows = new ArrayList<>();
    private TVShowsAdapter tvShowsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        doInitialization();
    }

    private void doInitialization() {
        viewModel = new ViewModelProvider(this).get(MostPopularTVShowsViewModel.class);

        tvShowsAdapter = new TVShowsAdapter(tvShows);
        activityMainBinding.rvTvShowList.setHasFixedSize(true);
        activityMainBinding.rvTvShowList.setAdapter(tvShowsAdapter);

        getMostPopularTVShows();
    }

    private void getMostPopularTVShows() {
        activityMainBinding.setIsLoading(true);

        viewModel.getMostPopularTVShows(0).observe(this, tvShowResponse -> {
            activityMainBinding.setIsLoading(false);
            if (tvShowResponse != null) {
                if (tvShowResponse.getTvShows() != null) {
                    tvShows.addAll(tvShowResponse.getTvShows());
                    tvShowsAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}