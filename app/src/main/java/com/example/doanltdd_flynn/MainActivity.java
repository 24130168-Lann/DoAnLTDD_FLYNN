package com.example.myapptrongcay;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_USER_NAME = "user_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Nếu vừa đăng ký xong thì hiện tên người dùng ở lời chào
        String name = getIntent().getStringExtra(EXTRA_USER_NAME);
        if (name != null && !name.isEmpty()) {
            TextView tvGreeting = findViewById(R.id.tvGreeting);
            tvGreeting.setText("Xin chào, " + name);
        }
    }
}