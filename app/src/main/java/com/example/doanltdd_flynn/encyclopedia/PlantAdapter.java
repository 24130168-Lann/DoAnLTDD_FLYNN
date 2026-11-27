package com.example.doanltdd_flynn.encyclopedia;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.doanltdd_flynn.R;

import java.util.ArrayList;
import java.util.List;

public class PlantAdapter extends RecyclerView.Adapter<PlantAdapter.PlantViewHolder> {

    public interface OnPlantClickListener {
        void onPlantClick(@NonNull Plant plant);
    }

    private final List<Plant> items = new ArrayList<>();
    private final OnPlantClickListener listener;

    public PlantAdapter(@NonNull OnPlantClickListener listener) {
        this.listener = listener;
    }

    public void setItems(@NonNull List<Plant> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_plant_card, parent, false);
        return new PlantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlantViewHolder holder, int position) {
        Plant plant = items.get(position);
        Context context = holder.itemView.getContext();

        String category = safe(plant.getNhomCay(), "BÁCH KHOA");
        holder.tvCategory.setText(category.toUpperCase());
        holder.tvCategory.setTextColor(parseColorOrDefault(plant.getMauChuDao()));

        holder.tvCommonName.setText(safe(plant.getTenTiengViet(), "Chưa có tên"));
        holder.tvScientificName.setText(safe(plant.getTenKhoaHoc(), ""));
        holder.tvQuote.setText(safe(plant.getCauDanNgan(), safe(plant.getGioiThieu(), "")));

        String listImageUrl = plant.getAnhGioiThieu();
        if (listImageUrl == null || listImageUrl.trim().isEmpty()) {
            listImageUrl = plant.getAnhChinh();
        }
        loadImage(context, holder.ivPlant, listImageUrl);

        holder.itemView.setOnClickListener(v -> listener.onPlantClick(plant));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private void loadImage(@NonNull Context context, @NonNull ImageView targetView, @Nullable String imageUrl) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            targetView.setImageDrawable(null);
            return;
        }

        String value = imageUrl.trim();
        if (value.startsWith("http://") || value.startsWith("https://")) {
            Glide.with(context)
                    .load(value)
                    .centerCrop()
                    .into(targetView);
            return;
        }

        int resId = context.getResources().getIdentifier(value, "drawable", context.getPackageName());
        if (resId != 0) {
            Glide.with(context)
                    .load(resId)
                    .centerCrop()
                    .into(targetView);
        } else {
            targetView.setImageDrawable(null);
        }
    }

    private int parseColorOrDefault(@Nullable String hex) {
        if (hex == null || hex.trim().isEmpty()) {
            return Color.parseColor("#5B3A52");
        }
        try {
            String value = hex.trim();
            if (!value.startsWith("#")) {
                value = "#" + value;
            }
            return Color.parseColor(value);
        } catch (IllegalArgumentException e) {
            return Color.parseColor("#5B3A52");
        }
    }

    @NonNull
    private String safe(@Nullable String value, @NonNull String fallback) {
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }
        return value.trim();
    }

    static class PlantViewHolder extends RecyclerView.ViewHolder {
        final ImageView ivPlant;
        final TextView tvCategory;
        final TextView tvCommonName;
        final TextView tvScientificName;
        final TextView tvQuote;

        PlantViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPlant = itemView.findViewById(R.id.ivItemPlant);
            tvCategory = itemView.findViewById(R.id.tvItemCategory);
            tvCommonName = itemView.findViewById(R.id.tvItemCommonName);
            tvScientificName = itemView.findViewById(R.id.tvItemScientificName);
            tvQuote = itemView.findViewById(R.id.tvItemQuote);
        }
    }
}