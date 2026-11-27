package com.example.doanltdd_flynn.encyclopedia;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import com.google.android.material.button.MaterialButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doanltdd_flynn.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EncyclopediaActivity extends AppCompatActivity {

    private static final List<String> CATEGORIES = Arrays.asList(
            "Tất cả",
            "Hoa cảnh",
            "Cây ăn quả",
            "Rau & Cây gia vị",
            "Cây gỗ & Cây bóng mát",
            "Cây thủy sinh & Bonsai"
    );

    private EditText etSearchPlant;
    private LinearLayout categoryContainer;
    private ProgressBar progressEncyclopedia;
    private TextView tvEncyclopediaStatus;
    private RecyclerView rvPlants;

    private PlantRepository plantRepository;
    private PlantAdapter plantAdapter;

    private String selectedCategory = "Tất cả";
    private final List<MaterialButton> categoryButtons = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encyclopedia);

        plantRepository = new PlantRepository();
        initViews();
        setupCategoryChips();
        setupRecyclerView();
        setupSearch();

        fetchPlants();
    }

    private void initViews() {
        etSearchPlant = findViewById(R.id.etSearchPlant);
        categoryContainer = findViewById(R.id.categoryContainer);
        progressEncyclopedia = findViewById(R.id.progressEncyclopedia);
        tvEncyclopediaStatus = findViewById(R.id.tvEncyclopediaStatus);
        rvPlants = findViewById(R.id.rvPlants);
    }
    private void setupCategoryChips() {
        categoryContainer.removeAllViews();
        categoryButtons.clear();

        for (int i = 0; i < CATEGORIES.size(); i++) {
            String category = CATEGORIES.get(i);
            MaterialButton chip = new MaterialButton(this);
            chip.setText(category);
            chip.setAllCaps(false);
            chip.setTextSize(12.5f);
            chip.setMinWidth(0);
            chip.setMinimumWidth(0);
            chip.setInsetTop(0);
            chip.setInsetBottom(0);
            chip.setPadding(32, 0, 32, 0);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    96
            );
            if (i < CATEGORIES.size() - 1) {
                params.setMarginEnd(16);
            }
            chip.setLayoutParams(params);

            chip.setOnClickListener(v -> {
                selectedCategory = category;
                updateCategoryChipStyles();
                fetchPlants();
            });

            categoryContainer.addView(chip);
            categoryButtons.add(chip);
        }

        updateCategoryChipStyles();
    }

    private void updateCategoryChipStyles() {
        int activeBg = Color.parseColor("#5B3A52");
        int inactiveBg = Color.parseColor("#EAE2D0");
        int inactiveText = Color.parseColor("#3A332D");

        for (int i = 0; i < CATEGORIES.size(); i++) {
            MaterialButton chip = categoryButtons.get(i);
            boolean isSelected = CATEGORIES.get(i).equals(selectedCategory);
            chip.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                    isSelected ? activeBg : inactiveBg
            ));
            chip.setTextColor(isSelected ? Color.WHITE : inactiveText);
        }
    }


    private void setupRecyclerView() {
        plantAdapter = new PlantAdapter(plant -> {
            Intent intent = new Intent(EncyclopediaActivity.this, PlantDetailActivity.class);
            intent.putExtra(PlantDetailActivity.EXTRA_PLANT_ID, plant.getId());
            startActivity(intent);
        });
        rvPlants.setLayoutManager(new LinearLayoutManager(this));
        rvPlants.setAdapter(plantAdapter);
    }

    private void setupSearch() {
        etSearchPlant.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                fetchPlants();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void fetchPlants() {
        String keyword = etSearchPlant.getText() != null ? etSearchPlant.getText().toString() : "";
        progressEncyclopedia.setVisibility(View.VISIBLE);
        tvEncyclopediaStatus.setVisibility(View.GONE);

        plantRepository.searchPlants(keyword, selectedCategory, new PlantRepository.PlantListCallback() {
            @Override
            public void onSuccess(@NonNull List<Plant> plants) {
                progressEncyclopedia.setVisibility(View.GONE);
                plantAdapter.setItems(plants);
                if (plants.isEmpty()) {
                    tvEncyclopediaStatus.setText("Không tìm thấy cây phù hợp.");
                    tvEncyclopediaStatus.setVisibility(View.VISIBLE);
                } else {
                    tvEncyclopediaStatus.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(@NonNull Exception e) {
                progressEncyclopedia.setVisibility(View.GONE);
                tvEncyclopediaStatus.setText("Lỗi tải danh sách cây: " + e.getMessage());
                tvEncyclopediaStatus.setVisibility(View.VISIBLE);
            }
        });
    }
}
