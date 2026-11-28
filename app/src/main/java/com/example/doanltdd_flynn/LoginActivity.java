package com.example.myapptrongcay;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText edtEmail, edtPassword;
    private MaterialButton btnLogin;
    private ProgressBar progress;

    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progress = findViewById(R.id.progress);

        AuthValidator.clearErrorOnTyping(tilEmail, edtEmail);
        AuthValidator.clearErrorOnTyping(tilPassword, edtPassword);

        btnLogin.setOnClickListener(v -> attemptLogin());

        // Bấm "Xong" trên bàn phím cũng đăng nhập
        edtPassword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                attemptLogin();
                return true;
            }
            return false;
        });

        findViewById(R.id.tvGoRegister).setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));

        TextView tvForgot = findViewById(R.id.tvForgotPassword);
        tvForgot.setOnClickListener(v ->
                Toast.makeText(this, "Tính năng quên mật khẩu đang được phát triển", Toast.LENGTH_SHORT).show());
    }

    private void attemptLogin() {
        String email = getText(edtEmail);
        String password = getText(edtPassword);

        String emailError = AuthValidator.validateEmail(email);
        String passwordError = AuthValidator.validatePassword(password);
        tilEmail.setError(emailError);
        tilPassword.setError(passwordError);

        if (emailError != null) { edtEmail.requestFocus(); return; }
        if (passwordError != null) { edtPassword.requestFocus(); return; }

        setLoading(true);
        performLogin(email, password);
    }

    /**
     * TODO: thay bằng lời gọi thật (Firebase Auth / API server).
     * Hiện tại chỉ giả lập chờ 0,8 giây rồi coi như đăng nhập thành công.
     */
    private void performLogin(String email, String password) {
        handler.postDelayed(() -> {
            setLoading(false);
            onLoginSuccess(null);
        }, 800);
    }

    private void onLoginSuccess(String displayName) {
        Intent intent = new Intent(this, MainActivity.class);
        if (displayName != null) intent.putExtra(MainActivity.EXTRA_USER_NAME, displayName);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setLoading(boolean loading) {
        btnLogin.setEnabled(!loading);
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
