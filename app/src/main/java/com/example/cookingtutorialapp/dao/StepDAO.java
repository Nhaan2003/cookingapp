package com.example.cookingtutorialapp.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.cookingtutorialapp.database.DatabaseHelper;
import com.example.cookingtutorialapp.models.Step;

import java.util.ArrayList;
import java.util.List;

/**
 * StepDAO - Lớp truy xuất dữ liệu các bước nấu ăn
 *
 * Lớp này cung cấp các phương thức để quản lý các bước nấu ăn của công thức,
 * bao gồm thêm bước mới, lấy danh sách bước theo công thức và xóa các bước.
 */
public class StepDAO {
    private DatabaseHelper dbHelper;  // Đối tượng hỗ trợ truy cập cơ sở dữ liệu

    public StepDAO(Context context) {
        dbHelper = new DatabaseHelper(context);  // Khởi tạo helper để thao tác với database
    }

    public long insertStep(Step step) {
        // Mở kết nối ghi cơ sở dữ liệu
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Tạo đối tượng ContentValues để lưu các giá trị cần thêm
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_RECIPE_ID, step.getRecipeId());  // ID công thức
        values.put(DatabaseHelper.COLUMN_STEP_NUMBER, step.getStepNumber());  // Số thứ tự bước
        values.put(DatabaseHelper.COLUMN_STEP_DESCRIPTION, step.getDescription());  // Mô tả bước

        // Thêm bước vào bảng và lấy ID bản ghi vừa thêm
        long id = db.insert(DatabaseHelper.TABLE_STEPS, null, values);
        db.close();  // Đóng kết nối cơ sở dữ liệu

        return id;  // Trả về ID bản ghi vừa thêm
    }

    public List<Step> getStepsByRecipeId(int recipeId) {
        List<Step> stepList = new ArrayList<>();  // Danh sách kết quả

        // Mở kết nối đọc cơ sở dữ liệu
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Thực hiện truy vấn có điều kiện để lấy các bước của công thức
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_STEPS,  // Tên bảng
                new String[]{DatabaseHelper.COLUMN_ID, DatabaseHelper.COLUMN_RECIPE_ID,
                        DatabaseHelper.COLUMN_STEP_NUMBER, DatabaseHelper.COLUMN_STEP_DESCRIPTION},  // Các cột cần lấy
                DatabaseHelper.COLUMN_RECIPE_ID + "=?",  // Điều kiện WHERE
                new String[]{String.valueOf(recipeId)},  // Tham số cho điều kiện WHERE
                null, null, DatabaseHelper.COLUMN_STEP_NUMBER + " ASC"  // Sắp xếp theo số thứ tự tăng dần
        );

        // Duyệt qua từng dòng kết quả
        if (cursor.moveToFirst()) {
            do {
                // Tạo đối tượng Step từ dữ liệu trong cursor
                Step step = new Step(
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RECIPE_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STEP_NUMBER)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STEP_DESCRIPTION))
                );
                // Thêm đối tượng vào danh sách kết quả
                stepList.add(step);
            } while (cursor.moveToNext());  // Di chuyển đến dòng tiếp theo (nếu có)
        }

        cursor.close();  // Đóng cursor
        db.close();  // Đóng kết nối cơ sở dữ liệu

        return stepList;  // Trả về danh sách các bước
    }

    public void deleteStepsByRecipeId(int recipeId) {
        // Mở kết nối ghi cơ sở dữ liệu
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Thực hiện lệnh xóa tất cả các bước của công thức
        db.delete(DatabaseHelper.TABLE_STEPS, DatabaseHelper.COLUMN_RECIPE_ID + "=?",
                new String[]{String.valueOf(recipeId)});

        db.close();  // Đóng kết nối cơ sở dữ liệu
    }
}