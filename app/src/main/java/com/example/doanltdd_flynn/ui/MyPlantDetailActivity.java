package com.example.doanltdd_flynn.ui;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doanltdd_flynn.R;
import com.example.doanltdd_flynn.adapter.CareLogAdapter;
import com.example.doanltdd_flynn.model.CareLog;
import com.example.doanltdd_flynn.model.CareType;
import com.example.doanltdd_flynn.model.MyPlant;
import com.example.doanltdd_flynn.model.StreakState;
import com.example.doanltdd_flynn.repository.LocalTestStore;
import com.example.doanltdd_flynn.utils.ChuoiUtil;
import com.example.doanltdd_flynn.utils.UiUtil;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class MyPlantDetailActivity extends AppCompatActivity {

    public static final String EXTRA_PLANT_ID = "plant_id";

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
    private TextView tvSelectedDateHeader;
    private TextView tvCalendarMonth;

    private TextView tvInfoPlantName;
    private TextView tvInfoNickname;
    private TextView tvInfoPlantingDate;
    private TextView tvInfoStatus;
    private TextView tvInfoNote;

    // Ô nhập và nút lưu tên thân mật
    private EditText edtNickname;
    private MaterialButton btnSaveNickname;

    private RecyclerView rvLogs;

    private MaterialButton btnAddLog;
    private MaterialButton btnTabJournal;
    private MaterialButton btnTabInfo;

    private View layoutJournal;
    private View layoutInfo;

    private TextView btnPrevMonth;
    private TextView btnNextMonth;

    private GridLayout layoutCalendarGrid;

    private LocalDate selectedDate;
    private YearMonth displayedMonth;

    private List<CareLog> allLogs = new ArrayList<>();

    private static final int GREEN = Color.rgb(67, 135, 90);
    private static final int GREEN_DARK = Color.rgb(39, 103, 62);
    private static final int GREEN_LIGHT = Color.rgb(226, 242, 230);
    private static final int BLACK = Color.rgb(40, 40, 40);
    private static final int GREY = Color.rgb(185, 185, 185);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_plant_detail);

        UiUtil.applyEdgeToEdge(
                this,
                findViewById(R.id.root)
        );

        selectedDate = ChuoiUtil.todayDate();
        displayedMonth = YearMonth.from(selectedDate);

        plantId = getIntent().getStringExtra(EXTRA_PLANT_ID);

        if (plantId == null || plantId.trim().isEmpty()) {
            Toast.makeText(
                    this,
                    "Không tìm thấy mã cây.",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        bindViews();
        setupToolbar();
        setupRecyclerView();
        setupCalendarButtons();
        setupTabs();
        setupAddLogButton();
        setupNicknameEditor();

        renderCalendar();
        renderSelectedDateLogs();
    }

   

    private void bindViews() {

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
        tvSelectedDateHeader = findViewById(R.id.tvSelectedDateHeader);
        tvCalendarMonth = findViewById(R.id.tvCalendarMonth);

        rvLogs = findViewById(R.id.rvLogs);

        btnAddLog = findViewById(R.id.btnAddLog);

        btnTabJournal = findViewById(R.id.btnTabJournal);
        btnTabInfo = findViewById(R.id.btnTabInfo);

        layoutJournal = findViewById(R.id.layoutJournal);
        layoutInfo = findViewById(R.id.layoutInfo);

        btnPrevMonth = findViewById(R.id.btnPrevMonth);
        btnNextMonth = findViewById(R.id.btnNextMonth);

        layoutCalendarGrid = findViewById(R.id.layoutCalendarGrid);

        tvInfoPlantName = findViewById(R.id.tvInfoPlantName);
        tvInfoNickname = findViewById(R.id.tvInfoNickname);
        tvInfoPlantingDate = findViewById(R.id.tvInfoPlantingDate);
        tvInfoStatus = findViewById(R.id.tvInfoStatus);
        tvInfoNote = findViewById(R.id.tvInfoNote);

        edtNickname = findViewById(R.id.edtNickname);
        btnSaveNickname = findViewById(R.id.btnSaveNickname);
    }

   

    private void setupToolbar() {

        toolbar.setNavigationOnClickListener(v -> finish());

        toolbar.inflateMenu(R.menu.menu_my_plant_detail);

        toolbar.setOnMenuItemClickListener(item -> {

            if (item.getItemId() == R.id.action_delete) {
                confirmDeletePlant();
                return true;
            }

            return false;
        });
    }

   

    private void setupNicknameEditor() {

        btnSaveNickname.setOnClickListener(v -> saveNickname());
    }

    private void saveNickname() {

        if (currentPlant == null) {
            return;
        }

        String nickname = edtNickname.getText().toString().trim();

        // Cập nhật trực tiếp đối tượng cây đang được LocalTestStore lưu.
        currentPlant.setNickname(nickname);

        // Cập nhật ngay phần thông tin cây.
        if (nickname.isEmpty()) {
            tvInfoNickname.setText("Chưa đặt tên");
        } else {
            tvInfoNickname.setText(nickname);
        }

        // Ẩn bàn phím sau khi lưu.
        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm != null) {
            imm.hideSoftInputFromWindow(
                    edtNickname.getWindowToken(),
                    0
            );
        }

        edtNickname.clearFocus();

        Toast.makeText(
                this,
                nickname.isEmpty()
                        ? "Đã xóa tên gọi thân mật."
                        : "Đã lưu tên gọi thân mật.",
                Toast.LENGTH_SHORT
        ).show();
    }

   

    private void setupRecyclerView() {

        adapter = new CareLogAdapter(new CareLogAdapter.Listener() {

            @Override
            public void onEdit(CareLog log) {

                Intent intent = new Intent(
                        MyPlantDetailActivity.this,
                        AddCareLogActivity.class
                );

                intent.putExtra(AddCareLogActivity.EXTRA_PLANT_ID, plantId);
                intent.putExtra(AddCareLogActivity.EXTRA_LOG_ID, log.getCareLogId());
                intent.putExtra(AddCareLogActivity.EXTRA_LOG_DATE, log.getCareDate());

                intent.putExtra(
                        AddCareLogActivity.EXTRA_LOG_TYPE,
                        log.getCareType() == null
                                ? "OTHER"
                                : log.getCareType().name()
                );

                intent.putExtra(
                        AddCareLogActivity.EXTRA_LOG_NOTE,
                        log.getNote()
                );

                ArrayList<String> careTypes = new ArrayList<>();

                if (log.getCareTypes() != null) {
                    for (CareType type : log.getCareTypes()) {
                        if (type != null) {
                            careTypes.add(type.name());
                        }
                    }
                }

                intent.putStringArrayListExtra(
                        AddCareLogActivity.EXTRA_LOG_CARE_TYPES,
                        careTypes
                );

                intent.putExtra(
                        AddCareLogActivity.EXTRA_LOG_CONDITION,
                        log.getPlantCondition()
                );

                intent.putStringArrayListExtra(
                        AddCareLogActivity.EXTRA_LOG_IMAGE_URLS,
                        new ArrayList<>(log.getImageUrls())
                );

                startActivity(intent);
            }

            @Override
            public void onDelete(CareLog log) {
                confirmDeleteLog(log);
            }
        });

        rvLogs.setLayoutManager(new LinearLayoutManager(this));
        rvLogs.setAdapter(adapter);
    }

  

    private void setupCalendarButtons() {

        btnPrevMonth.setOnClickListener(v -> {
            displayedMonth = displayedMonth.minusMonths(1);
            renderCalendar();
        });

        btnNextMonth.setOnClickListener(v -> {

            YearMonth currentMonth = YearMonth.from(
                    ChuoiUtil.todayDate()
            );

            if (displayedMonth.isBefore(currentMonth)) {
                displayedMonth = displayedMonth.plusMonths(1);
                renderCalendar();
            }
        });
    }

   

    private void setupTabs() {

        btnTabJournal.setOnClickListener(v -> showJournalTab());

        btnTabInfo.setOnClickListener(v -> showInfoTab());

        showJournalTab();
    }

    private void showJournalTab() {

        layoutJournal.setVisibility(View.VISIBLE);
        layoutInfo.setVisibility(View.GONE);

        btnTabJournal.setBackgroundTintList(
                ColorStateList.valueOf(GREEN)
        );

        btnTabJournal.setTextColor(Color.WHITE);

        btnTabInfo.setBackgroundTintList(
                ColorStateList.valueOf(Color.TRANSPARENT)
        );

        btnTabInfo.setTextColor(GREEN);
    }

    private void showInfoTab() {

        layoutJournal.setVisibility(View.GONE);
        layoutInfo.setVisibility(View.VISIBLE);

        btnTabInfo.setBackgroundTintList(
                ColorStateList.valueOf(GREEN)
        );

        btnTabInfo.setTextColor(Color.WHITE);

        btnTabJournal.setBackgroundTintList(
                ColorStateList.valueOf(Color.TRANSPARENT)
        );

        btnTabJournal.setTextColor(GREEN);
    }

   

    private void setupAddLogButton() {

        btnAddLog.setOnClickListener(v -> {

            if (selectedDate.isAfter(ChuoiUtil.todayDate())) {

                Toast.makeText(
                        this,
                        "Không thể ghi nhật ký cho ngày trong tương lai.",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            Intent intent = new Intent(
                    this,
                    AddCareLogActivity.class
            );

            intent.putExtra(
                    AddCareLogActivity.EXTRA_PLANT_ID,
                    plantId
            );

            intent.putExtra(
                    AddCareLogActivity.EXTRA_SELECTED_DATE,
                    ChuoiUtil.toKey(selectedDate)
            );

            startActivity(intent);
        });
    }

  

    @Override
    protected void onResume() {
        super.onResume();

        if (plantId == null) {
            return;
        }

        loadPlant();
        loadLogs();
    }

    private void loadPlant() {

        MyPlant plant = LocalTestStore.getPlant(plantId);

        if (plant == null) {

            Toast.makeText(
                    this,
                    "Không tìm thấy thông tin cây.",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        currentPlant = plant;

        renderPlant(plant);
    }

    private void loadLogs() {

        List<CareLog> logs = LocalTestStore.getCareLogs(plantId);

        if (logs == null) {
            allLogs = new ArrayList<>();
        } else {
            allLogs = new ArrayList<>(logs);
        }

        renderCalendar();
        renderSelectedDateLogs();
    }

   

    private void renderPlant(MyPlant p) {

        toolbar.setTitle(p.getPlantName());

        tvName.setText(p.getPlantName());

        tvPlantingDate.setText(
                "Bắt đầu trồng: " + ChuoiUtil.display(p.getPlantingDate())
        );

        // Hiển thị tên thân mật đã lưu trong ô nhập.
        String nickname = p.getNickname();

        if (nickname == null) {
            nickname = "";
        }

        edtNickname.setText(nickname);
        edtNickname.setSelection(edtNickname.getText().length());

        tvStreak.setText(
                String.valueOf(
                        ChuoiUtil.calculateCurrentStreak(p.getCareDays())
                )
        );

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

        tvStreakState.setTextColor(
                ContextCompat.getColor(this, color)
        );

        tvLongest.setText(
                String.valueOf(
                        ChuoiUtil.calculateLongestStreak(p.getCareDays())
                )
        );

        tvTotalCare.setText(
                String.valueOf(
                        ChuoiUtil.countCareDays(p.getCareDays())
                )
        );

        tvCompanion.setText(
                String.valueOf(
                        ChuoiUtil.calculateCompanionDays(p.getPlantingDate())
                )
        );

        String note = p.getNote();

        if (note == null || note.trim().isEmpty()) {
            tvNote.setVisibility(View.GONE);
        } else {
            tvNote.setVisibility(View.VISIBLE);
            tvNote.setText("Ghi chú: " + note);
        }

        // Tab thông tin cây

        tvInfoPlantName.setText(
                p.getPlantName() == null
                        ? "Chưa có thông tin"
                        : p.getPlantName()
        );

        tvInfoNickname.setText(
                nickname.trim().isEmpty()
                        ? "Chưa đặt tên"
                        : nickname
        );

        tvInfoPlantingDate.setText(
                ChuoiUtil.display(p.getPlantingDate())
        );

        String status = p.getStatus();

        if (status == null || status.trim().isEmpty()) {
            tvInfoStatus.setText("Chưa có thông tin");
        } else if ("DANG_TRONG".equals(status)) {
            tvInfoStatus.setText("Đang trồng");
        } else {
            tvInfoStatus.setText(status);
        }

        tvInfoNote.setText(
                note == null || note.trim().isEmpty()
                        ? "Chưa có ghi chú"
                        : note
        );
    }

   

    private void renderCalendar() {

        layoutCalendarGrid.removeAllViews();

        tvCalendarMonth.setText(
                "Tháng " + displayedMonth.getMonthValue()
                        + ", " + displayedMonth.getYear()
        );

        YearMonth currentMonth = YearMonth.from(
                ChuoiUtil.todayDate()
        );

        btnNextMonth.setEnabled(displayedMonth.isBefore(currentMonth));

        btnNextMonth.setAlpha(
                displayedMonth.isBefore(currentMonth) ? 1f : 0.4f
        );

        int daysInMonth = displayedMonth.lengthOfMonth();

        // Thứ 2 = 0, thứ 3 = 1, ..., chủ nhật = 6

        int firstDayOffset = displayedMonth.atDay(1)
                .getDayOfWeek()
                .getValue() - 1;

        for (int i = 0; i < 42; i++) {

            int dayNumber = i - firstDayOffset + 1;

            if (dayNumber < 1 || dayNumber > daysInMonth) {

                View emptyCell = new View(this);

                GridLayout.LayoutParams emptyParams =
                        new GridLayout.LayoutParams();

                emptyParams.width = 0;
                emptyParams.height = dpToPx(52);

                emptyParams.columnSpec =
                        GridLayout.spec(i % 7, 1f);

                emptyParams.rowSpec =
                        GridLayout.spec(i / 7);

                layoutCalendarGrid.addView(emptyCell, emptyParams);

                continue;
            }

            LocalDate date = displayedMonth.atDay(dayNumber);

            addCalendarDay(date, i);
        }
    }

  

    private void addCalendarDay(LocalDate date, int position) {

        boolean isSelected = date.equals(selectedDate);

        boolean isToday = date.equals(ChuoiUtil.todayDate());

        boolean isFuture = date.isAfter(ChuoiUtil.todayDate());

        boolean hasLog = hasLogOnDate(date);

        LinearLayout dayLayout = new LinearLayout(this);

        dayLayout.setOrientation(LinearLayout.VERTICAL);
        dayLayout.setGravity(Gravity.CENTER);

        GridLayout.LayoutParams params =
                new GridLayout.LayoutParams();

        params.width = 0;
        params.height = dpToPx(52);

        params.columnSpec =
                GridLayout.spec(position % 7, 1f);

        params.rowSpec =
                GridLayout.spec(position / 7);

        params.setMargins(
                dpToPx(2),
                dpToPx(2),
                dpToPx(2),
                dpToPx(2)
        );

        // Tạo nền riêng cho từng ngày

        dayLayout.setBackground(
                createDayBackground(isSelected, hasLog, isToday)
        );

        // Số ngày

        TextView tvDay = new TextView(this);

        tvDay.setText(String.valueOf(date.getDayOfMonth()));

        tvDay.setGravity(Gravity.CENTER);

        tvDay.setTextSize(14);

        if (isSelected) {

            tvDay.setTextColor(Color.WHITE);
            tvDay.setTypeface(null, android.graphics.Typeface.BOLD);

        } else if (isFuture) {

            tvDay.setTextColor(GREY);

        } else if (hasLog) {

            tvDay.setTextColor(GREEN_DARK);
            tvDay.setTypeface(null, android.graphics.Typeface.BOLD);

        } else if (isToday) {

            tvDay.setTextColor(GREEN);
            tvDay.setTypeface(null, android.graphics.Typeface.BOLD);

        } else {

            tvDay.setTextColor(BLACK);
        }

        LinearLayout.LayoutParams dayParams =
                new LinearLayout.LayoutParams(
                        dpToPx(34),
                        dpToPx(32)
                );

        dayLayout.addView(tvDay, dayParams);

        // Dấu chấm cho ngày có nhật ký

        TextView tvDot = new TextView(this);

        tvDot.setText(hasLog ? "●" : "");

        tvDot.setTextSize(8);

        tvDot.setGravity(Gravity.CENTER);

        if (isSelected) {
            tvDot.setTextColor(Color.WHITE);
        } else {
            tvDot.setTextColor(GREEN);
        }

        LinearLayout.LayoutParams dotParams =
                new LinearLayout.LayoutParams(
                        dpToPx(20),
                        dpToPx(12)
                );

        dayLayout.addView(tvDot, dotParams);

        // Chỉ cho phép chọn ngày hôm nay và ngày trong quá khứ

        if (!isFuture) {

            dayLayout.setClickable(true);
            dayLayout.setFocusable(true);

            dayLayout.setOnClickListener(v -> {

                selectedDate = date;
                displayedMonth = YearMonth.from(date);

                renderCalendar();
                renderSelectedDateLogs();
            });

        } else {

            dayLayout.setAlpha(0.55f);
        }

        layoutCalendarGrid.addView(dayLayout, params);
    }

    

    private GradientDrawable createDayBackground(
            boolean isSelected,
            boolean hasLog,
            boolean isToday
    ) {

        GradientDrawable background = new GradientDrawable();

        background.setShape(GradientDrawable.RECTANGLE);

        background.setCornerRadius(dpToPx(10));

        if (isSelected) {

            // Ngày đang được chọn

            background.setColor(GREEN);

            background.setStroke(
                    dpToPx(1),
                    GREEN_DARK
            );

        } else if (hasLog) {

            // Ngày đã có nhật ký

            background.setColor(GREEN_LIGHT);

            background.setStroke(
                    dpToPx(1),
                    Color.rgb(158, 202, 168)
            );

        } else if (isToday) {

            // Hôm nay nhưng chưa có nhật ký

            background.setColor(Color.WHITE);

            background.setStroke(
                    dpToPx(2),
                    GREEN
            );

        } else {

            // Ngày chưa có nhật ký

            background.setColor(Color.TRANSPARENT);
        }

        return background;
    }

   

    private boolean hasLogOnDate(LocalDate date) {

        String dateKey = ChuoiUtil.toKey(date);

        for (CareLog log : allLogs) {

            if (log.getCareDate() != null
                    && log.getCareDate().equals(dateKey)) {

                return true;
            }
        }

        return false;
    }

    

    private void renderSelectedDateLogs() {

        String dateKey = ChuoiUtil.toKey(selectedDate);

        String displayDate = ChuoiUtil.display(dateKey);

        tvSelectedDateHeader.setText(
                "Nhật ký ngày " + displayDate
        );

        List<CareLog> selectedLogs = new ArrayList<>();

        for (CareLog log : allLogs) {

            if (log.getCareDate() != null
                    && log.getCareDate().equals(dateKey)) {

                selectedLogs.add(log);
            }
        }

        adapter.submitList(selectedLogs);

        if (selectedLogs.isEmpty()) {

            tvEmptyLogs.setVisibility(View.VISIBLE);
            rvLogs.setVisibility(View.GONE);

            tvEmptyLogs.setText(
                    "Ngày " + displayDate
                            + " chưa có nhật ký chăm sóc."
            );

        } else {

            tvEmptyLogs.setVisibility(View.GONE);
            rvLogs.setVisibility(View.VISIBLE);
        }

        if (selectedDate.isAfter(ChuoiUtil.todayDate())) {

            btnAddLog.setEnabled(false);
            btnAddLog.setText("Chưa thể ghi nhật ký cho ngày tương lai");

        } else if (selectedLogs.isEmpty()) {

            btnAddLog.setEnabled(true);
            btnAddLog.setText("Ghi nhật ký ngày " + displayDate);

        } else {

            btnAddLog.setEnabled(true);
            btnAddLog.setText("Ghi thêm nhật ký ngày " + displayDate);
        }

        renderCalendar();
    }

   

    private int dpToPx(int dp) {

        return (int) (
                dp * getResources().getDisplayMetrics().density + 0.5f
        );
    }

   

    private void confirmDeleteLog(CareLog log) {

        new MaterialAlertDialogBuilder(this)

                .setTitle("Xóa nhật ký?")

                .setMessage(
                        "Xóa nhật ký không làm thay đổi chuỗi của cây."
                )

                .setNegativeButton("Hủy", null)

                .setPositiveButton("Xóa", (dialog, which) -> {

                    LocalTestStore.deleteCareLog(
                            plantId,
                            log.getCareLogId()
                    );

                    loadLogs();

                    Toast.makeText(
                            this,
                            "Đã xóa nhật ký.",
                            Toast.LENGTH_SHORT
                    ).show();
                })

                .show();
    }

   

    private void confirmDeletePlant() {

        if (currentPlant == null) {
            return;
        }

        new MaterialAlertDialogBuilder(this)

                .setTitle(
                        "Xóa cây \"" + currentPlant.getPlantName() + "\"?"
                )

                .setMessage(
                        "Cây và toàn bộ nhật ký sẽ bị xóa và không thể mở lại.\n\n"
                                + "Flynn sẽ lưu một thẻ kỷ niệm \"Bạn đã ở bên "
                                + currentPlant.getPlantName()
                                + " được "
                                + ChuoiUtil.calculateCompanionDays(
                                currentPlant.getPlantingDate()
                        )
                                + " ngày\" vào Lịch sử trồng cây. "
                                + "Kỷ lục chuỗi của bạn vẫn được giữ nguyên."
                )

                .setNegativeButton("Hủy", null)

                .setPositiveButton("Xóa cây", (dialog, which) -> {

                    LocalTestStore.deletePlant(plantId);

                    Toast.makeText(
                            this,
                            "Đã xóa cây.",
                            Toast.LENGTH_SHORT
                    ).show();

                    finish();
                })

                .show();
    }
}
