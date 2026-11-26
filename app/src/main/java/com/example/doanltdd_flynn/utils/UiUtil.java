package com.example.doanltdd_flynn.utils;

import android.app.DatePickerDialog;
import android.content.Context;
import android.view.View;

import androidx.activity.ComponentActivity;
import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.doanltdd_flynn.model.CareType;

import java.time.LocalDate;
import java.util.function.Consumer;


public final class UiUtil {
    private UiUtil() {
    }

   
    public static void applyEdgeToEdge(ComponentActivity activity, View root) {
        EdgeToEdge.enable(activity);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });
    }

    /** Hộp chọn ngày, không cho chọn ngày ở tương lai. */
    public static void pickDate(Context context, LocalDate initial, Consumer<LocalDate> onPicked) {
        LocalDate base = initial == null ? ChuoiUtil.todayDate() : initial;
        DatePickerDialog dialog = new DatePickerDialog(context,
                (view, year, month, day) -> onPicked.accept(LocalDate.of(year, month + 1, day)),
                base.getYear(), base.getMonthValue() - 1, base.getDayOfMonth());
        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        dialog.show();
    }

   
    public static String careTypeLabel(CareType type) {
        if (type == null) return "Việc khác";
        switch (type) {
            case WATERING:
                return "Tưới nước";
            case FERTILIZING:
                return "Bón phân";
            case PRUNING:
                return "Tỉa cành";
            default:
                return "Việc khác";
        }
    }

    public static String[] careTypeLabels() {
        CareType[] types = CareType.values();
        String[] labels = new String[types.length];
        for (int i = 0; i < types.length; i++) labels[i] = careTypeLabel(types[i]);
        return labels;
    }

    public static CareType careTypeFromLabel(String label) {
        for (CareType t : CareType.values()) {
            if (careTypeLabel(t).equals(label)) return t;
        }
        return CareType.OTHER;
    }
}
