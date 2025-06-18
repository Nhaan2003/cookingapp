package com.example.cookingtutorialapp.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.cookingtutorialapp.database.DatabaseHelper;
import com.example.cookingtutorialapp.models.Ingredient;

import java.util.ArrayList;
import java.util.List;

/**
 * IngredientDAO - Lớp truy xuất dữ liệu nguyên liệu
 *
 * Lớp này cung cấp các phương thức để quản lý nguyên liệu của công thức và danh sách mua sắm,
 * bao gồm thêm, lấy, xóa nguyên liệu và các chức năng liên quan đến danh sách mua sắm.
 */
public class IngredientDAO {
    private DatabaseHelper dbHelper;  // Đối tượng hỗ trợ truy cập cơ sở dữ liệu

    public IngredientDAO(Context context) {
        dbHelper = new DatabaseHelper(context);  // Khởi tạo helper để thao tác với database
    }

    public long insertIngredient(Ingredient ingredient) {
        // Mở kết nối ghi cơ sở dữ liệu
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Tạo đối tượng ContentValues để lưu các giá trị cần thêm
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_RECIPE_ID, ingredient.getRecipeId());  // ID công thức
        values.put(DatabaseHelper.COLUMN_INGREDIENT_NAME, ingredient.getName());  // Tên nguyên liệu
        values.put(DatabaseHelper.COLUMN_INGREDIENT_QUANTITY, ingredient.getQuantity());  // Số lượng
        values.put(DatabaseHelper.COLUMN_INGREDIENT_UNIT, ingredient.getUnit());  // Đơn vị

        // Thêm bản ghi vào bảng nguyên liệu và lấy ID bản ghi vừa thêm
        long id = db.insert(DatabaseHelper.TABLE_INGREDIENTS, null, values);
        db.close();  // Đóng kết nối cơ sở dữ liệu

