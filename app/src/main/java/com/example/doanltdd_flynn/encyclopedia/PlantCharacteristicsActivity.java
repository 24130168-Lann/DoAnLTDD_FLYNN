package com.example.doanltdd_flynn.encyclopedia;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.doanltdd_flynn.R;

public class PlantCharacteristicsActivity extends AppCompatActivity {

    public static final String EXTRA_PLANT_ID = "plant_id";

    private ImageButton btnBackChar;
    private ProgressBar progressChar;
    private TextView tvCharError;

    private TextView tvCharCategory;
    private TextView tvCharCommonName;
    private TextView tvCharEnglishName;
    private TextView tvCharScientificName;

    private ImageView ivCharMainImage;
    private TextView tvCharFeatureTitle;
    private TextView tvCharFeatureContent;
    private ImageView ivCharPartImage;

    private TextView tvCharOrigin;
    private TextView tvCharSeason;
    private TextView tvCharLight;
    private TextView tvCharIntro;

    private PlantRepository plantRepository;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_characteristics);

        plantRepository = new PlantRepository();
        initViews();
        btnBackChar.setOnClickListener(v -> finish());

        String plantId = getIntent().getStringExtra(EXTRA_PLANT_ID);
        if (plantId == null || plantId.trim().isEmpty()) {
            showError("Không tìm thấy mã cây để hiển thị đặc điểm.");
            return;
        }

        loadCharacteristics(plantId.trim());
    }

    private void initViews() {
        btnBackChar = findViewById(R.id.btnBackChar);
        progressChar = findViewById(R.id.progressChar);
        tvCharError = findViewById(R.id.tvCharError);

        tvCharCategory = findViewById(R.id.tvCharCategory);
        tvCharCommonName = findViewById(R.id.tvCharCommonName);
        tvCharEnglishName = findViewById(R.id.tvCharEnglishName);
        tvCharScientificName = findViewById(R.id.tvCharScientificName);

        ivCharMainImage = findViewById(R.id.ivCharMainImage);
        tvCharFeatureTitle = findViewById(R.id.tvCharFeatureTitle);
        tvCharFeatureContent = findViewById(R.id.tvCharFeatureContent);
        ivCharPartImage = findViewById(R.id.ivCharPartImage);

        tvCharOrigin = findViewById(R.id.tvCharOrigin);
        tvCharSeason = findViewById(R.id.tvCharSeason);
        tvCharLight = findViewById(R.id.tvCharLight);
        tvCharIntro = findViewById(R.id.tvCharIntro);
    }

    private void loadCharacteristics(@NonNull String plantId) {
        progressChar.setVisibility(View.VISIBLE);
        tvCharError.setVisibility(View.GONE);

        plantRepository.getPlantById(plantId, new PlantRepository.PlantDetailCallback() {
            @Override
            public void onSuccess(@NonNull Plant plant) {
                progressChar.setVisibility(View.GONE);
                bindData(plant);
            }

            @Override
            public void onNotFound() {
                progressChar.setVisibility(View.GONE);
                showError("Không tìm thấy dữ liệu đặc điểm cho cây: " + plantId);
            }

            @Override
            public void onError(@NonNull Exception e) {
                progressChar.setVisibility(View.GONE);
                showError("Lỗi tải dữ liệu: " + e.getMessage());
            }
        });
    }

    private void bindData(@NonNull Plant plant) {
        int accent = parseColorOrDefault(plant.getMauChuDao());

        String category = safe(plant.getNhomCay(), "BÁCH KHOA");
        tvCharCategory.setText(category.toUpperCase());
        tvCharCategory.setTextColor(accent);

        tvCharCommonName.setText(safe(plant.getTenTiengViet(), "Chưa có tên"));
        tvCharEnglishName.setText(safe(plant.getTenTiengAnh(), ""));
        tvCharScientificName.setText(safe(plant.getTenKhoaHoc(), ""));

        tvCharFeatureTitle.setText(safe(plant.getDacDiemTieuDe(), "Đặc điểm nổi bật"));
        tvCharFeatureTitle.setTextColor(accent);
        tvCharFeatureContent.setText(safe(plant.getDacDiemNoiDung(), "Đang cập nhật đặc điểm nổi bật."));

        tvCharOrigin.setText("Nguồn gốc: " + safe(plant.getNguonGoc(), "Đang cập nhật"));
        tvCharSeason.setText("Thời điểm đẹp nhất: " + safe(plant.getThoiDiemDep(), "Đang cập nhật"));
        tvCharLight.setText("Ánh sáng: " + safe(plant.getAnhSang(), "Đang cập nhật"));
        tvCharIntro.setText(safe(plant.getGioiThieu(), "Đang cập nhật giới thiệu."));

        loadImage(ivCharMainImage, plant.getAnhChinh());
        loadImage(ivCharPartImage, plant.getAnhBoPhan());
    }

    private void loadImage(@NonNull ImageView targetView, @Nullable String imageUrl) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            targetView.setImageDrawable(null);
            return;
        }
        String value = imageUrl.trim();
        if (value.startsWith("http://") || value.startsWith("https://")) {
            Glide.with(this).load(value).centerCrop().into(targetView);
            return;
        }
        int resId = getResources().getIdentifier(value, "drawable", getPackageName());
        if (resId != 0) {
            Glide.with(this).load(resId).centerCrop().into(targetView);
        } else {
            targetView.setImageDrawable(null);
        }
    }

    private void showError(@NonNull String msg) {
        tvCharError.setText(msg);
        tvCharError.setVisibility(View.VISIBLE);
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
    private String safe(@Nullable String val, @NonNull String fallback) {
        if (val == null || val.trim().isEmpty()) {
            return fallback;
        }
        return val.trim();
    }
}