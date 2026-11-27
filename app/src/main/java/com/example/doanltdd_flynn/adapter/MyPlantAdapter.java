package com.example.doanltdd_flynn.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.doanltdd_flynn.R;
import com.example.doanltdd_flynn.model.MyPlant;
import com.example.doanltdd_flynn.utils.ChuoiUtil;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class MyPlantAdapter
        extends RecyclerView.Adapter<MyPlantAdapter.MyPlantViewHolder> {

    public interface Listener {
        void onItemClick(MyPlant plant);
        void onQuickCareClick(MyPlant plant);
    }

    private final List<MyPlant> plantList = new ArrayList<>();
    private final Listener listener;

    public MyPlantAdapter(Listener listener) {
        this.listener = listener;
    }

    public void submitList(List<MyPlant> plants) {
        plantList.clear();

        if (plants != null) {
            plantList.addAll(plants);
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyPlantViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_my_plant, parent, false);

        return new MyPlantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull MyPlantViewHolder holder, int position) {

        MyPlant plant = plantList.get(position);

        // Hiển thị ảnh cây
        Glide.with(holder.itemView)
                .load(plant.getImageUrl())
                .placeholder(R.drawable.ic_plant)
                .error(R.drawable.ic_plant)
                .centerCrop()
                .into(holder.imgPlant);

        // Tên cây
        String plantName = plant.getPlantName();

        if (plantName == null || plantName.trim().isEmpty()) {
            plantName = "Cây chưa có tên";
        }

        holder.tvName.setText(plantName);

        // Ngày trồng và thời gian đồng hành
        String plantingDate = plant.getPlantingDate();

        holder.tvInfo.setText(
                "Trồng từ " + ChuoiUtil.display(plantingDate)
                        + " · "
                        + ChuoiUtil.calculateCompanionDays(plantingDate)
                        + " ngày đồng hành"
        );

        // Chuỗi chăm sóc
        List<String> careDays = plant.getCareDays();

        int currentStreak =
                ChuoiUtil.calculateCurrentStreak(careDays);

        holder.tvStreak.setText(currentStreak + " ngày");

        // Trạng thái chăm sóc
        String stateName = String.valueOf(
                ChuoiUtil.getStreakState(careDays)
        );

        int backgroundColor;
        int textColor;
        String statusText;

        if ("CARED_TODAY".equals(stateName)) {
            statusText = "Đã chăm hôm nay";
            backgroundColor = R.color.flynn_green_light;
            textColor = R.color.flynn_green_dark;

        } else if ("WAITING".equals(stateName)) {
            statusText = "Chờ chăm sóc";
            backgroundColor = R.color.flynn_orange_light;
            textColor = R.color.flynn_orange;

        } else {
            statusText = "Chưa có chuỗi";
            backgroundColor = R.color.flynn_grey_light;
            textColor = R.color.flynn_grey;
        }

        holder.tvStatus.setText(statusText);

        holder.tvStatus.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(
                        ContextCompat.getColor(
                                holder.itemView.getContext(),
                                backgroundColor
                        )
                )
        );

        holder.tvStatus.setTextColor(
                ContextCompat.getColor(
                        holder.itemView.getContext(),
                        textColor
                )
        );

        // Kiểm tra cây đã thu hoạch hay chưa
        boolean isHarvested =
                "DA_THU_HOACH".equalsIgnoreCase(
                        plant.getStatus() == null
                                ? ""
                                : plant.getStatus().trim()
                );

        holder.btnQuickCare.setEnabled(!isHarvested);
        holder.btnQuickCare.setAlpha(
                isHarvested ? 0.5f : 1.0f
        );

        // Nhấn vào thẻ cây để xem chi tiết
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(plant);
            }
        });

        // Nhấn nút chăm nhanh
        holder.btnQuickCare.setOnClickListener(v -> {
            if (listener != null && !isHarvested) {
                listener.onQuickCareClick(plant);
            }
        });
    }

    @Override
    public int getItemCount() {
        return plantList.size();
    }

    static class MyPlantViewHolder
            extends RecyclerView.ViewHolder {

        ImageView imgPlant;
        TextView tvName;
        TextView tvInfo;
        TextView tvStreak;
        TextView tvStatus;
        MaterialButton btnQuickCare;

        public MyPlantViewHolder(@NonNull View itemView) {
            super(itemView);

            imgPlant = itemView.findViewById(R.id.imgPlant);
            tvName = itemView.findViewById(R.id.tvName);
            tvInfo = itemView.findViewById(R.id.tvInfo);
            tvStreak = itemView.findViewById(R.id.tvStreak);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnQuickCare = itemView.findViewById(R.id.btnQuickCare);
        }
    }
}
