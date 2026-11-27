package com.example.doanltdd_flynn.encyclopedia;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.doanltdd_flynn.R;

import java.util.Arrays;
import java.util.List;

public class PlantDetailActivity extends AppCompatActivity {

    public static final String EXTRA_PLANT_ID = "plant_id";

    private static final int DEFAULT_ACCENT_COLOR = Color.parseColor("#5B3A52");
    private static final int INACTIVE_TAB_BG = Color.parseColor("#EAE2D0");
    private static final int INACTIVE_TAB_TEXT = Color.parseColor("#3A332D");
    private static final int ACTIVE_TAB_TEXT = Color.WHITE;

    private ImageButton btnBack;
    private ImageButton btnBookmark;
    private ImageButton btnShare;

    private ProgressBar progressLoading;
    private TextView tvErrorMessage;

    private TextView tvCommonName;
    private TextView tvEnglishName;
    private TextView tvScientificName;
    private TextView tvOrigin;
    private TextView tvBestSeason;
    private TextView tvLight;

    private ImageView ivMainPlant;
    private ImageView ivIntroPlant;
    private ImageView ivDetailPart;

    private TextView tvShortQuote;
    private TextView tvIntroContent;

    private Button btnTab1;
    private Button btnTab2;
    private Button btnTab3;
    private Button btnTab4;

    private TextView tvSubTitle;
    private TextView tvSubContent;

