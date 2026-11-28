package com.example.myapptrongcay;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;

/** Các hàm kiểm tra dữ liệu dùng chung cho màn hình đăng nhập / đăng ký. */
public final class AuthValidator {

    public static final int MIN_PASSWORD_LENGTH = 6;

    private AuthValidator() {}

    /** Trả về chuỗi lỗi, hoặc null nếu hợp lệ. */
    public static String validateName(String name) {
        if (name.isEmpty()) return "Vui lòng nhập tên hiển thị";
        if (name.length() < 2) return "Tên cần ít nhất 2 ký tự";
        return null;
    }

    public static String validateEmail(String email) {
        if (email.isEmpty()) return "Vui lòng nhập email";
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) return "Email không hợp lệ";
        return null;
    }

    public static String validatePassword(String password) {
        if (password.isEmpty()) return "Vui lòng nhập mật khẩu";
        if (password.length() < MIN_PASSWORD_LENGTH)
            return "Mật khẩu cần ít nhất " + MIN_PASSWORD_LENGTH + " ký tự";
        return null;
    }

    public static String validateConfirm(String password, String confirm) {
        if (confirm.isEmpty()) return "Vui lòng nhập lại mật khẩu";
        if (!confirm.equals(password)) return "Mật khẩu nhập lại không khớp";
        return null;
    }

    /** Tự xóa thông báo lỗi khi người dùng bắt đầu gõ lại. */
    public static void clearErrorOnTyping(final TextInputLayout layout, EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {}
            @Override public void afterTextChanged(Editable s) { layout.setError(null); }
        });
    }
}
