package com.example.cookingtutorialapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cookingtutorialapp.R;
import com.example.cookingtutorialapp.dao.UserDAO;
import com.example.cookingtutorialapp.models.User;
import com.example.cookingtutorialapp.utils.SessionManager;

/**
 * Màn hình đăng nhập của ứng dụng hướng dẫn nấu ăn
 * Cho phép người dùng đăng nhập vào ứng dụng bằng email và mật khẩu
 */
public class LoginActivity extends AppCompatActivity {
    // Khai báo các thành phần UI
    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister;

    // Khai báo các đối tượng xử lý dữ liệu
    private UserDAO userDAO;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Khởi tạo đối tượng truy cập dữ liệu người dùng
        userDAO = new UserDAO(this);
        // Khởi tạo quản lý phiên đăng nhập
        sessionManager = new SessionManager(this);

        // Kiểm tra nếu người dùng đã đăng nhập
        if (sessionManager.isLoggedIn()) {
            // Nếu đã đăng nhập thì chuyển đến màn hình chính
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        // Liên kết các biến với các thành phần UI trong layout
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvRegister = findViewById(R.id.tv_register);

        // Thiết lập sự kiện click cho nút đăng nhập
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy thông tin đăng nhập từ người dùng
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                // Kiểm tra các trường không được để trống
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Kiểm tra thông tin đăng nhập
                if (userDAO.checkUser(email, password)) {
                    // Lấy thông tin người dùng từ database
                    User user = userDAO.getUserByEmail(email);
                    // Tạo phiên đăng nhập mới
                    sessionManager.createLoginSession(user.getId(), user.getUsername(), user.getEmail());

                    // Chuyển đến màn hình chính
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    // Thông báo đăng nhập thất bại
                    Toast.makeText(LoginActivity.this, "Email hoặc mật khẩu không chính xác", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Thiết lập sự kiện click cho liên kết đăng ký
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển đến màn hình đăng ký
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }
}