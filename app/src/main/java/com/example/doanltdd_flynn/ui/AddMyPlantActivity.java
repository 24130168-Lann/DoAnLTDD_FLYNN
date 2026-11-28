package com.example.doanltdd_flynn.ui;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.doanltdd_flynn.R;
import com.example.doanltdd_flynn.controller.MyPlantController;
import com.example.doanltdd_flynn.model.MyPlant;
import com.example.doanltdd_flynn.model.PlantSuggestion;
import com.example.doanltdd_flynn.repository.LocalTestStore;
import com.example.doanltdd_flynn.utils.ChuoiUtil;
import com.example.doanltdd_flynn.utils.TextUtil;
import com.example.doanltdd_flynn.utils.UiUtil;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AddMyPlantActivity extends AppCompatActivity {

    private MyPlantController myPlantController;

    // Ô tìm cây trong Bách khoa
    private MaterialAutoCompleteTextView actSearch;

    // Ô nhập tên cây để lưu vào vườn cá nhân
    private TextInputEditText etName;

    private TextInputEditText etDate;
    private TextInputEditText etNote;

    private ChipGroup chipStage;

    private MaterialButton btnSave;

    private List<PlantSuggestion> suggestions = new ArrayList<>();

    private String selectedPlantId = null;

    private LocalDate plantingDate = ChuoiUtil.todayDate();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_my_plant);

        UiUtil.applyEdgeToEdge(
                this,
                findViewById(R.id.root)
        );

        myPlantController = new MyPlantController();

        // Thanh tiêu đề
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Ánh xạ giao diện
        actSearch = findViewById(R.id.actSearch);
        etName = findViewById(R.id.actName);
        etDate = findViewById(R.id.etDate);
        etNote = findViewById(R.id.etNote);

        chipStage = findViewById(R.id.chipStage);
        btnSave = findViewById(R.id.btnSave);

        // Chỉ hiện gợi ý sau khi nhập ít nhất 1 ký tự
        actSearch.setThreshold(1);

        // Mặc định chọn giai đoạn Phát triển
        chipStage.check(R.id.chipGrowing);

        // Hiển thị ngày trồng mặc định
        etDate.setText(
                ChuoiUtil.display(
                        ChuoiUtil.toKey(plantingDate)
                )
        );

        // Chọn ngày trồng
        etDate.setOnClickListener(v ->
                UiUtil.pickDate(
                        this,
                        plantingDate,
                        date -> {
                            plantingDate = date;

                            etDate.setText(
                                    ChuoiUtil.display(
                                            ChuoiUtil.toKey(date)
                                    )
                            );
                        }
                )
        );

        // Khi người dùng tự sửa tên cây,
        // kiểm tra xem tên đó có trùng cây trong Bách khoa không
        etName.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(
                    CharSequence s,
                    int start,
                    int count,
                    int after
            ) {
            }

            @Override
            public void onTextChanged(
                    CharSequence s,
                    int start,
                    int before,
                    int count
            ) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                selectedPlantId = findPlantId(s.toString());
            }
        });

        // Chọn một cây từ danh sách tìm kiếm
        actSearch.setOnItemClickListener((parent, view, position, id) -> {

            String selectedName = (String) parent.getItemAtPosition(position);

            PlantSuggestion selectedPlant = findSuggestion(selectedName);

            if (selectedPlant != null) {

                // Điền tên cây vào ô thông tin cây
                etName.setText(selectedPlant.getName());

                // Gán đúng ID tài liệu Firebase
                selectedPlantId = selectedPlant.getPlantId();

                // Xóa thông báo lỗi nếu có
                etName.setError(null);
            }
        });

        // Nút lưu cây
        btnSave.setOnClickListener(v -> save());

        // Tải danh sách cây từ Firebase
        loadSuggestions();
    }

    

    private void loadSuggestions() {

        myPlantController.getPlantSuggestions(

                list -> {

                    if (isFinishing() || isDestroyed()) {
                        return;
                    }

                    suggestions = list;

                    List<String> names = new ArrayList<>();

                    for (PlantSuggestion plant : suggestions) {

                        names.add(plant.getName());
                    }

                    SuggestionAdapter adapter =
                            new SuggestionAdapter(this, names);

                    actSearch.setAdapter(adapter);
                },

                e -> {

                    if (isFinishing() || isDestroyed()) {
                        return;
                    }

                    Toast.makeText(
                            this,
                            "Không thể tải danh sách cây từ Bách khoa",
                            Toast.LENGTH_LONG
                    ).show();
                }
        );
    }

    

    private PlantSuggestion findSuggestion(String name) {

        String key = TextUtil.normalize(name);

        if (key.isEmpty()) {
            return null;
        }

        for (PlantSuggestion plant : suggestions) {

            if (TextUtil.normalize(plant.getName()).equals(key)) {
                return plant;
            }
        }

        return null;
    }

   

    private String findPlantId(String typedName) {

        PlantSuggestion plant = findSuggestion(typedName);

        if (plant != null) {
            return plant.getPlantId();
        }

        return null;
    }

  

    private String getSelectedStage() {

        int selectedId = chipStage.getCheckedChipId();

        if (selectedId == R.id.chipSeed) {
            return "GIEO_HAT";
        }

        if (selectedId == R.id.chipSprout) {
            return "NAY_MAM";
        }

        if (selectedId == R.id.chipGrowing) {
            return "DANG_PHAT_TRIEN";
        }

        return "DANG_PHAT_TRIEN";
    }

   

    private void save() {

        String name = etName.getText() == null
                ? ""
                : etName.getText().toString().trim();

        String note = etNote.getText() == null
                ? ""
                : etNote.getText().toString();

        // Kiểm tra tên cây
        if (name.isEmpty()) {

            etName.setError("Vui lòng nhập tên cây");
            etName.requestFocus();

            return;
        }

        // Tạo đối tượng cây
        MyPlant plant = new MyPlant();

        // Giữ nguyên cơ chế kiểm thử hiện tại
        plant.setUserId(null);

        plant.setPlantName(name);

        plant.setPlantId(selectedPlantId);

        plant.setPlantingDate(
                ChuoiUtil.toKey(plantingDate)
        );

        plant.setNote(note.trim());

        plant.setStatus(getSelectedStage());

        // Khóa nút trong lúc lưu
        btnSave.setEnabled(false);

        try {

            // Lưu cây vào bộ nhớ cục bộ
            LocalTestStore.addPlant(plant);

            Toast.makeText(
                    this,
                    "Đã thêm cây thành công!",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

        } catch (Exception e) {

            btnSave.setEnabled(true);

            Toast.makeText(
                    this,
                    "Không thể thêm cây: " + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();
        }
    }

   

    private static class SuggestionAdapter extends ArrayAdapter<String> {

        private final List<String> all;

        private final List<String> shown = new ArrayList<>();

        SuggestionAdapter(Context context, List<String> names) {

            super(
                    context,
                    android.R.layout.simple_dropdown_item_1line
            );

            this.all = new ArrayList<>(names);
        }

        @Override
        public int getCount() {
            return shown.size();
        }

        @Override
        public String getItem(int position) {
            return shown.get(position);
        }

        @NonNull
        @Override
        public Filter getFilter() {

            return new Filter() {

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {

                    String keyword = TextUtil.normalize(
                            constraint == null
                                    ? ""
                                    : constraint.toString()
                    );

                    List<String> resultList = new ArrayList<>();

                    // Không hiển thị danh sách khi chưa nhập ký tự
                    if (!keyword.isEmpty()) {

                        for (String name : all) {

                            if (TextUtil.normalize(name).contains(keyword)) {
                                resultList.add(name);
                            }
                        }
                    }

                    FilterResults results = new FilterResults();

                    results.values = resultList;
                    results.count = resultList.size();

                    return results;
                }

                @Override
                @SuppressWarnings("unchecked")
                protected void publishResults(
                        CharSequence constraint,
                        FilterResults results
                ) {

                    shown.clear();

                    if (results.values != null) {

                        shown.addAll(
                                (List<String>) results.values
                        );
                    }

                    notifyDataSetChanged();
                }

                @Override
                public CharSequence convertResultToString(Object resultValue) {
                    return (String) resultValue;
                }
            };
        }
    }
}
