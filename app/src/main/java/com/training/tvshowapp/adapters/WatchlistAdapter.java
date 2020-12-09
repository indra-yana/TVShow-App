package com.training.tvshowapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.training.tvshowapp.R;
import com.training.tvshowapp.databinding.ItemTvShowBinding;
import com.training.tvshowapp.listeners.WatchlistListener;
import com.training.tvshowapp.models.TVShow;

import java.util.List;

public class WatchlistAdapter extends RecyclerView.Adapter<WatchlistAdapter.WatchlistViewHolder> {

    private List<TVShow> tvShows;
    private LayoutInflater layoutInflater;
    private WatchlistListener watchlistListener;

    public WatchlistAdapter(List<TVShow> tvShows, WatchlistListener watchlistListener) {
        this.tvShows = tvShows;
        this.watchlistListener = watchlistListener;
    }

    @NonNull
    @Override
    public WatchlistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }

        ItemTvShowBinding tvShowBinding = DataBindingUtil.inflate(layoutInflater, R.layout.item_tv_show, parent, false);

        return new WatchlistViewHolder(tvShowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull WatchlistViewHolder holder, int position) {
        holder.bindTVShow(tvShows.get(position));
    }

    @Override
    public int getItemCount() {
        return tvShows.size();
    }

    class WatchlistViewHolder extends RecyclerView.ViewHolder {

        private ItemTvShowBinding itemTvShowBinding;

        public WatchlistViewHolder(ItemTvShowBinding itemTvShowBinding) {
            super(itemTvShowBinding.getRoot());
            this.itemTvShowBinding = itemTvShowBinding;
        }

        public void bindTVShow(TVShow tvShow) {
            itemTvShowBinding.setTvShow(tvShow);
            itemTvShowBinding.executePendingBindings();
            itemTvShowBinding.getRoot().setOnClickListener(v -> watchlistListener.onTVShowItemClicked(tvShow, getAdapterPosition()));
            itemTvShowBinding.ivRemoveWatchlist.setOnClickListener(v -> watchlistListener.onRemoveTVShowFromWatchlist(tvShow, getAdapterPosition()));
            itemTvShowBinding.ivRemoveWatchlist.setVisibility(View.VISIBLE);
        }
    }
}
