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
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AddMyPlantActivity extends AppCompatActivity {

    private static final int MAX_CHIPS = 10;

    private MyPlantController myPlantController;

    private MaterialAutoCompleteTextView actName;
    private TextInputEditText etDate;
    private TextInputEditText etNote;

    private ChipGroup chipSuggestions;
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

        // Khởi tạo Controller để tải danh sách gợi ý cây.
        myPlantController = new MyPlantController();

        // Thanh tiêu đề
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Ánh xạ giao diện
        actName = findViewById(R.id.actName);
        etDate = findViewById(R.id.etDate);
        etNote = findViewById(R.id.etNote);

        chipSuggestions = findViewById(R.id.chipSuggestions);
        chipStage = findViewById(R.id.chipStage);

        btnSave = findViewById(R.id.btnSave);

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

        // Theo dõi tên cây được nhập
        actName.addTextChangedListener(new TextWatcher() {

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

                selectedPlantId = findPlantId(
                        s.toString()
                );
            }
        });

        // Nút lưu cây
        btnSave.setOnClickListener(v -> save());

        // Tải danh sách gợi ý
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

                    for (PlantSuggestion p : suggestions) {
                        names.add(p.getName());
                    }

                    actName.setAdapter(
                            new SuggestionAdapter(this, names)
                    );

                    chipSuggestions.removeAllViews();

                    int count = 0;

                    for (PlantSuggestion p : suggestions) {

                        if (count++ >= MAX_CHIPS) {
                            break;
                        }

                        Chip chip = new Chip(this);

                        chip.setText(p.getName());

                        chip.setOnClickListener(v -> {

                            actName.setText(
                                    p.getName(),
                                    false
                            );

                            actName.setSelection(
                                    p.getName().length()
                            );
                        });

                        chipSuggestions.addView(chip);
                    }
                },

                e -> {
                    // Không có gợi ý vẫn cho phép nhập tên cây thủ công.
                }
        );
    }

   

    private String findPlantId(String typedName) {

        String key = TextUtil.normalize(typedName);

        if (key.isEmpty()) {
            return null;
        }

        for (PlantSuggestion p : suggestions) {

            if (TextUtil.normalize(p.getName()).equals(key)) {
                return p.getPlantId();
            }
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

        // Nếu không có lựa chọn thì mặc định là Phát triển
        return "DANG_PHAT_TRIEN";
    }

   

    private void save() {

        String name = actName.getText() == null
                ? ""
                : actName.getText().toString().trim();

        String note = etNote.getText() == null
                ? ""
                : etNote.getText().toString();

        // Kiểm tra tên cây
        if (name.isEmpty()) {

            actName.setError("Vui lòng nhập tên cây");

            actName.requestFocus();

            return;
        }

        // Tạo đối tượng cây
        MyPlant plant = new MyPlant();

        // Không sử dụng tài khoản giả trong chế độ kiểm thử
        plant.setUserId(null);

        plant.setPlantName(name);

        plant.setPlantId(selectedPlantId);

        plant.setPlantingDate(
                ChuoiUtil.toKey(plantingDate)
        );

        plant.setNote(note.trim());

        // Lưu đúng giai đoạn người dùng đã chọn
        plant.setStatus(getSelectedStage());

        // Khóa nút trong lúc lưu
        btnSave.setEnabled(false);

        try {

            // Lưu cây vào bộ nhớ cục bộ, không gọi Firebase
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

            this.shown.addAll(all);
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

                    String q = TextUtil.normalize(
                            constraint == null
                                    ? ""
                                    : constraint.toString()
                    );

                    List<String> out = new ArrayList<>();

                    for (String s : all) {

                        if (q.isEmpty()
                                || TextUtil.normalize(s).contains(q)) {

                            out.add(s);
                        }
                    }

                    FilterResults results = new FilterResults();

                    results.values = out;

                    results.count = out.size();

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
