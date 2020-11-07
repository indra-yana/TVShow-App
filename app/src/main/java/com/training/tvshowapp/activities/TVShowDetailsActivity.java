package com.training.tvshowapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.widget.Toast;

import com.training.tvshowapp.R;
import com.training.tvshowapp.databinding.ActivityTvShowDetailsBinding;
import com.training.tvshowapp.models.TVShow;
import com.training.tvshowapp.viewmodels.TVShowDetailsViewModel;

public class TVShowDetailsActivity extends AppCompatActivity {

    private ActivityTvShowDetailsBinding activityTvShowDetailsBinding;
    private TVShowDetailsViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityTvShowDetailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_tv_show_details);
        doInitialization();
    }

    private void doInitialization() {
        viewModel = new ViewModelProvider(this).get(TVShowDetailsViewModel.class);
        getTVShowDetails();
    }

    private void getTVShowDetails() {
        activityTvShowDetailsBinding.setIsLoading(true);

        TVShow tvShow = (TVShow) getIntent().getSerializableExtra("tvShow");
        if (tvShow != null) {
            viewModel.getTVShowDetails(String.valueOf(tvShow.getId())).observe(this, tvShowDetailsResponse -> {
                activityTvShowDetailsBinding.setIsLoading(false);
                if (tvShowDetailsResponse != null) {
                    Toast.makeText(TVShowDetailsActivity.this, tvShowDetailsResponse.getTvShowDetails().getUrl(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}