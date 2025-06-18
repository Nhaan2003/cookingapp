package com.example.cookingtutorialapp.activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.cookingtutorialapp.R;
import com.example.cookingtutorialapp.fragments.AccountFragment;
import com.example.cookingtutorialapp.fragments.AddRecipeFragment;
import com.example.cookingtutorialapp.fragments.RecipeListFragment;
import com.example.cookingtutorialapp.fragments.ShoppingListFragment;
import com.example.cookingtutorialapp.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * MainActivity - Màn hình chính của ứng dụng hướng dẫn nấu ăn
 * Quản lý điều hướng giữa các fragment chức năng thông qua thanh điều hướng dưới cùng
 * Các tính năng chính: Xem danh sách công thức, thêm công thức, danh sách mua sắm, thông tin tài khoản
 */
public class MainActivity extends AppCompatActivity {
    // Quản lý thông tin đăng nhập của người dùng
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khởi tạo đối tượng quản lý phiên đăng nhập
        sessionManager = new SessionManager(this);

        // Kiểm tra xem người dùng đã đăng nhập chưa
        if (!sessionManager.isLoggedIn()) {
            // Chuyển hướng về trang đăng nhập nếu chưa đăng nhập
            finish();
        }

        // Thiết lập điều hướng dưới cùng
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        // Thiết lập fragment mặc định khi khởi động ứng dụng
        if (savedInstanceState == null) {
            // Hiển thị danh sách công thức là màn hình đầu tiên
            getSupportFragmentManager().beginTransaction().replace(
                    R.id.fragment_container, new RecipeListFragment()).commit();
        }
    }

    /**
     * Xử lý sự kiện khi người dùng chọn các mục trong thanh điều hướng
     */
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    // Biến lưu fragment được chọn
                    Fragment selectedFragment = null;

                    // Xác định fragment tương ứng với mục được chọn
                    switch (item.getItemId()) {
                        case R.id.nav_recipes:
                            // Danh sách công thức nấu ăn
                            selectedFragment = new RecipeListFragment();
                            break;
                        case R.id.nav_shopping_list:
                            // Danh sách mua sắm nguyên liệu
                            selectedFragment = new ShoppingListFragment();
                            break;
                        case R.id.nav_add_recipe:
                            // Thêm công thức mới
                            selectedFragment = new AddRecipeFragment();
                            break;
                        case R.id.nav_account:
                            // Thông tin tài khoản người dùng
                            selectedFragment = new AccountFragment();
                            break;
                    }

                    // Thay thế fragment hiện tại bằng fragment được chọn
                    getSupportFragmentManager().beginTransaction().replace(
                            R.id.fragment_container, selectedFragment).commit();

                    return true; // Trả về true để hiển thị đã chọn mục
                }
            };
}