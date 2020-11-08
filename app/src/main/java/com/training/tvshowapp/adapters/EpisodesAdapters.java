package com.training.tvshowapp.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.training.tvshowapp.R;
import com.training.tvshowapp.databinding.ItemEpisodesBinding;
import com.training.tvshowapp.models.Episode;

import java.util.List;

public class EpisodesAdapters extends RecyclerView.Adapter<EpisodesAdapters.EpisodesViewHolder> {

    private List<Episode> episodes;
    private LayoutInflater layoutInflater;

    public EpisodesAdapters(List<Episode> episodes) {
        this.episodes = episodes;
    }

    @NonNull
    @Override
    public EpisodesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }

        ItemEpisodesBinding itemEpisodesBinding = DataBindingUtil.inflate(layoutInflater, R.layout.item_episodes, parent, false);

        return new EpisodesViewHolder(itemEpisodesBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull EpisodesViewHolder holder, int position) {
        holder.bindEpisodes(episodes.get(position));
    }

    @Override
    public int getItemCount() {
        return episodes.size();
    }

    static class EpisodesViewHolder extends RecyclerView.ViewHolder {

        private ItemEpisodesBinding itemEpisodesBinding;

        public EpisodesViewHolder(ItemEpisodesBinding itemEpisodesBinding) {
            super(itemEpisodesBinding.getRoot());
            this.itemEpisodesBinding = itemEpisodesBinding;
        }

        public void bindEpisodes(Episode episode) {
            String title = "";
            String season = "S" + (episode.getSeason().length() == 1 ? "0".concat(episode.getSeason()) : episode.getSeason());
            String episodeNumber = "E" + (episode.getEpisode().length() == 1 ? "0".concat(episode.getEpisode()) : episode.getEpisode());

            itemEpisodesBinding.setTitle(title.concat(season).concat(episodeNumber));
            itemEpisodesBinding.setName(episode.getName());
            itemEpisodesBinding.setAirDate(episode.getAirDate());
        }
    }
}
