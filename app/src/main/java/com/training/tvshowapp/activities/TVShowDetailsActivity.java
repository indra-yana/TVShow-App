package com.training.tvshowapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.training.tvshowapp.R;
import com.training.tvshowapp.adapters.EpisodesAdapters;
import com.training.tvshowapp.adapters.ImageSliderAdapter;
import com.training.tvshowapp.databinding.ActivityTvShowDetailsBinding;
import com.training.tvshowapp.databinding.LayoutEpisodesBottomSheetBinding;
import com.training.tvshowapp.models.TVShow;
import com.training.tvshowapp.responses.TVShowDetailsResponse;
import com.training.tvshowapp.viewmodels.TVShowDetailsViewModel;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

public class TVShowDetailsActivity extends AppCompatActivity {

    private static final String TAG = TVShowDetailsActivity.class.getSimpleName();

    private ActivityTvShowDetailsBinding activityTvShowDetailsBinding;
    private TVShowDetailsViewModel viewModel;
    private BottomSheetDialog episodesBottomSheetDialog;
    private LayoutEpisodesBottomSheetBinding layoutEpisodesBottomSheetBinding;
    private TVShow tvShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityTvShowDetailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_tv_show_details);
        doInitialization();
    }

    private void doInitialization() {
        viewModel = new ViewModelProvider(this).get(TVShowDetailsViewModel.class);
        tvShow = (TVShow) getIntent().getSerializableExtra("tvShow");

        getTVShowDetails();

        activityTvShowDetailsBinding.ivBackButton.setOnClickListener(v -> onBackPressed());
    }

    private void getTVShowDetails() {
        // Show the loading indicator
        activityTvShowDetailsBinding.setIsLoading(true);
        activityTvShowDetailsBinding.setNetWorkStateError(false);

        if (tvShow != null) {
            // Load TV Shows basic info
            loadBasicTVShowDetails();

            // Register and observe the view model
            viewModel.getTVShowDetails(String.valueOf(tvShow.getId())).observe(this, tvShowDetailsResponse -> {
                // Hide the loading indicator when return the response
                activityTvShowDetailsBinding.setIsLoading(false);

                if (tvShowDetailsResponse != null) {
                    if (tvShowDetailsResponse.getTvShowDetails() != null) {

                        // Setup image slider
                        if (tvShowDetailsResponse.getTvShowDetails().getPictures() != null) {
                            loadImageSlider(tvShowDetailsResponse.getTvShowDetails().getPictures());
                        }

                        // Setup description and other TV Shows primary info
                        activityTvShowDetailsBinding.setDescription(String.valueOf(HtmlCompat.fromHtml(tvShowDetailsResponse.getTvShowDetails().getDescription(), HtmlCompat.FROM_HTML_MODE_LEGACY)));
                        activityTvShowDetailsBinding.setRating(String.format(Locale.getDefault(), "%.2f", Double.parseDouble(tvShowDetailsResponse.getTvShowDetails().getRating())));
                        activityTvShowDetailsBinding.setRuntime(tvShowDetailsResponse.getTvShowDetails().getRuntime() + " Min");
                        if (tvShowDetailsResponse.getTvShowDetails().getGenres() != null) {
                            String genre = Arrays.stream(tvShowDetailsResponse.getTvShowDetails().getGenres()).collect(Collectors.joining(" | "));
                            activityTvShowDetailsBinding.setGenre(genre);
                        } else {
                            activityTvShowDetailsBinding.setGenre("N/A");
                        }

                        activityTvShowDetailsBinding.viewDivider1.setVisibility(View.VISIBLE);
                        activityTvShowDetailsBinding.viewDivider2.setVisibility(View.VISIBLE);
                        activityTvShowDetailsBinding.layoutMisc.setVisibility(View.VISIBLE);
                        activityTvShowDetailsBinding.layoutGenres.setVisibility(View.VISIBLE);

                        // Setup Read More text action
                        activityTvShowDetailsBinding.tvReadMore.setVisibility(View.VISIBLE);
                        activityTvShowDetailsBinding.tvReadMore.setOnClickListener(v -> {
                            if (activityTvShowDetailsBinding.tvReadMore.getText().toString().equals("Read More")) {
                                activityTvShowDetailsBinding.tvDescription.setMaxLines(Integer.MAX_VALUE);
                                activityTvShowDetailsBinding.tvDescription.setEllipsize(null);
                                activityTvShowDetailsBinding.tvReadMore.setText(R.string.text_read_less);
                            } else {
                                activityTvShowDetailsBinding.tvDescription.setMaxLines(4);
                                activityTvShowDetailsBinding.tvDescription.setEllipsize(TextUtils.TruncateAt.END);
                                activityTvShowDetailsBinding.tvReadMore.setText(R.string.text_read_more);
                            }
                        });

                        // Setup button goto website
                        activityTvShowDetailsBinding.btnGoToWebsite.setVisibility(View.VISIBLE);
                        activityTvShowDetailsBinding.btnGoToWebsite.setOnClickListener(v -> {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(tvShowDetailsResponse.getTvShowDetails().getUrl()));
                            startActivity(intent);
                        });

                        // Setup button episodes
                        activityTvShowDetailsBinding.btnGoToEpisodes.setVisibility(View.VISIBLE);
                        activityTvShowDetailsBinding.btnGoToEpisodes.setOnClickListener(v -> openEpisodesBottomSheet(tvShowDetailsResponse, tvShow));
                    }
                } else {
                    activityTvShowDetailsBinding.setNetWorkStateError(true);
                    retryRequest();
                    Log.d(TAG, "Something went wrong!");
                }
            });
        }
    }

    private void loadImageSlider(String[] sliderImages) {
        activityTvShowDetailsBinding.vpImageSlider.setOffscreenPageLimit(1);
        activityTvShowDetailsBinding.vpImageSlider.setAdapter(new ImageSliderAdapter(sliderImages));
        activityTvShowDetailsBinding.vpImageSlider.setVisibility(View.VISIBLE);

        setupSliderIndicators(sliderImages.length);

        activityTvShowDetailsBinding.vpImageSlider.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentSliderIndicator(position);
            }
        });
    }

    private void setupSliderIndicators(int count) {
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

    private void loadBasicTVShowDetails() {
        activityTvShowDetailsBinding.setTvShow(tvShow);
    }

    private void openEpisodesBottomSheet(TVShowDetailsResponse tvShowDetailsResponse, TVShow tvShow) {
        if (episodesBottomSheetDialog == null) {
            // Setup layoutEpisodesBottomSheetBinding
            layoutEpisodesBottomSheetBinding = DataBindingUtil.inflate(LayoutInflater.from(TVShowDetailsActivity.this), R.layout.layout_episodes_bottom_sheet, findViewById(R.id.episodesContainer), false);
            layoutEpisodesBottomSheetBinding.rvTVShowEpisodes.setAdapter(new EpisodesAdapters(tvShowDetailsResponse.getTvShowDetails().getEpisodes()));
            layoutEpisodesBottomSheetBinding.tvTitle.setText(String.format("Episodes | %s", tvShow.getName()));
            layoutEpisodesBottomSheetBinding.ivActionClose.setOnClickListener(v -> episodesBottomSheetDialog.dismiss());

            // Setup bottom sheet dialog
            episodesBottomSheetDialog = new BottomSheetDialog(TVShowDetailsActivity.this);
            episodesBottomSheetDialog.setContentView(layoutEpisodesBottomSheetBinding.getRoot());
        }

        /* Optional Section Start */
        /*
        FrameLayout frameLayout = episodesBottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (frameLayout != null) {
            BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(frameLayout);
            bottomSheetBehavior.setPeekHeight(Resources.getSystem().getDisplayMetrics().heightPixels);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
        }
        */
        /* Optional Section End */

        // Show the bottom sheet dialog
        episodesBottomSheetDialog.show();
    }

    private void retryRequest() {
        activityTvShowDetailsBinding.btnRetryNetwork.setOnClickListener(v -> getTVShowDetails());
    }
}