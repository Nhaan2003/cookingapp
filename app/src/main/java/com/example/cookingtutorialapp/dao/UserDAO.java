package com.example.cookingtutorialapp.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.cookingtutorialapp.database.DatabaseHelper;
import com.example.cookingtutorialapp.models.User;

/**
 * UserDAO - Lớp truy xuất dữ liệu người dùng
 *
 * Lớp này cung cấp các phương thức để quản lý thông tin người dùng trong cơ sở dữ liệu,
 * bao gồm đăng ký, đăng nhập, lấy thông tin và cập nhật người dùng.
 */
public class UserDAO {
    private DatabaseHelper dbHelper;  // Đối tượng hỗ trợ truy cập cơ sở dữ liệu

    public UserDAO(Context context) {
        dbHelper = new DatabaseHelper(context);  // Khởi tạo helper để thao tác với database
    }

    public long insertUser(User user) {
        // Mở kết nối ghi cơ sở dữ liệu
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Tạo đối tượng ContentValues để lưu các giá trị cần thêm
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_USERNAME, user.getUsername());  // Tên đăng nhập
        values.put(DatabaseHelper.COLUMN_EMAIL, user.getEmail());  // Email
        values.put(DatabaseHelper.COLUMN_PASSWORD, user.getPassword());  // Mật khẩu

        // Thêm người dùng vào bảng và lấy ID bản ghi vừa thêm
        long id = db.insert(DatabaseHelper.TABLE_USERS, null, values);
        db.close();  // Đóng kết nối cơ sở dữ liệu

        return id;  // Trả về ID người dùng vừa thêm
    }

    public User getUserById(int id) {
        // Mở kết nối đọc cơ sở dữ liệu
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Thực hiện truy vấn có điều kiện để lấy thông tin người dùng theo ID
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_USERS,  // Tên bảng
                new String[]{DatabaseHelper.COLUMN_ID, DatabaseHelper.COLUMN_USERNAME, DatabaseHelper.COLUMN_EMAIL, DatabaseHelper.COLUMN_PASSWORD},  // Các cột cần lấy
                DatabaseHelper.COLUMN_ID + "=?",  // Điều kiện WHERE
                new String[]{String.valueOf(id)},  // Tham số cho điều kiện WHERE
                null, null, null);  // groupBy, having, orderBy

        User user = null;  // Khởi tạo kết quả là null

        // Nếu tìm thấy người dùng
        if (cursor != null && cursor.moveToFirst()) {
            // Tạo đối tượng User từ dữ liệu trong cursor
            user = new User(
                    cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USERNAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EMAIL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PASSWORD))
            );
            cursor.close();  // Đóng cursor
        }

        db.close();  // Đóng kết nối cơ sở dữ liệu
        return user;  // Trả về đối tượng User hoặc null
    }

    public User getUserByEmail(String email) {
        // Mở kết nối đọc cơ sở dữ liệu
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Thực hiện truy vấn có điều kiện để lấy thông tin người dùng theo email
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_USERS,  // Tên bảng
                new String[]{DatabaseHelper.COLUMN_ID, DatabaseHelper.COLUMN_USERNAME, DatabaseHelper.COLUMN_EMAIL, DatabaseHelper.COLUMN_PASSWORD},  // Các cột cần lấy
                DatabaseHelper.COLUMN_EMAIL + "=?",  // Điều kiện WHERE
                new String[]{email},  // Tham số cho điều kiện WHERE
                null, null, null);  // groupBy, having, orderBy

        User user = null;  // Khởi tạo kết quả là null

        // Nếu tìm thấy người dùng
        if (cursor != null && cursor.moveToFirst()) {
            // Tạo đối tượng User từ dữ liệu trong cursor
            user = new User(
                    cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USERNAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EMAIL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PASSWORD))
            );
            cursor.close();  // Đóng cursor
        }

        db.close();  // Đóng kết nối cơ sở dữ liệu
        return user;  // Trả về đối tượng User hoặc null
    }

    public int updateUser(User user) {
        // Mở kết nối ghi cơ sở dữ liệu
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Tạo đối tượng ContentValues để lưu các giá trị cần cập nhật
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_USERNAME, user.getUsername());  // Tên đăng nhập
        values.put(DatabaseHelper.COLUMN_EMAIL, user.getEmail());  // Email
        values.put(DatabaseHelper.COLUMN_PASSWORD, user.getPassword());  // Mật khẩu

        // Cập nhật thông tin người dùng trong bảng
        int rowsAffected = db.update(
                DatabaseHelper.TABLE_USERS,  // Tên bảng
                values,  // Giá trị cần cập nhật
                DatabaseHelper.COLUMN_ID + "=?",  // Điều kiện WHERE
                new String[]{String.valueOf(user.getId())}  // Tham số cho điều kiện WHERE
        );

        db.close();  // Đóng kết nối cơ sở dữ liệu
        return rowsAffected;  // Trả về số dòng bị ảnh hưởng
    }

    public boolean checkUser(String email, String password) {
        // Mở kết nối đọc cơ sở dữ liệu
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Thực hiện truy vấn kiểm tra sự tồn tại của người dùng với email và mật khẩu cung cấp
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_USERS,  // Tên bảng
                new String[]{DatabaseHelper.COLUMN_ID},  // Cột cần lấy
                DatabaseHelper.COLUMN_EMAIL + "=? AND " + DatabaseHelper.COLUMN_PASSWORD + "=?",  // Điều kiện WHERE
                new String[]{email, password},  // Tham số cho điều kiện WHERE
                null, null, null  // groupBy, having, orderBy
        );

        // Kiểm tra xem có kết quả trả về không
        boolean exists = cursor != null && cursor.getCount() > 0;

        // Đóng cursor nếu không null
        if (cursor != null) {
            cursor.close();
        }

        db.close();  // Đóng kết nối cơ sở dữ liệu
        return exists;  // Trả về kết quả kiểm tra
    }

    public boolean checkUserExist(String email) {
        // Mở kết nối đọc cơ sở dữ liệu
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Thực hiện truy vấn kiểm tra sự tồn tại của email
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_USERS,  // Tên bảng
                new String[]{DatabaseHelper.COLUMN_ID},  // Cột cần lấy
                DatabaseHelper.COLUMN_EMAIL + "=?",  // Điều kiện WHERE
                new String[]{email},  // Tham số cho điều kiện WHERE
                null, null, null  // groupBy, having, orderBy
        );

        // Kiểm tra xem có kết quả trả về không
        boolean exists = cursor != null && cursor.getCount() > 0;

        // Đóng cursor nếu không null
        if (cursor != null) {
            cursor.close();
        }

        db.close();  // Đóng kết nối cơ sở dữ liệu
        return exists;  // Trả về kết quả kiểm tra
    }
}