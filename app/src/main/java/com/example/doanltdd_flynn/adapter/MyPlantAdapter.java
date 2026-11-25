package com.example.doanltdd_flynn.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doanltdd_flynn.R;
import com.example.doanltdd_flynn.model.MyPlant;
import com.example.doanltdd_flynn.model.StreakState;
import com.example.doanltdd_flynn.utils.ChuoiUtil;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;


public class MyPlantAdapter extends RecyclerView.Adapter<MyPlantAdapter.ViewHolder> {

    public interface Listener {
        /** Mở chi tiết cây. */
        void onItemClick(MyPlant plant);

        /** Nút chăm nhanh: mở màn hình ghi nhật ký của cây đó. */
        void onQuickCareClick(MyPlant plant);
    }

    private List<MyPlant> myPlants = new ArrayList<>();
    private final Listener listener;

    public MyPlantAdapter(Listener listener) {
        this.listener = listener;
    }

    public void submitList(List<MyPlant> plants) {
        this.myPlants = plants == null ? new ArrayList<>() : plants;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_plant, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        MyPlant p = myPlants.get(position);
        Context ctx = h.itemView.getContext();

        String name = p.getPlantName() == null ? "" : p.getPlantName();
        h.tvName.setText(name);
        h.tvAvatar.setText(name.isEmpty() ? "?" : name.substring(0, 1).toUpperCase());
        h.tvInfo.setText("Trồng từ " + ChuoiUtil.display(p.getPlantingDate())
                + " · " + ChuoiUtil.calculateCompanionDays(p.getPlantingDate()) + " ngày đồng hành");

        // Chuỗi và trạng thái luôn tính từ danh sách ngày chăm (v4, mục 2)
        h.tvStreak.setText(ChuoiUtil.calculateCurrentStreak(p.getCareDays()) + " ngày");
        StreakState state = ChuoiUtil.getStreakState(p.getCareDays());
        h.tvStatus.setText(state.getLabel());

        int bgColor;
        int textColor;
        switch (state) {
            case CARED_TODAY:
                bgColor = R.color.flynn_green_light;
                textColor = R.color.flynn_green_dark;
                break;
            case WAITING:
                bgColor = R.color.flynn_orange_light;
                textColor = R.color.flynn_orange;
                break;
            default:
                bgColor = R.color.flynn_grey_light;
                textColor = R.color.flynn_grey;
                break;
        }
        h.tvStatus.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ctx, bgColor)));
        h.tvStatus.setTextColor(ContextCompat.getColor(ctx, textColor));

        h.itemView.setOnClickListener(v -> listener.onItemClick(p));
        h.btnQuickCare.setOnClickListener(v -> listener.onQuickCareClick(p));
    }

    @Override
    public int getItemCount() {
        return myPlants.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvAvatar;
        final TextView tvName;
        final TextView tvInfo;
        final TextView tvStreak;
        final TextView tvStatus;
        final MaterialButton btnQuickCare;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAvatar = itemView.findViewById(R.id.tvAvatar);
            tvName = itemView.findViewById(R.id.tvName);
            tvInfo = itemView.findViewById(R.id.tvInfo);
            tvStreak = itemView.findViewById(R.id.tvStreak);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnQuickCare = itemView.findViewById(R.id.btnQuickCare);
        }
    }
}
