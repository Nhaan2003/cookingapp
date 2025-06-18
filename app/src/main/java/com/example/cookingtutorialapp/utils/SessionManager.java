package com.example.cookingtutorialapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Lớp tiện ích quản lý phiên làm việc của người dùng
 * - Lưu trữ và quản lý thông tin đăng nhập
 * - Kiểm tra trạng thái đăng nhập
 * - Cung cấp thông tin người dùng cho toàn ứng dụng
 */
public class SessionManager {
    // Tên của SharedPreferences
    private static final String PREF_NAME = "CookingTutorialPref";
    // Các khóa để lưu và truy xuất dữ liệu
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";

    // Đối tượng SharedPreferences để lưu trữ dữ liệu
    private SharedPreferences pref;
    // Editor để thực hiện các thao tác chỉnh sửa SharedPreferences
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        // MODE_PRIVATE đảm bảo chỉ ứng dụng này mới có thể truy cập dữ liệu
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void createLoginSession(int userId, String username, String email) {
        // Đánh dấu người dùng đã đăng nhập
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        // Lưu các thông tin cơ bản của người dùng
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_EMAIL, email);
        // Áp dụng thay đổi và lưu vào bộ nhớ ngay lập tức
        editor.commit();
    }

    public boolean isLoggedIn() {
        // Mặc định là false nếu khóa không tồn tại
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void logoutUser() {
        // Xóa tất cả dữ liệu trong SharedPreferences
        editor.clear();
        // Áp dụng thay đổi và lưu vào bộ nhớ
        editor.commit();
    }

    public int getUserId() {
        // Mặc định là -1 nếu không tìm thấy
        return pref.getInt(KEY_USER_ID, -1);
    }

    public String getUsername() {
        // Mặc định là null nếu không tìm thấy
        return pref.getString(KEY_USERNAME, null);
    }

    public String getEmail() {
        // Mặc định là null nếu không tìm thấy
        return pref.getString(KEY_EMAIL, null);
    }
}