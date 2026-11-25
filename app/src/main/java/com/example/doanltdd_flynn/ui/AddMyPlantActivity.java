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
import com.example.doanltdd_flynn.utils.ChuoiUtil;
import com.example.doanltdd_flynn.utils.TextUtil;
import com.example.doanltdd_flynn.utils.UiUtil;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class AddMyPlantActivity extends AppCompatActivity {

    private static final int MAX_CHIPS = 10;

    private MyPlantController myPlantController;
    private String userId;

    private MaterialAutoCompleteTextView actName;
    private TextInputEditText etDate;
    private TextInputEditText etNote;
    private ChipGroup chipSuggestions;
    private MaterialButton btnSave;

    private List<PlantSuggestion> suggestions = new ArrayList<>();
    private String selectedPlantId = null;           
    private LocalDate plantingDate = ChuoiUtil.todayDate();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_my_plant);
        UiUtil.applyEdgeToEdge(this, findViewById(R.id.root));

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để thêm cây", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        userId = user.getUid();
        myPlantController = new MyPlantController();

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        actName = findViewById(R.id.actName);
        etDate = findViewById(R.id.etDate);
        etNote = findViewById(R.id.etNote);
        chipSuggestions = findViewById(R.id.chipSuggestions);
        btnSave = findViewById(R.id.btnSave);

        etDate.setText(ChuoiUtil.display(ChuoiUtil.toKey(plantingDate)));
        etDate.setOnClickListener(v -> UiUtil.pickDate(this, plantingDate, date -> {
            plantingDate = date;
            etDate.setText(ChuoiUtil.display(ChuoiUtil.toKey(date)));
        }));

        actName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                selectedPlantId = findPlantId(s.toString());
            }
        });

        btnSave.setOnClickListener(v -> save());
        loadSuggestions();
    }

    private void loadSuggestions() {
        myPlantController.getPlantSuggestions(list -> {
            if (isFinishing()) return;
            suggestions = list;

            List<String> names = new ArrayList<>();
            for (PlantSuggestion p : suggestions) names.add(p.getName());
            actName.setAdapter(new SuggestionAdapter(this, names));

            chipSuggestions.removeAllViews();
            int count = 0;
            for (PlantSuggestion p : suggestions) {
                if (count++ >= MAX_CHIPS) break;
                Chip chip = new Chip(this);
                chip.setText(p.getName());
                chip.setOnClickListener(v -> {
                    actName.setText(p.getName(), false); // false: không bật lại danh sách gợi ý
                    actName.setSelection(p.getName().length());
                });
                chipSuggestions.addView(chip);
            }
        }, e -> { /* không tải được đề xuất thì người dùng vẫn nhập tên tay được */ });
    }

    private String findPlantId(String typedName) {
        String key = TextUtil.normalize(typedName);
        if (key.isEmpty()) return null;
        for (PlantSuggestion p : suggestions) {
            if (TextUtil.normalize(p.getName()).equals(key)) return p.getPlantId();
        }
        return null;
    }

    private void save() {
        String name = actName.getText() == null ? "" : actName.getText().toString();
        String note = etNote.getText() == null ? "" : etNote.getText().toString();

        MyPlant plant = new MyPlant();
        plant.setUserId(userId);
        plant.setPlantName(name);
        plant.setPlantId(selectedPlantId);
        plant.setPlantingDate(ChuoiUtil.toKey(plantingDate));
        plant.setNote(note.trim());

        btnSave.setEnabled(false);
        myPlantController.addMyPlant(plant,
                unused -> {
                    btnSave.setEnabled(true);
                    Toast.makeText(this, "Đã thêm cây. Chuỗi bắt đầu từ lần chăm đầu tiên!",
                            Toast.LENGTH_SHORT).show();
                    finish();
                },
                e -> {
                    btnSave.setEnabled(true);
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private static class SuggestionAdapter extends ArrayAdapter<String> {
        private final List<String> all;
        private final List<String> shown = new ArrayList<>();

        SuggestionAdapter(Context context, List<String> names) {
            super(context, android.R.layout.simple_dropdown_item_1line);
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
                    String q = TextUtil.normalize(constraint == null ? "" : constraint.toString());
                    List<String> out = new ArrayList<>();
                    for (String s : all) {
                        if (q.isEmpty() || TextUtil.normalize(s).contains(q)) out.add(s);
                    }
                    FilterResults r = new FilterResults();
                    r.values = out;
                    r.count = out.size();
                    return r;
                }

                @Override
                @SuppressWarnings("unchecked")
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    shown.clear();
                    if (results.values != null) shown.addAll((List<String>) results.values);
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
