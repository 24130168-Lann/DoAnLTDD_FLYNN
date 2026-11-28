package com.example.doanltdd_flynn.adapter;

import android.content.res.ColorStateList;
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
import java.util.Locale;

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

        // Trạng thái phát triển của cây
        String plantStatus = getPlantStatusText(plant.getStatus());
        holder.tvStatus.setText(plantStatus);

        // Chuỗi chăm sóc
        List<String> careDays = plant.getCareDays();

        int currentStreak =
                ChuoiUtil.calculateCurrentStreak(careDays);

        holder.tvStreak.setText(currentStreak + " ngày");

        // Kiểm tra cây đã thu hoạch chưa
        boolean isHarvested =
                "DA_THU_HOACH".equalsIgnoreCase(
                        plant.getStatus() == null
                                ? ""
                                : plant.getStatus().trim()
                );

        // Kiểm tra hôm nay đã có nhật ký chăm sóc chưa
        String stateName = String.valueOf(
                ChuoiUtil.getStreakState(careDays)
        );

        boolean caredToday = "CARED_TODAY".equals(stateName);

        // Cập nhật nút chăm sóc
        if (isHarvested) {
            holder.btnQuickCare.setText("Đã thu hoạch");

        } else if (caredToday) {
            holder.btnQuickCare.setText("✓ Đã chăm sóc");

        } else {
            holder.btnQuickCare.setText("Chăm nhanh");
        }

        boolean canQuickCare = !isHarvested && !caredToday;

        holder.btnQuickCare.setEnabled(canQuickCare);
        holder.btnQuickCare.setAlpha(canQuickCare ? 1.0f : 0.7f);

        //Nhấn thẻ cây để xem chi tiết
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(plant);
            }
        });

        // Nhấn nút chăm nhanh
        holder.btnQuickCare.setOnClickListener(v -> {
            if (listener != null && canQuickCare) {
                listener.onQuickCareClick(plant);
            }
        });
    }

    // Chuyển mã trạng thái thành nội dung hiển thị
    private String getPlantStatusText(String status) {

        if (status == null || status.trim().isEmpty()) {
            return "Đang phát triển";
        }

        String value = status.trim().toUpperCase(Locale.ROOT);

        switch (value) {
            case "GIEO_HAT":
                return "Gieo hạt";

            case "NAY_MAM":
                return "Nảy mầm";

            case "DANG_TRONG":
            case "DANG_PHAT_TRIEN":
                return "Đang phát triển";

            case "TRUONG_THANH":
                return "Trưởng thành";

            case "DA_THU_HOACH":
                return "Đã thu hoạch";

            default:
                return "Đang phát triển";
        }
    }

    @Override
    public int getItemCount() {
        return plantList.size();
    }

    static class MyPlantViewHolder
            extends RecyclerView.ViewHolder {

        ImageView imgPlant;
        TextView tvName;
        TextView tvStatus;
        TextView tvStreak;
        MaterialButton btnQuickCare;

        public MyPlantViewHolder(@NonNull View itemView) {
            super(itemView);

            imgPlant = itemView.findViewById(R.id.imgPlant);
            tvName = itemView.findViewById(R.id.tvName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvStreak = itemView.findViewById(R.id.tvStreak);
            btnQuickCare = itemView.findViewById(R.id.btnQuickCare);
        }
    }
}
