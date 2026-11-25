package com.example.doanltdd_flynn.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doanltdd_flynn.R;
import com.example.doanltdd_flynn.adapter.MyPlantAdapter;
import com.example.doanltdd_flynn.controller.MyPlantController;
import com.example.doanltdd_flynn.model.MyPlant;
import com.example.doanltdd_flynn.model.StreakSummary;
import com.example.doanltdd_flynn.utils.ChuoiUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Tab "Cây của tôi" (màn hình 4): thẻ tổng hợp chuỗi + danh sách cây, mỗi cây có chuỗi riêng
 * và nút chăm nhanh. Khách (chưa đăng nhập) không thấy chuỗi và không thêm được cây (A20).
 */
public class MyPlantsFragment extends Fragment {

    private MyPlantController myPlantController;
    private MyPlantAdapter adapter;

    private RecyclerView rvMyPlants;
    private View emptyView;
    private View summaryCard;
    private TextView tvEmpty;
    private TextView tvHighestStreak;
    private TextView tvHolding;
    private TextView tvRecord;
    private FloatingActionButton fabAdd;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_plants, container, false);

        rvMyPlants = view.findViewById(R.id.rvMyPlants);
        emptyView = view.findViewById(R.id.emptyView);
        summaryCard = view.findViewById(R.id.summaryCard);
        tvEmpty = view.findViewById(R.id.tvEmpty);
        tvHighestStreak = view.findViewById(R.id.tvHighestStreak);
        tvHolding = view.findViewById(R.id.tvHolding);
        tvRecord = view.findViewById(R.id.tvRecord);
        fabAdd = view.findViewById(R.id.fabAdd);

        myPlantController = new MyPlantController();

        adapter = new MyPlantAdapter(new MyPlantAdapter.Listener() {
            @Override
            public void onItemClick(MyPlant plant) {
                Intent i = new Intent(requireContext(), MyPlantDetailActivity.class);
                i.putExtra(MyPlantDetailActivity.EXTRA_PLANT_ID, plant.getMyPlantId());
                startActivity(i);
            }

            @Override
            public void onQuickCareClick(MyPlant plant) {
                // Chăm nhanh = mở màn hình ghi nhật ký của cây đó, ngày mặc định hôm nay (mục 5.1)
                Intent i = new Intent(requireContext(), AddCareLogActivity.class);
                i.putExtra(AddCareLogActivity.EXTRA_PLANT_ID, plant.getMyPlantId());
                startActivity(i);
            }
        });
        rvMyPlants.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvMyPlants.setAdapter(adapter);

        fabAdd.setOnClickListener(v -> startActivity(new Intent(requireContext(), AddMyPlantActivity.class)));
        return view;
    }

    // Nạp lại dữ liệu mỗi lần quay về màn hình (sau khi thêm cây / ghi nhật ký / xóa cây)
    @Override
    public void onResume() {
        super.onResume();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            showGuestState();
            return;
        }
        fabAdd.setVisibility(View.VISIBLE);
        summaryCard.setVisibility(View.VISIBLE);
        loadData(user.getUid());
    }

    private void loadData(String userId) {
        myPlantController.getMyPlants(userId, plants ->
                myPlantController.getMaxStreakRecord(userId, record -> {
                    if (!isAdded()) return;
                    render(plants, record);
                }, e -> showError(e)),
                e -> showError(e));
    }

    private void render(List<MyPlant> plants, int record) {
        adapter.submitList(plants);

        // Số liệu tổng hợp tính ra từ các cây (A13), không lưu riêng
        StreakSummary s = ChuoiUtil.summarize(plants, record);
        tvHighestStreak.setText(String.valueOf(s.getHighestCurrentStreak()));
        tvHolding.setText("Đang giữ chuỗi: " + s.getHoldingCount() + "/" + s.getTotalPlants() + " cây");
        tvRecord.setText("Kỷ lục: " + s.getMaxStreakRecord() + " ngày");

        boolean empty = plants.isEmpty();
        emptyView.setVisibility(empty ? View.VISIBLE : View.GONE);
        rvMyPlants.setVisibility(empty ? View.GONE : View.VISIBLE);
        // Chưa có cây: chỉ gợi ý thêm cây, không có nhãn "Tạm nghỉ" (mục 5.3)
        tvEmpty.setText("Bạn chưa có cây nào.\nBấm nút + để thêm cây đầu tiên.");
    }

    private void showError(Exception e) {
        if (!isAdded()) return;
        Toast.makeText(requireContext(), "Không tải được dữ liệu: " + e.getMessage(),
                Toast.LENGTH_LONG).show();
    }

    private void showGuestState() {
        summaryCard.setVisibility(View.GONE);
        fabAdd.setVisibility(View.GONE);
        rvMyPlants.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
        tvEmpty.setText("Đăng nhập để quản lý cây và giữ chuỗi chăm sóc.");
        adapter.submitList(new ArrayList<>());
    }
}