        return id;  // Trả về ID bản ghi vừa thêm
    }

    public List<Ingredient> getIngredientsByRecipeId(int recipeId) {
        List<Ingredient> ingredientList = new ArrayList<>();  // Danh sách kết quả

        // Mở kết nối đọc cơ sở dữ liệu
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Thực hiện truy vấn có điều kiện để lấy danh sách nguyên liệu theo recipeId
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_INGREDIENTS,  // Tên bảng
                new String[]{DatabaseHelper.COLUMN_ID, DatabaseHelper.COLUMN_RECIPE_ID,
                        DatabaseHelper.COLUMN_INGREDIENT_NAME, DatabaseHelper.COLUMN_INGREDIENT_QUANTITY,
                        DatabaseHelper.COLUMN_INGREDIENT_UNIT},  // Các cột cần lấy
                DatabaseHelper.COLUMN_RECIPE_ID + "=?",  // Điều kiện WHERE
                new String[]{String.valueOf(recipeId)},  // Tham số cho điều kiện WHERE
                null, null, null  // groupBy, having, orderBy
        );

        // Duyệt qua từng dòng kết quả
        if (cursor.moveToFirst()) {
            do {
                // Tạo đối tượng Ingredient từ dữ liệu trong cursor
                Ingredient ingredient = new Ingredient(
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RECIPE_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_INGREDIENT_NAME)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_INGREDIENT_QUANTITY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_INGREDIENT_UNIT))
                );
                // Thêm đối tượng vào danh sách kết quả
                ingredientList.add(ingredient);
            } while (cursor.moveToNext());  // Di chuyển đến dòng tiếp theo (nếu có)
        }

        cursor.close();  // Đóng cursor
        db.close();  // Đóng kết nối cơ sở dữ liệu

        return ingredientList;  // Trả về danh sách kết quả
    }

    public void deleteIngredientsByRecipeId(int recipeId) {
        // Mở kết nối ghi cơ sở dữ liệu
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Thực hiện lệnh xóa tất cả nguyên liệu của công thức
        db.delete(DatabaseHelper.TABLE_INGREDIENTS,
                DatabaseHelper.COLUMN_RECIPE_ID + "=?",
                new String[]{String.valueOf(recipeId)});

        db.close();  // Đóng kết nối cơ sở dữ liệu
    }

    public void addIngredientToShoppingList(int userId, int ingredientId) {
        // Mở kết nối ghi cơ sở dữ liệu
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Tạo đối tượng ContentValues để lưu các giá trị cần thêm
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_USER_ID, userId);  // ID người dùng
        values.put(DatabaseHelper.COLUMN_INGREDIENT_ID, ingredientId);  // ID nguyên liệu
        values.put(DatabaseHelper.COLUMN_IS_PURCHASED, 0);  // Ban đầu chưa mua (0 = false)

        // Thêm bản ghi vào bảng danh sách mua sắm
        db.insert(DatabaseHelper.TABLE_SHOPPING_LIST, null, values);
        db.close();  // Đóng kết nối cơ sở dữ liệu
    }

    public void updateShoppingListItemStatus(int shoppingListId, boolean purchased) {
        // Mở kết nối ghi cơ sở dữ liệu
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Tạo đối tượng ContentValues để lưu giá trị cần cập nhật
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_IS_PURCHASED, purchased ? 1 : 0);  // Chuyển boolean thành 1/0

        // Cập nhật trạng thái mua hàng cho mục trong danh sách mua sắm
        db.update(
                DatabaseHelper.TABLE_SHOPPING_LIST,  // Tên bảng
                values,  // Giá trị cần cập nhật
                DatabaseHelper.COLUMN_ID + "=?",  // Điều kiện WHERE
                new String[]{String.valueOf(shoppingListId)}  // Tham số cho điều kiện WHERE
        );

        db.close();  // Đóng kết nối cơ sở dữ liệu
    }

    public List<Ingredient> getShoppingList(int userId) {
        List<Ingredient> shoppingList = new ArrayList<>();  // Danh sách kết quả

        // Câu lệnh SQL JOIN để lấy thông tin nguyên liệu từ bảng danh sách mua sắm
        String selectQuery = "SELECT s." + DatabaseHelper.COLUMN_ID + ", i.* " +
                "FROM " + DatabaseHelper.TABLE_SHOPPING_LIST + " s " +
                "INNER JOIN " + DatabaseHelper.TABLE_INGREDIENTS + " i ON s." +
                DatabaseHelper.COLUMN_INGREDIENT_ID + "=i." + DatabaseHelper.COLUMN_ID + " " +
                "WHERE s." + DatabaseHelper.COLUMN_USER_ID + "=" + userId +
                " ORDER BY " + DatabaseHelper.COLUMN_INGREDIENT_NAME + " ASC";  // Sắp xếp theo tên

        // Mở kết nối đọc cơ sở dữ liệu
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        // Thực thi câu lệnh SQL và lấy con trỏ kết quả
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Duyệt qua từng dòng kết quả
        if (cursor.moveToFirst()) {
            do {
                // Tạo đối tượng Ingredient từ dữ liệu trong cursor
                Ingredient ingredient = new Ingredient(
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RECIPE_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_INGREDIENT_NAME)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_INGREDIENT_QUANTITY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_INGREDIENT_UNIT))
                );
                // Lấy ID của mục trong danh sách mua sắm
                ingredient.setId(cursor.getInt(0));

                // Lấy trạng thái đã mua hay chưa
                ingredient.setPurchased(getPurchasedStatus(cursor.getInt(0)));

                // Thêm đối tượng vào danh sách kết quả
                shoppingList.add(ingredient);
            } while (cursor.moveToNext());  // Di chuyển đến dòng tiếp theo (nếu có)
        }

        cursor.close();  // Đóng cursor
        db.close();  // Đóng kết nối cơ sở dữ liệu

        return shoppingList;  // Trả về danh sách kết quả
    }

    private boolean getPurchasedStatus(int shoppingListId) {
        // Mở kết nối đọc cơ sở dữ liệu
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Thực hiện truy vấn có điều kiện để lấy trạng thái mua hàng
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_SHOPPING_LIST,  // Tên bảng
                new String[]{DatabaseHelper.COLUMN_IS_PURCHASED},  // Cột cần lấy
                DatabaseHelper.COLUMN_ID + "=?",  // Điều kiện WHERE
                new String[]{String.valueOf(shoppingListId)},  // Tham số cho điều kiện WHERE
                null, null, null  // groupBy, having, orderBy
        );

        boolean purchased = false;  // Mặc định là chưa mua

        // Nếu có kết quả trả về, lấy giá trị trạng thái mua hàng
        if (cursor != null && cursor.moveToFirst()) {
            purchased = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IS_PURCHASED)) == 1;
            cursor.close();  // Đóng cursor
        }

        return purchased;  // Trả về trạng thái mua hàng
    }

    public boolean clearShoppingList(int userId) {
        // Mở kết nối ghi cơ sở dữ liệu
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            // Xóa tất cả các mục trong danh sách mua sắm của người dùng
            int rowsDeleted = db.delete(
                    DatabaseHelper.TABLE_SHOPPING_LIST,  // Tên bảng
                    DatabaseHelper.COLUMN_USER_ID + " = ?",  // Điều kiện WHERE
                    new String[]{String.valueOf(userId)}  // Tham số cho điều kiện WHERE
            );

            // Trả về true nếu có ít nhất một mục bị xóa hoặc danh sách đã trống
            return true;
        } catch (Exception e) {
            // Ghi log lỗi nếu có
            Log.e("IngredientDAO", "Lỗi khi xóa danh sách mua sắm: " + e.getMessage());
            return false;
        } finally {
            db.close();  // Đảm bảo đóng kết nối cơ sở dữ liệu
        }
    }
}