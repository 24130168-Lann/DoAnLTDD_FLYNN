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

            // Khoảng trống của thanh trạng thái và thanh điều hướng.
            Insets systemBars = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
            );

            // Khoảng trống của bàn phím ảo.
            Insets ime = insets.getInsets(
                    WindowInsetsCompat.Type.ime()
            );

            // Khi bàn phím mở, dùng khoảng trống lớn hơn để tránh bị che nội dung.
            int bottomInset = Math.max(systemBars.bottom, ime.bottom);

            v.setPadding(
                    systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    bottomInset
            );

            return insets;
        });

        // Yêu cầu hệ thống cập nhật lại vùng hiển thị khi bàn phím mở/đóng.
        ViewCompat.requestApplyInsets(root);
    }

    public static void pickDate(
            Context context,
            LocalDate initial,
            Consumer<LocalDate> onPicked
    ) {

        LocalDate base = initial == null
                ? ChuoiUtil.todayDate()
                : initial;

        DatePickerDialog dialog = new DatePickerDialog(
                context,
                (view, year, month, day) ->
                        onPicked.accept(LocalDate.of(year, month + 1, day)),
                base.getYear(),
                base.getMonthValue() - 1,
                base.getDayOfMonth()
        );

        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        dialog.show();
    }

   
    public static String careTypeLabel(CareType type) {

        if (type == null) {
            return "Việc khác";
        }

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

        for (int i = 0; i < types.length; i++) {
            labels[i] = careTypeLabel(types[i]);
        }

        return labels;
    }

    public static CareType careTypeFromLabel(String label) {

        for (CareType type : CareType.values()) {

            if (careTypeLabel(type).equals(label)) {
                return type;
            }
        }

        return CareType.OTHER;
    }
}
