package com.example.cookingtutorialapp.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.cookingtutorialapp.database.DatabaseHelper;
import com.example.cookingtutorialapp.models.Category;

import java.util.ArrayList;
import java.util.List;

/**
 * CategoryDAO - Lớp truy xuất dữ liệu danh mục
 *
 * Lớp này cung cấp các phương thức để truy cập và thao tác với dữ liệu danh mục
 * trong cơ sở dữ liệu. Bao gồm các chức năng lấy tất cả danh mục và lấy danh mục
 * theo ID.
 */
public class CategoryDAO {
    private DatabaseHelper dbHelper;  // Đối tượng hỗ trợ truy cập cơ sở dữ liệu

    public CategoryDAO(Context context) {
        dbHelper = new DatabaseHelper(context);  // Khởi tạo helper để thao tác với database
    }

    public List<Category> getAllCategories() {
        List<Category> categoryList = new ArrayList<>();  // Danh sách kết quả

        // Câu lệnh SQL để lấy tất cả dữ liệu từ bảng danh mục
        String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLE_CATEGORIES;

        // Mở kết nối đọc cơ sở dữ liệu
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        // Thực thi câu lệnh SQL và lấy con trỏ kết quả
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Duyệt qua từng dòng kết quả
        if (cursor.moveToFirst()) {
            do {
                // Tạo đối tượng Category từ dữ liệu trong cursor
                Category category = new Category(
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_NAME))
                );
                // Thêm đối tượng vào danh sách kết quả
                categoryList.add(category);
            } while (cursor.moveToNext());  // Di chuyển đến dòng tiếp theo (nếu có)
        }

        // Đóng cursor để giải phóng tài nguyên
        cursor.close();
        // Đóng kết nối cơ sở dữ liệu
        db.close();

        return categoryList;  // Trả về danh sách kết quả
    }

    public Category getCategoryById(int id) {
        // Mở kết nối đọc cơ sở dữ liệu
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Thực hiện truy vấn có điều kiện để lấy danh mục theo ID
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_CATEGORIES,  // Tên bảng
                new String[]{DatabaseHelper.COLUMN_ID, DatabaseHelper.COLUMN_CATEGORY_NAME},  // Các cột cần lấy
                DatabaseHelper.COLUMN_ID + "=?",  // Điều kiện WHERE
                new String[]{String.valueOf(id)},  // Tham số cho điều kiện WHERE
                null, null, null  // groupBy, having, orderBy
        );

        Category category = null;  // Khởi tạo kết quả là null

        // Nếu tìm thấy kết quả
        if (cursor != null && cursor.moveToFirst()) {
            // Tạo đối tượng Category từ dữ liệu trong cursor
            category = new Category(
                    cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_NAME))
            );
            cursor.close();  // Đóng cursor
        }

        db.close();  // Đóng kết nối cơ sở dữ liệu
        return category;  // Trả về kết quả
    }
}