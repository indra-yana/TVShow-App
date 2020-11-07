package com.training.tvshowapp.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.training.tvshowapp.R;
import com.training.tvshowapp.databinding.ItemSliderBinding;

public class ImageSliderAdapter extends RecyclerView.Adapter<ImageSliderAdapter.ImageSliderViewHolder> {

    private String[] sliderImages;
    private LayoutInflater layoutInflater;

    public ImageSliderAdapter(String[] sliderImages) {
        this.sliderImages = sliderImages;
    }

    @NonNull
    @Override
    public ImageSliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }

        ItemSliderBinding itemSliderBinding = DataBindingUtil.inflate(layoutInflater, R.layout.item_slider, parent, false);

        return new ImageSliderViewHolder(itemSliderBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageSliderViewHolder holder, int position) {
        holder.bindSliderImage(sliderImages[position]);
    }

    @Override
    public int getItemCount() {
        return sliderImages.length;
    }

    static class ImageSliderViewHolder extends RecyclerView.ViewHolder {
        private ItemSliderBinding itemSliderBinding;

        public ImageSliderViewHolder(ItemSliderBinding itemSliderBinding) {
            super(itemSliderBinding.getRoot());
            this.itemSliderBinding = itemSliderBinding;
        }

        public void bindSliderImage(String imageURL) {
            itemSliderBinding.setImageURL(imageURL);
        }
    }
}
