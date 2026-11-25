package com.example.doanltdd_flynn.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doanltdd_flynn.R;
import com.example.doanltdd_flynn.adapter.CareLogAdapter;
import com.example.doanltdd_flynn.controller.CareController;
import com.example.doanltdd_flynn.controller.MyPlantController;
import com.example.doanltdd_flynn.model.CareLog;
import com.example.doanltdd_flynn.model.MyPlant;
import com.example.doanltdd_flynn.model.StreakState;
import com.example.doanltdd_flynn.utils.ChuoiUtil;
import com.example.doanltdd_flynn.utils.UiUtil;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;


public class MyPlantDetailActivity extends AppCompatActivity {

    public static final String EXTRA_PLANT_ID = "plant_id";

    private MyPlantController myPlantController;
    private CareController careController;
    private String userId;
    private String plantId;
    private MyPlant currentPlant;

    private CareLogAdapter adapter;

    private MaterialToolbar toolbar;
    private TextView tvName;
    private TextView tvPlantingDate;
    private TextView tvStreak;
    private TextView tvStreakState;
    private TextView tvLongest;
    private TextView tvTotalCare;
    private TextView tvCompanion;
    private TextView tvNote;
    private TextView tvEmptyLogs;
    private RecyclerView rvLogs;
    private MaterialButton btnAddLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_plant_detail);
        UiUtil.applyEdgeToEdge(this, findViewById(R.id.root));

        plantId = getIntent().getStringExtra(EXTRA_PLANT_ID);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (plantId == null || user == null) {
            finish();
            return;
        }
        userId = user.getUid();
        myPlantController = new MyPlantController();
        careController = new CareController();

        toolbar = findViewById(R.id.toolbar);
        tvName = findViewById(R.id.tvName);
        tvPlantingDate = findViewById(R.id.tvPlantingDate);
        tvStreak = findViewById(R.id.tvStreak);
        tvStreakState = findViewById(R.id.tvStreakState);
        tvLongest = findViewById(R.id.tvLongest);
        tvTotalCare = findViewById(R.id.tvTotalCare);
        tvCompanion = findViewById(R.id.tvCompanion);
        tvNote = findViewById(R.id.tvNote);
        tvEmptyLogs = findViewById(R.id.tvEmptyLogs);
        rvLogs = findViewById(R.id.rvLogs);
        btnAddLog = findViewById(R.id.btnAddLog);

        toolbar.setNavigationOnClickListener(v -> finish());
        toolbar.inflateMenu(R.menu.menu_my_plant_detail);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_delete) {
                confirmDeletePlant();
                return true;
            }
            return false;
        });

        adapter = new CareLogAdapter(new CareLogAdapter.Listener() {
            @Override
            public void onEdit(CareLog log) {
                Intent i = new Intent(MyPlantDetailActivity.this, AddCareLogActivity.class);
                i.putExtra(AddCareLogActivity.EXTRA_PLANT_ID, plantId);
                i.putExtra(AddCareLogActivity.EXTRA_LOG_ID, log.getCareLogId());
                i.putExtra(AddCareLogActivity.EXTRA_LOG_DATE, log.getCareDate());
                i.putExtra(AddCareLogActivity.EXTRA_LOG_TYPE,
                        log.getCareType() == null ? "OTHER" : log.getCareType().name());
                i.putExtra(AddCareLogActivity.EXTRA_LOG_NOTE, log.getNote());
                startActivity(i);
            }

            @Override
            public void onDelete(CareLog log) {
                confirmDeleteLog(log);
            }
        });
        rvLogs.setLayoutManager(new LinearLayoutManager(this));
        rvLogs.setAdapter(adapter);

        btnAddLog.setOnClickListener(v -> {
            Intent i = new Intent(this, AddCareLogActivity.class);
            i.putExtra(AddCareLogActivity.EXTRA_PLANT_ID, plantId);
            startActivity(i);
        });
    }

    // Nạp lại mỗi lần quay về (sau khi ghi / sửa nhật ký) để chuỗi và danh sách luôn mới
    @Override
    protected void onResume() {
        super.onResume();
        if (myPlantController == null) return;
        loadPlant();
        loadLogs();
    }

    private void loadPlant() {
        myPlantController.getMyPlantDetail(userId, plantId, plant -> {
            if (plant == null) { // cây đã bị xóa
                finish();
                return;
            }
            currentPlant = plant;
            renderPlant(plant);
        }, e -> Toast.makeText(this, "Không tải được cây: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void loadLogs() {
        careController.getCareLogs(userId, plantId, this::renderLogs,
                e -> Toast.makeText(this, "Không tải được nhật ký: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void renderPlant(MyPlant p) {
        toolbar.setTitle(p.getPlantName());
        tvName.setText(p.getPlantName());
        tvPlantingDate.setText("Bắt đầu trồng: " + ChuoiUtil.display(p.getPlantingDate()));

        // Mọi con số đều tính từ danh sách ngày chăm (không lưu bộ đếm)
        tvStreak.setText(String.valueOf(ChuoiUtil.calculateCurrentStreak(p.getCareDays())));
        StreakState state = ChuoiUtil.getStreakState(p.getCareDays());
        tvStreakState.setText(state.getLabel());
        int color;
        switch (state) {
            case CARED_TODAY:
                color = R.color.flynn_green_dark;
                break;
            case WAITING:
                color = R.color.flynn_orange;
                break;
            default:
                color = R.color.flynn_grey;
                break;
        }
        tvStreakState.setTextColor(ContextCompat.getColor(this, color));

        tvLongest.setText(String.valueOf(ChuoiUtil.calculateLongestStreak(p.getCareDays())));
        tvTotalCare.setText(String.valueOf(ChuoiUtil.countCareDays(p.getCareDays())));
        tvCompanion.setText(String.valueOf(ChuoiUtil.calculateCompanionDays(p.getPlantingDate())));

        String note = p.getNote();
        if (note == null || note.trim().isEmpty()) {
            tvNote.setVisibility(View.GONE);
        } else {
            tvNote.setVisibility(View.VISIBLE);
            tvNote.setText("Ghi chú: " + note);
        }
    }

    private void renderLogs(List<CareLog> logs) {
        adapter.submitList(logs);
        tvEmptyLogs.setVisibility(logs.isEmpty() ? View.VISIBLE : View.GONE);
        rvLogs.setVisibility(logs.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private void confirmDeleteLog(CareLog log) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Xóa nhật ký?")
                .setMessage("Xóa nhật ký không làm thay đổi chuỗi của cây.")
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Xóa", (d, w) ->
                        careController.deleteCareLog(userId, plantId, log.getCareLogId(),
                                unused -> loadLogs(),
                                e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show()))
                .show();
    }

    private void confirmDeletePlant() {
        if (currentPlant == null) return;
        new MaterialAlertDialogBuilder(this)
                .setTitle("Xóa cây \"" + currentPlant.getPlantName() + "\"?")
                .setMessage("Cây và toàn bộ nhật ký sẽ bị xóa và không thể mở lại.\n\n"
                        + "Flynn sẽ lưu một thẻ kỷ niệm \"Bạn đã ở bên " + currentPlant.getPlantName()
                        + " được " + ChuoiUtil.calculateCompanionDays(currentPlant.getPlantingDate())
                        + " ngày\" vào Lịch sử trồng cây. Kỷ lục chuỗi của bạn vẫn được giữ nguyên.")
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Xóa cây", (d, w) ->
                        myPlantController.deleteMyPlant(userId, plantId,
                                unused -> {
                                    Toast.makeText(this, "Đã xóa cây và lưu thẻ kỷ niệm",
                                            Toast.LENGTH_SHORT).show();
                                    finish();
                                },
                                e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show()))
                .show();
    }
}
