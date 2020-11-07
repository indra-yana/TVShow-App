package com.training.tvshowapp.listeners;

import com.training.tvshowapp.models.TVShow;

public interface TVShowListener {
    void onTVShowItemClicked(TVShow tvShow, int position);
}
