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
import com.example.doanltdd_flynn.repository.LocalTestStore;
import com.example.doanltdd_flynn.utils.ChuoiUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

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
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_my_plants,
                container,
                false
        );

        // 1. Ánh xạ giao diện
        rvMyPlants = view.findViewById(R.id.rvMyPlants);
        emptyView = view.findViewById(R.id.emptyView);
        summaryCard = view.findViewById(R.id.summaryCard);

        tvEmpty = view.findViewById(R.id.tvEmpty);
        tvHighestStreak = view.findViewById(R.id.tvHighestStreak);
        tvHolding = view.findViewById(R.id.tvHolding);
        tvRecord = view.findViewById(R.id.tvRecord);

        fabAdd = view.findViewById(R.id.fabAdd);

        // 2. Khởi tạo Controller
        myPlantController = new MyPlantController();

        // 3. Khởi tạo Adapter
        adapter = new MyPlantAdapter(
                new MyPlantAdapter.Listener() {

                    @Override
                    public void onItemClick(MyPlant plant) {

                        Intent intent = new Intent(
                                requireContext(),
                                MyPlantDetailActivity.class
                        );

                        intent.putExtra(
                                MyPlantDetailActivity.EXTRA_PLANT_ID,
                                plant.getMyPlantId()
                        );

                        startActivity(intent);
                    }

                    @Override
                    public void onQuickCareClick(MyPlant plant) {

                        Intent intent = new Intent(
                                requireContext(),
                                AddCareLogActivity.class
                        );

                        intent.putExtra(
                                AddCareLogActivity.EXTRA_PLANT_ID,
                                plant.getMyPlantId()
                        );

                        startActivity(intent);
                    }
                }
        );

        // 4. Cấu hình RecyclerView
        rvMyPlants.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );

        rvMyPlants.setAdapter(adapter);

        // 5. Xử lý nút thêm cây
        fabAdd.setOnClickListener(v -> {

            Intent intent = new Intent(
                    requireContext(),
                    AddMyPlantActivity.class
            );

            startActivity(intent);
        });

        return view;
    }

    // Khi quay lại màn hình, tải lại danh sách cây
    @Override
    public void onResume() {
        super.onResume();

        FirebaseUser user = FirebaseAuth
                .getInstance()
                .getCurrentUser();

        // Chưa đăng nhập: sử dụng dữ liệu kiểm thử cục bộ.
        if (user == null) {
            loadLocalData();
            return;
        }

        // Đã đăng nhập: sử dụng Firebase như luồng ban đầu.
        loadData(user.getUid());
    }

  

    private void loadLocalData() {

        List<MyPlant> plants = LocalTestStore.getPlants();

        int record = 0;

        // Tìm chuỗi chăm sóc dài nhất trong các cây kiểm thử.
        for (MyPlant plant : plants) {

            int longest = ChuoiUtil.calculateLongestStreak(
                    plant.getCareDays()
            );

            if (longest > record) {
                record = longest;
            }
        }

        render(plants, record);
    }

    // =========================
    // DỮ LIỆU FIREBASE
    // =========================

    private void loadData(String userId) {

        myPlantController.getMyPlants(
                userId,
                plants -> {

                    if (!isAdded() || getView() == null) {
                        return;
                    }

                    myPlantController.getMaxStreakRecord(
                            userId,
                            record -> {

                                if (!isAdded() || getView() == null) {
                                    return;
                                }

                                render(plants, record);
                            },
                            this::showError
                    );
                },
                this::showError
        );
    }



    private void render(List<MyPlant> plants, int record) {

        if (plants == null) {
            plants = new ArrayList<>();
        }

        adapter.submitList(plants);

        // Tính toán thống kê chăm sóc.
        StreakSummary summary = ChuoiUtil.summarize(
                plants,
                record
        );

        // Hiển thị chuỗi cao nhất.
        tvHighestStreak.setText(
                String.valueOf(summary.getHighestCurrentStreak())
        );

        // Hiển thị số cây đang duy trì chuỗi.
        tvHolding.setText(
                String.valueOf(summary.getHoldingCount())
        );

        // Hiển thị kỷ lục chăm sóc.
        tvRecord.setText(
                "Kỷ lục: "
                        + summary.getMaxStreakRecord()
                        + " ngày"
        );

        summaryCard.setVisibility(View.VISIBLE);
        fabAdd.setVisibility(View.VISIBLE);

        // Nếu chưa có cây.
        if (plants.isEmpty()) {

            rvMyPlants.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);

            tvEmpty.setText(
                    "Bạn chưa có cây nào!\n"
                            + "Hãy thêm người bạn xanh đầu tiên nhé."
            );

        } else {

            // Nếu đã có cây.
            emptyView.setVisibility(View.GONE);
            rvMyPlants.setVisibility(View.VISIBLE);
        }
    }

    

    private void showError(Exception e) {

        if (!isAdded()) {
            return;
        }

        String message = e == null
                ? "Đã xảy ra lỗi"
                : e.getMessage();

        if (message == null || message.trim().isEmpty()) {
            message = "Không thể tải dữ liệu cây trồng.";
        }

        Toast.makeText(
                requireContext(),
                message,
                Toast.LENGTH_LONG
        ).show();
    }
}
