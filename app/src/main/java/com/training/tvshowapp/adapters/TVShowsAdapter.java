package com.training.tvshowapp.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.training.tvshowapp.R;
import com.training.tvshowapp.databinding.ItemTvShowBinding;
import com.training.tvshowapp.listeners.TVShowListener;
import com.training.tvshowapp.models.TVShow;

import java.util.List;

public class TVShowsAdapter extends RecyclerView.Adapter<TVShowsAdapter.TVShowsViewHolder> {

    private List<TVShow> tvShows;
    private LayoutInflater layoutInflater;
    private TVShowListener tvShowListener;

    public TVShowsAdapter(List<TVShow> tvShows, TVShowListener tvShowListener) {
        this.tvShows = tvShows;
        this.tvShowListener = tvShowListener;
    }

    @NonNull
    @Override
    public TVShowsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }

        ItemTvShowBinding tvShowBinding = DataBindingUtil.inflate(layoutInflater, R.layout.item_tv_show, parent, false);

        return new TVShowsViewHolder(tvShowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull TVShowsViewHolder holder, int position) {
        holder.bindTVShow(tvShows.get(position));
    }

    @Override
    public int getItemCount() {
        return tvShows.size();
    }

    class TVShowsViewHolder extends RecyclerView.ViewHolder {

        private ItemTvShowBinding itemTvShowBinding;

        public TVShowsViewHolder(ItemTvShowBinding itemTvShowBinding) {
            super(itemTvShowBinding.getRoot());
            this.itemTvShowBinding = itemTvShowBinding;
        }

        public void bindTVShow(TVShow tvShow) {
            itemTvShowBinding.setTvShow(tvShow);
            itemTvShowBinding.executePendingBindings();
            itemTvShowBinding.getRoot().setOnClickListener(v -> tvShowListener.onTVShowItemClicked(tvShow, getAdapterPosition()));
        }
    }
}
