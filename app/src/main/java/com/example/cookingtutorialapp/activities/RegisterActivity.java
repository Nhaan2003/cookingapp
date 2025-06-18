package com.example.cookingtutorialapp.activities;

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

/**
 * RegisterActivity - Màn hình đăng ký tài khoản người dùng mới
 *
 * Chức năng chính:
 * - Thu thập thông tin cá nhân từ người dùng: tên đăng nhập, email, mật khẩu
 * - Xác thực tính hợp lệ của thông tin nhập vào
 * - Kiểm tra email đã tồn tại chưa để tránh trùng lặp
 * - Lưu thông tin người dùng vào cơ sở dữ liệu
 * - Chuyển hướng về màn hình đăng nhập sau khi đăng ký thành công
 */
public class RegisterActivity extends AppCompatActivity {
    // Khai báo các thành phần giao diện
    private EditText etUsername, etEmail, etPassword, etConfirmPassword;  // Các trường nhập liệu
    private Button btnRegister;  // Nút đăng ký
    private TextView tvLogin;    // Liên kết quay lại đăng nhập

    // Khai báo đối tượng DAO để thao tác với cơ sở dữ liệu người dùng
    private UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Khởi tạo đối tượng DAO để thao tác với bảng User trong cơ sở dữ liệu
        userDAO = new UserDAO(this);

        // Ánh xạ các thành phần giao diện từ layout
        etUsername = findViewById(R.id.et_username);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnRegister = findViewById(R.id.btn_register);
        tvLogin = findViewById(R.id.tv_login);

        // Thiết lập sự kiện click cho nút đăng ký
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy thông tin từ các trường nhập liệu và loại bỏ khoảng trắng thừa
                String username = etUsername.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String confirmPassword = etConfirmPassword.getText().toString().trim();

                // Kiểm tra xem có trường thông tin nào bị bỏ trống không
                if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Kiểm tra mật khẩu và xác nhận mật khẩu có khớp nhau không
                if (!password.equals(confirmPassword)) {
                    Toast.makeText(RegisterActivity.this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Kiểm tra email đã tồn tại trong hệ thống chưa
                if (userDAO.checkUserExist(email)) {
                    Toast.makeText(RegisterActivity.this, "Email đã được sử dụng", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Tạo đối tượng User mới từ thông tin đã nhập
                User user = new User(username, email, password);

                // Thực hiện thêm người dùng vào cơ sở dữ liệu
                long result = userDAO.insertUser(user);

                // Kiểm tra kết quả thêm người dùng
                if (result > 0) {
                    // Nếu thành công (ID trả về > 0)
                    Toast.makeText(RegisterActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                    finish();  // Đóng màn hình đăng ký và quay về màn hình trước đó (màn hình đăng nhập)
                } else {
                    // Nếu thất bại (ID trả về <= 0)
                    Toast.makeText(RegisterActivity.this, "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Thiết lập sự kiện click cho liên kết đăng nhập
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();  // Đóng màn hình đăng ký và quay về màn hình trước đó (màn hình đăng nhập)
            }
        });
    }
}