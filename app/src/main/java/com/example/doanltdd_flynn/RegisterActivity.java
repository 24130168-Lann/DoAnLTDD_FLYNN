package com.example.myapptrongcay;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout tilName, tilEmail, tilPassword, tilConfirm;
    private TextInputEditText edtName, edtEmail, edtPassword, edtConfirm;
    private MaterialButton btnRegister;
    private ProgressBar progress;

    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        tilName = findViewById(R.id.tilName);
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        tilConfirm = findViewById(R.id.tilConfirm);
        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirm = findViewById(R.id.edtConfirm);
        btnRegister = findViewById(R.id.btnRegister);
        progress = findViewById(R.id.progress);

        AuthValidator.clearErrorOnTyping(tilName, edtName);
        AuthValidator.clearErrorOnTyping(tilEmail, edtEmail);
        AuthValidator.clearErrorOnTyping(tilPassword, edtPassword);
        AuthValidator.clearErrorOnTyping(tilConfirm, edtConfirm);

        btnRegister.setOnClickListener(v -> attemptRegister());

        edtConfirm.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                attemptRegister();
                return true;
            }
            return false;
        });

        // Quay lại màn hình đăng nhập (đang nằm dưới trong back stack)
        findViewById(R.id.tvGoLogin).setOnClickListener(v -> finish());
    }

    private void attemptRegister() {
        String name = getText(edtName);
        String email = getText(edtEmail);
        String password = getText(edtPassword);
        String confirm = getText(edtConfirm);

        String nameError = AuthValidator.validateName(name);
        String emailError = AuthValidator.validateEmail(email);
        String passwordError = AuthValidator.validatePassword(password);
        String confirmError = AuthValidator.validateConfirm(password, confirm);

        tilName.setError(nameError);
        tilEmail.setError(emailError);
        tilPassword.setError(passwordError);
        tilConfirm.setError(confirmError);

        if (nameError != null) { edtName.requestFocus(); return; }
        if (emailError != null) { edtEmail.requestFocus(); return; }
        if (passwordError != null) { edtPassword.requestFocus(); return; }
        if (confirmError != null) { edtConfirm.requestFocus(); return; }

        setLoading(true);
        performRegister(name, email, password);
    }

    /**
     * TODO: thay bằng lời gọi thật (Firebase Auth / API server).
     * Hiện tại chỉ giả lập chờ 0,8 giây rồi coi như đăng ký thành công.
     */
    private void performRegister(String name, String email, String password) {
        handler.postDelayed(() -> {
            setLoading(false);
            onRegisterSuccess(name);
        }, 800);
    }

    private void onRegisterSuccess(String name) {
        Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_USER_NAME, name);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setLoading(boolean loading) {
        btnRegister.setEnabled(!loading);
        progress.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    private String getText(TextInputEditText e) {
        return e.getText() == null ? "" : e.getText().toString().trim();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
