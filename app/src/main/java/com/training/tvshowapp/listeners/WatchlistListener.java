package com.training.tvshowapp.listeners;

import com.training.tvshowapp.models.TVShow;

public interface WatchlistListener {
    void onTVShowItemClicked(TVShow tvShow, int position);
    void onRemoveTVShowFromWatchlist(TVShow tvShow, int position);
}
