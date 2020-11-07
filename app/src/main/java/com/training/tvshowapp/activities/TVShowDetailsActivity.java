package com.training.tvshowapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.training.tvshowapp.R;
import com.training.tvshowapp.adapters.ImageSliderAdapter;
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
                    if (tvShowDetailsResponse.getTvShowDetails() != null) {
                        if (tvShowDetailsResponse.getTvShowDetails().getPictures() != null) {
                            loadImageSlider(tvShowDetailsResponse.getTvShowDetails().getPictures());
                        }
                    }
                }
            });
        }
    }

    private void loadImageSlider(String[] sliderImages) {
        activityTvShowDetailsBinding.vpImageSlider.setOffscreenPageLimit(1);
        activityTvShowDetailsBinding.vpImageSlider.setAdapter(new ImageSliderAdapter(sliderImages));
        activityTvShowDetailsBinding.vpImageSlider.setVisibility(View.VISIBLE);

        setupSliderIndactors(sliderImages.length);

        activityTvShowDetailsBinding.vpImageSlider.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentSliderIndicator(position);
            }
        });
    }

    private void setupSliderIndactors(int count) {
        ImageView[] indicators = new ImageView[count];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(8, 0, 8, 0);
        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(getApplicationContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bg_slider_indicator_inactive));
            indicators[i].setLayoutParams(layoutParams);
            activityTvShowDetailsBinding.vpContainerImageSliderIndicator.addView(indicators[i]);
        }
        activityTvShowDetailsBinding.vpContainerImageSliderIndicator.setVisibility(View.VISIBLE);

        setCurrentSliderIndicator(0);
    }

    private void setCurrentSliderIndicator(int position) {
        int childCount = activityTvShowDetailsBinding.vpContainerImageSliderIndicator.getChildCount();

        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) activityTvShowDetailsBinding.vpContainerImageSliderIndicator.getChildAt(i);
            if (i == position) {
                imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bg_slider_indicator_active));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bg_slider_indicator_inactive));
            }
        }
    }
}