    private PlantRepository plantRepository;
    private Plant currentPlant;
    private int activeTabIndex = 0;
    private int accentColor = DEFAULT_ACCENT_COLOR;
    private boolean isBookmarked = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_detail);

        plantRepository = new PlantRepository();
        initViews();
        setupListeners();

        String plantId = getIntent().getStringExtra(EXTRA_PLANT_ID);
        if (plantId == null || plantId.trim().isEmpty()) {
            showError("Không tìm thấy mã cây (plant_id).");
            return;
        }

        loadPlantDetail(plantId.trim());
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnBookmark = findViewById(R.id.btnBookmark);
        btnShare = findViewById(R.id.btnShare);

        progressLoading = findViewById(R.id.progressLoading);
        tvErrorMessage = findViewById(R.id.tvErrorMessage);

        tvCommonName = findViewById(R.id.tvCommonName);
        tvEnglishName = findViewById(R.id.tvEnglishName);
        tvScientificName = findViewById(R.id.tvScientificName);
        tvOrigin = findViewById(R.id.tvOrigin);
        tvBestSeason = findViewById(R.id.tvBestSeason);
        tvLight = findViewById(R.id.tvLight);

        ivMainPlant = findViewById(R.id.ivMainPlant);
        ivIntroPlant = findViewById(R.id.ivIntroPlant);
        ivDetailPart = findViewById(R.id.ivDetailPart);

        tvShortQuote = findViewById(R.id.tvShortQuote);
        tvIntroContent = findViewById(R.id.tvIntroContent);

        btnTab1 = findViewById(R.id.btnTab1);
        btnTab2 = findViewById(R.id.btnTab2);
        btnTab3 = findViewById(R.id.btnTab3);
        btnTab4 = findViewById(R.id.btnTab4);

        tvSubTitle = findViewById(R.id.tvSubTitle);
        tvSubContent = findViewById(R.id.tvSubContent);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnBookmark.setOnClickListener(v -> {
            if (currentPlant == null) return;
            isBookmarked = !isBookmarked;
            btnBookmark.setImageResource(
                    isBookmarked ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off
            );
            String msg = isBookmarked
                    ? "Đã lưu " + safeText(currentPlant.getTenTiengViet(), "cây") + " vào yêu thích"
                    : "Đã bỏ lưu khỏi yêu thích";
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        });

        btnShare.setOnClickListener(v -> {
            if (currentPlant == null) return;
            String title = safeText(currentPlant.getTenTiengViet(), "Cây bách khoa");
            String quote = safeText(currentPlant.getCauDanNgan(), "");
            String shareText = quote.isEmpty()
                    ? title + " — Flynn Bách khoa toàn thư"
                    : title + ": \"" + quote + "\" — Flynn Bách khoa toàn thư";

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            startActivity(Intent.createChooser(shareIntent, "Chia sẻ thông tin cây"));
        });

        btnTab1.setOnClickListener(v -> {
            if (activeTabIndex == 0) {
                openCharacteristicsScreen();
            } else {
                selectTab(0);
            }
        });
        btnTab2.setOnClickListener(v -> selectTab(1));
        btnTab3.setOnClickListener(v -> selectTab(2));
        btnTab4.setOnClickListener(v -> selectTab(3));
    }

    private void openCharacteristicsScreen() {
        if (currentPlant == null || currentPlant.getId() == null || currentPlant.getId().trim().isEmpty()) {
            return;
        }
        Intent intent = new Intent(this, PlantCharacteristicsActivity.class);
        intent.putExtra(PlantCharacteristicsActivity.EXTRA_PLANT_ID, currentPlant.getId().trim());
        startActivity(intent);
    }

    private void loadPlantDetail(@NonNull String plantId) {
        progressLoading.setVisibility(ProgressBar.VISIBLE);
        tvErrorMessage.setVisibility(TextView.GONE);

        plantRepository.getPlantById(plantId, new PlantRepository.PlantDetailCallback() {
            @Override
            public void onSuccess(@NonNull Plant plant) {
                progressLoading.setVisibility(ProgressBar.GONE);
                tvErrorMessage.setVisibility(TextView.GONE);
                currentPlant = plant;
                bindPlantData(plant);
            }

            @Override
            public void onNotFound() {
                progressLoading.setVisibility(ProgressBar.GONE);
                showError("Không tìm thấy dữ liệu cây với mã: " + plantId);
            }

            @Override
            public void onError(@NonNull Exception e) {
                progressLoading.setVisibility(ProgressBar.GONE);
                showError("Lỗi tải dữ liệu từ Firestore: " + e.getMessage());
            }
        });
    }

    private void showError(@NonNull String message) {
        tvErrorMessage.setText(message);
        tvErrorMessage.setVisibility(TextView.VISIBLE);
    }

    private void bindPlantData(@NonNull Plant plant) {
        accentColor = parseAccentColor(plant.getMauChuDao());

        tvCommonName.setText(safeText(plant.getTenTiengViet(), "Chưa có tên"));
        tvCommonName.setTextColor(accentColor);

        tvEnglishName.setText(safeText(plant.getTenTiengAnh(), ""));
        tvScientificName.setText(safeText(plant.getTenKhoaHoc(), ""));

        tvOrigin.setText("Nguồn gốc: " + safeText(plant.getNguonGoc(), "Đang cập nhật"));
        tvBestSeason.setText("Thời điểm đẹp nhất: " + safeText(plant.getThoiDiemDep(), "Đang cập nhật"));
        tvLight.setText("Ánh sáng: " + safeText(plant.getAnhSang(), "Đang cập nhật"));

        String quote = safeText(plant.getCauDanNgan(), "");
        if (quote.isEmpty()) {
            tvShortQuote.setVisibility(TextView.GONE);
        } else {
            tvShortQuote.setVisibility(TextView.VISIBLE);
            tvShortQuote.setText("“" + quote + "”");
        }

        tvIntroContent.setText(safeText(plant.getGioiThieu(), "Đang cập nhật phần giới thiệu."));

        btnTab1.setText(safeText(plant.getDacDiemTieuDe(), "Đặc điểm"));
        btnTab2.setText(safeText(plant.getMuc2TieuDe(), "Mục 2"));
        btnTab3.setText(safeText(plant.getMuc3TieuDe(), "Mục 3"));
        btnTab4.setText(safeText(plant.getMuc4TieuDe(), "Mục 4"));

        loadImage(ivMainPlant, plant.getAnhChinh());
        loadImage(ivIntroPlant, plant.getAnhGioiThieu());

        selectTab(0);
    }

    private void selectTab(int index) {
        activeTabIndex = index;
        List<Button> tabs = Arrays.asList(btnTab1, btnTab2, btnTab3, btnTab4);

        for (int i = 0; i < tabs.size(); i++) {
            Button tab = tabs.get(i);
            if (i == index) {
                tab.setBackgroundTintList(ColorStateList.valueOf(accentColor));
                tab.setTextColor(ACTIVE_TAB_TEXT);
            } else {
                tab.setBackgroundTintList(ColorStateList.valueOf(INACTIVE_TAB_BG));
                tab.setTextColor(INACTIVE_TAB_TEXT);
            }
        }

        if (currentPlant == null) return;

        String title;
        String content;
        String imageUrl;

        switch (index) {
            case 1:
                title = safeText(currentPlant.getMuc2TieuDe(), "Mục 2");
                content = safeText(currentPlant.getMuc2NoiDung(), "Đang cập nhật nội dung.");
                imageUrl = currentPlant.getAnhBoPhan();
                break;
            case 2:
                title = safeText(currentPlant.getMuc3TieuDe(), "Mục 3");
                content = safeText(currentPlant.getMuc3NoiDung(), "Đang cập nhật nội dung.");
                imageUrl = currentPlant.getAnhBoPhan();
                break;
            case 3:
                title = safeText(currentPlant.getMuc4TieuDe(), "Mục 4");
                content = safeText(currentPlant.getMuc4NoiDung(), "Đang cập nhật nội dung.");
                imageUrl = currentPlant.getAnhBoPhan();
                break;
            case 0:
            default:
                title = safeText(currentPlant.getDacDiemTieuDe(), "Đặc điểm");
                content = safeText(currentPlant.getDacDiemNoiDung(), "Đang cập nhật đặc điểm.");
                imageUrl = currentPlant.getAnhBoPhan();
                break;
        }

        tvSubTitle.setText(title);
        tvSubTitle.setTextColor(accentColor);
        tvSubContent.setText(content);
        loadImage(ivDetailPart, imageUrl);
    }

    private void loadImage(@NonNull ImageView targetView, @Nullable String imageUrl) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            targetView.setImageDrawable(null);
            return;
        }

        String value = imageUrl.trim();
        if (value.startsWith("http://") || value.startsWith("https://")) {
            Glide.with(this)
                    .load(value)
                    .centerCrop()
                    .into(targetView);
            return;
        }

        int resId = getResources().getIdentifier(value, "drawable", getPackageName());
        if (resId != 0) {
            Glide.with(this)
                    .load(resId)
                    .centerCrop()
                    .into(targetView);
        } else {
            targetView.setImageDrawable(null);
        }
    }

    private int parseAccentColor(@Nullable String hex) {
        if (hex == null || hex.trim().isEmpty()) {
            return DEFAULT_ACCENT_COLOR;
        }
        try {
            String value = hex.trim();
            if (!value.startsWith("#")) {
                value = "#" + value;
            }
            return Color.parseColor(value);
        } catch (IllegalArgumentException ex) {
            return DEFAULT_ACCENT_COLOR;
        }
    }

    @NonNull
    private String safeText(@Nullable String value, @NonNull String fallback) {
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }
        return value.trim();
    }
}