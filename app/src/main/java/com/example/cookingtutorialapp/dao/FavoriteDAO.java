package com.example.cookingtutorialapp.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.cookingtutorialapp.database.DatabaseHelper;
import com.example.cookingtutorialapp.models.Recipe;

import java.util.ArrayList;
import java.util.List;

/**
 * FavoriteDAO - Lớp truy xuất dữ liệu công thức yêu thích
 *
 * Lớp này cung cấp các phương thức để quản lý danh sách công thức yêu thích của người dùng,
 * bao gồm thêm vào yêu thích, xóa khỏi yêu thích, kiểm tra trạng thái yêu thích và lấy
 * danh sách công thức yêu thích.
 */
public class FavoriteDAO {
    private DatabaseHelper dbHelper;  // Đối tượng hỗ trợ truy cập cơ sở dữ liệu
    private Context context;  // Context của ứng dụng

    public FavoriteDAO(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);  // Khởi tạo helper để thao tác với database
    }

    public long addToFavorites(int userId, int recipeId) {
        // Mở kết nối ghi cơ sở dữ liệu
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Tạo đối tượng ContentValues để lưu các giá trị cần thêm
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_USER_ID, userId);  // ID người dùng
        values.put(DatabaseHelper.COLUMN_RECIPE_ID, recipeId);  // ID công thức

        // Thêm bản ghi vào bảng yêu thích và lấy ID bản ghi vừa thêm
        long id = db.insert(DatabaseHelper.TABLE_FAVORITES, null, values);
        db.close();  // Đóng kết nối cơ sở dữ liệu

        return id;  // Trả về ID bản ghi vừa thêm
    }

    public int removeFromFavorites(int userId, int recipeId) {
        // Mở kết nối ghi cơ sở dữ liệu
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Thực hiện lệnh xóa với điều kiện cả userId và recipeId
        int rowsAffected = db.delete(
                DatabaseHelper.TABLE_FAVORITES,  // Tên bảng
                DatabaseHelper.COLUMN_USER_ID + "=? AND " + DatabaseHelper.COLUMN_RECIPE_ID + "=?",  // Điều kiện WHERE
                new String[]{String.valueOf(userId), String.valueOf(recipeId)}  // Tham số cho điều kiện WHERE
        );

        db.close();  // Đóng kết nối cơ sở dữ liệu
        return rowsAffected;  // Trả về số dòng bị ảnh hưởng
    }

    public boolean checkFavorite(int userId, int recipeId) {
        // Mở kết nối đọc cơ sở dữ liệu
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Thực hiện truy vấn kiểm tra sự tồn tại của bản ghi
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_FAVORITES,  // Tên bảng
                new String[]{DatabaseHelper.COLUMN_ID},  // Cột cần lấy
                DatabaseHelper.COLUMN_USER_ID + "=? AND " + DatabaseHelper.COLUMN_RECIPE_ID + "=?",  // Điều kiện WHERE
                new String[]{String.valueOf(userId), String.valueOf(recipeId)},  // Tham số cho điều kiện WHERE
                null, null, null  // groupBy, having, orderBy
        );

        // Kiểm tra xem có kết quả trả về không
        boolean isFavorite = cursor != null && cursor.getCount() > 0;

        // Đóng cursor nếu không null
        if (cursor != null) {
            cursor.close();
        }

        db.close();  // Đóng kết nối cơ sở dữ liệu
        return isFavorite;  // Trả về kết quả kiểm tra
    }

    public List<Recipe> getFavoriteRecipes(int userId) {
        List<Recipe> favoritesList = new ArrayList<>();  // Danh sách kết quả

        // Câu lệnh SQL JOIN để lấy thông tin công thức từ bảng yêu thích
        String selectQuery = "SELECT r.* FROM " + DatabaseHelper.TABLE_RECIPES + " r " +
                "INNER JOIN " + DatabaseHelper.TABLE_FAVORITES + " f " +
                "ON r." + DatabaseHelper.COLUMN_ID + " = f." + DatabaseHelper.COLUMN_RECIPE_ID + " " +
                "WHERE f." + DatabaseHelper.COLUMN_USER_ID + " = " + userId;

        // Mở kết nối đọc cơ sở dữ liệu
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        // Thực thi câu lệnh SQL và lấy con trỏ kết quả
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Duyệt qua từng dòng kết quả
        if (cursor.moveToFirst()) {
            do {
                // Tạo đối tượng Recipe từ dữ liệu trong cursor
                Recipe recipe = new Recipe(
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RECIPE_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RECIPE_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RECIPE_IMAGE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RECIPE_COOKING_TIME))
                );

                // Đánh dấu là công thức yêu thích
                recipe.setFavorite(true);

                // Thêm đối tượng vào danh sách kết quả
                favoritesList.add(recipe);
            } while (cursor.moveToNext());  // Di chuyển đến dòng tiếp theo (nếu có)
        }

        cursor.close();  // Đóng cursor
        db.close();  // Đóng kết nối cơ sở dữ liệu

        return favoritesList;  // Trả về danh sách kết quả
    }
}