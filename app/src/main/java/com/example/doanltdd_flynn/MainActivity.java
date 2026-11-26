package com.example.doanltdd_flynn;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.doanltdd_flynn.ui.MyPlantsFragment;
import com.example.doanltdd_flynn.ui.PlaceholderFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

   
    private static final boolean DEV_AUTO_ANONYMOUS_LOGIN = true;

    private static final String KEY_TAB = "selected_tab";

    private BottomNavigationView bottomNav;
    private int selectedTab = R.id.nav_my_plants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bottomNav = findViewById(R.id.bottom_nav);
        if (savedInstanceState != null) {
            selectedTab = savedInstanceState.getInt(KEY_TAB, R.id.nav_my_plants);
        }

        // SỬA: dựng thanh điều hướng và nội dung tab NGAY, không chờ đăng nhập.
        // (Bản cũ chỉ dựng sau khi đăng nhập ẩn danh xong nên máy ảo chưa có mạng thì
        // màn hình chỉ có thanh điều hướng, không hiện gì khác.)
        setupNavigation();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null && DEV_AUTO_ANONYMOUS_LOGIN) {
            auth.signInAnonymously().addOnCompleteListener(task -> {
                if (isFinishing() || isDestroyed()) return;
                if (!task.isSuccessful()) {
                    String reason = task.getException() == null ? "" : task.getException().getMessage();
                    Toast.makeText(this, "Chưa đăng nhập được: " + reason
                                    + "\nKiểm tra internet của máy ảo và bật Anonymous trong Firebase Authentication.",
                            Toast.LENGTH_LONG).show();
                }
                openFragment(selectedTab); // nạp lại tab hiện tại để nhận người dùng mới
            });
        }
    }

    private void setupNavigation() {
        bottomNav.setOnItemSelectedListener(item -> {
            openFragment(item.getItemId());
            return true;
        });
        bottomNav.setSelectedItemId(selectedTab); // gọi luôn openFragment
    }

    /** Chuyển giữa 5 tab. */
    public void openFragment(int menuId) {
        selectedTab = menuId;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, createFragment(menuId))
                .commitAllowingStateLoss();
    }

    private Fragment createFragment(int menuId) {
        if (menuId == R.id.nav_my_plants) {
            return new MyPlantsFragment();
        } else if (menuId == R.id.nav_home) {
            return PlaceholderFragment.newInstance("Trang chủ\n(bạn phụ trách Trang chủ sẽ thay phần này)");
        } else if (menuId == R.id.nav_encyclopedia) {
            return PlaceholderFragment.newInstance("Bách khoa\n(bạn phụ trách Bách khoa sẽ thay phần này)");
        } else if (menuId == R.id.nav_community) {
            return PlaceholderFragment.newInstance("Cộng đồng\n(bạn phụ trách Cộng đồng sẽ thay phần này)");
        }
        return PlaceholderFragment.newInstance("Cá nhân\n(bạn phụ trách Cá nhân sẽ thay phần này)");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_TAB, selectedTab);
    }
}
