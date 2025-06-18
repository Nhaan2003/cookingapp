package com.example.cookingtutorialapp.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.cookingtutorialapp.database.DatabaseHelper;
import com.example.cookingtutorialapp.models.Ingredient;
import com.example.cookingtutorialapp.models.Recipe;
import com.example.cookingtutorialapp.models.Step;

import java.util.ArrayList;
import java.util.List;

/**
 * RecipeDAO - Lớp truy xuất dữ liệu công thức nấu ăn
 *
 * Lớp này cung cấp các phương thức để quản lý công thức nấu ăn, bao gồm thêm, cập nhật, xóa,
 * tìm kiếm và lấy thông tin công thức. Đây là lớp trung tâm xử lý dữ liệu công thức trong ứng dụng.
 */
public class RecipeDAO {
    private DatabaseHelper dbHelper;  // Đối tượng hỗ trợ truy cập cơ sở dữ liệu
    private Context context;  // Context của ứng dụng

    public RecipeDAO(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);  // Khởi tạo helper để thao tác với database
    }

    public long insertRecipe(Recipe recipe) {
        // Mở kết nối ghi cơ sở dữ liệu
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Tạo đối tượng ContentValues để lưu các giá trị cần thêm
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_RECIPE_NAME, recipe.getRecipeName());  // Tên công thức
        values.put(DatabaseHelper.COLUMN_RECIPE_DESCRIPTION, recipe.getDescription());  // Mô tả
        values.put(DatabaseHelper.COLUMN_RECIPE_IMAGE, recipe.getImage());  // Đường dẫn hình ảnh
        values.put(DatabaseHelper.COLUMN_CATEGORY_ID, recipe.getCategoryId());  // ID danh mục
        values.put(DatabaseHelper.COLUMN_USER_ID, recipe.getUserId());  // ID người dùng tạo
        values.put(DatabaseHelper.COLUMN_RECIPE_COOKING_TIME, recipe.getCookingTime());  // Thời gian nấu

        // Thêm công thức vào bảng và lấy ID công thức vừa thêm
        long recipeId = db.insert(DatabaseHelper.TABLE_RECIPES, null, values);

        // Nếu thêm công thức thành công (ID > -1), thêm các bước và nguyên liệu
        if (recipeId != -1) {
            // Thêm các bước nấu ăn
            StepDAO stepDAO = new StepDAO(context);
            for (Step step : recipe.getSteps()) {
                step.setRecipeId((int) recipeId);  // Gán ID công thức vừa thêm cho bước
                stepDAO.insertStep(step);  // Thêm bước vào cơ sở dữ liệu
            }

            // Thêm các nguyên liệu
            IngredientDAO ingredientDAO = new IngredientDAO(context);
            for (Ingredient ingredient : recipe.getIngredients()) {
                ingredient.setRecipeId((int) recipeId);  // Gán ID công thức vừa thêm cho nguyên liệu
                ingredientDAO.insertIngredient(ingredient);  // Thêm nguyên liệu vào cơ sở dữ liệu
            }
        }

        db.close();  // Đóng kết nối cơ sở dữ liệu
        return recipeId;  // Trả về ID công thức vừa thêm
    }

    public Recipe getRecipeById(int id, int userId) {
        // Mở kết nối đọc cơ sở dữ liệu
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Thực hiện truy vấn để lấy thông tin công thức theo ID
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_RECIPES,  // Tên bảng
                new String[]{
                        DatabaseHelper.COLUMN_ID,
                        DatabaseHelper.COLUMN_RECIPE_NAME,
                        DatabaseHelper.COLUMN_RECIPE_DESCRIPTION,
                        DatabaseHelper.COLUMN_RECIPE_IMAGE,
                        DatabaseHelper.COLUMN_CATEGORY_ID,
                        DatabaseHelper.COLUMN_USER_ID,
                        DatabaseHelper.COLUMN_RECIPE_COOKING_TIME
                },  // Các cột cần lấy
                DatabaseHelper.COLUMN_ID + "=?",  // Điều kiện WHERE
                new String[]{String.valueOf(id)},  // Tham số cho điều kiện WHERE
                null, null, null  // groupBy, having, orderBy
        );

        Recipe recipe = null;  // Khởi tạo kết quả là null

        // Nếu tìm thấy công thức
        if (cursor != null && cursor.moveToFirst()) {
            // Tạo đối tượng Recipe từ dữ liệu trong cursor
            recipe = new Recipe(
                    cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RECIPE_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RECIPE_DESCRIPTION)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RECIPE_IMAGE)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_ID)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RECIPE_COOKING_TIME))
            );
            cursor.close();  // Đóng cursor

            // Tải các bước nấu ăn
            StepDAO stepDAO = new StepDAO(context);
            recipe.setSteps(stepDAO.getStepsByRecipeId(recipe.getId()));

            // Tải danh sách nguyên liệu
            IngredientDAO ingredientDAO = new IngredientDAO(context);
            recipe.setIngredients(ingredientDAO.getIngredientsByRecipeId(recipe.getId()));

            // Kiểm tra xem công thức có được yêu thích không
            FavoriteDAO favoriteDAO = new FavoriteDAO(context);
            recipe.setFavorite(favoriteDAO.checkFavorite(userId, recipe.getId()));
        }

        db.close();  // Đóng kết nối cơ sở dữ liệu
        return recipe;  // Trả về công thức tìm được hoặc null
    }

    public List<Recipe> getAllRecipes(int userId) {
        List<Recipe> recipeList = new ArrayList<>();  // Danh sách kết quả

        // Câu lệnh SQL để lấy tất cả công thức
        String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLE_RECIPES;

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

                // Kiểm tra xem công thức có được yêu thích không
                FavoriteDAO favoriteDAO = new FavoriteDAO(context);
                recipe.setFavorite(favoriteDAO.checkFavorite(userId, recipe.getId()));

                // Thêm vào danh sách kết quả
                recipeList.add(recipe);
            } while (cursor.moveToNext());  // Di chuyển đến dòng tiếp theo (nếu có)
        }

        cursor.close();  // Đóng cursor
        db.close();  // Đóng kết nối cơ sở dữ liệu

        return recipeList;  // Trả về danh sách công thức
    }

    public List<Recipe> getRecipesByCategory(int categoryId, int userId) {
        List<Recipe> recipeList = new ArrayList<>();  // Danh sách kết quả

        // Mở kết nối đọc cơ sở dữ liệu
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Thực hiện truy vấn để lấy các công thức theo danh mục
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_RECIPES,  // Tên bảng
                new String[]{
                        DatabaseHelper.COLUMN_ID,
                        DatabaseHelper.COLUMN_RECIPE_NAME,
                        DatabaseHelper.COLUMN_RECIPE_DESCRIPTION,
                        DatabaseHelper.COLUMN_RECIPE_IMAGE,
                        DatabaseHelper.COLUMN_CATEGORY_ID,
                        DatabaseHelper.COLUMN_USER_ID,
                        DatabaseHelper.COLUMN_RECIPE_COOKING_TIME
                },  // Các cột cần lấy
                DatabaseHelper.COLUMN_CATEGORY_ID + "=?",  // Điều kiện WHERE
                new String[]{String.valueOf(categoryId)},  // Tham số cho điều kiện WHERE
                null, null, null  // groupBy, having, orderBy
        );

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

                // Kiểm tra xem công thức có được yêu thích không
                FavoriteDAO favoriteDAO = new FavoriteDAO(context);
                recipe.setFavorite(favoriteDAO.checkFavorite(userId, recipe.getId()));

                // Thêm vào danh sách kết quả
                recipeList.add(recipe);
            } while (cursor.moveToNext());  // Di chuyển đến dòng tiếp theo (nếu có)
        }

        cursor.close();  // Đóng cursor
        db.close();  // Đóng kết nối cơ sở dữ liệu

        return recipeList;  // Trả về danh sách công thức thuộc danh mục
    }

    public List<Recipe> searchRecipes(String keyword, int userId) {
        List<Recipe> recipeList = new ArrayList<>();  // Danh sách kết quả

        // Mở kết nối đọc cơ sở dữ liệu
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Câu lệnh SQL tìm kiếm công thức theo tên hoặc mô tả
        String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLE_RECIPES +
                " WHERE " + DatabaseHelper.COLUMN_RECIPE_NAME + " LIKE '%" + keyword + "%' OR " +
                DatabaseHelper.COLUMN_RECIPE_DESCRIPTION + " LIKE '%" + keyword + "%'";

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

                // Kiểm tra xem công thức có được yêu thích không
                FavoriteDAO favoriteDAO = new FavoriteDAO(context);
                recipe.setFavorite(favoriteDAO.checkFavorite(userId, recipe.getId()));

                // Thêm vào danh sách kết quả
                recipeList.add(recipe);
            } while (cursor.moveToNext());  // Di chuyển đến dòng tiếp theo (nếu có)
        }

        cursor.close();  // Đóng cursor
        db.close();  // Đóng kết nối cơ sở dữ liệu

        return recipeList;  // Trả về danh sách công thức phù hợp
    }

    public int updateRecipe(Recipe recipe) {
        // Mở kết nối ghi cơ sở dữ liệu
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Tạo đối tượng ContentValues để lưu các giá trị cần cập nhật
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_RECIPE_NAME, recipe.getRecipeName());  // Tên công thức
        values.put(DatabaseHelper.COLUMN_RECIPE_DESCRIPTION, recipe.getDescription());  // Mô tả
        values.put(DatabaseHelper.COLUMN_RECIPE_IMAGE, recipe.getImage());  // Đường dẫn hình ảnh
        values.put(DatabaseHelper.COLUMN_CATEGORY_ID, recipe.getCategoryId());  // ID danh mục
        values.put(DatabaseHelper.COLUMN_USER_ID, recipe.getUserId());  // ID người dùng tạo
        values.put(DatabaseHelper.COLUMN_RECIPE_COOKING_TIME, recipe.getCookingTime());  // Thời gian nấu

        // Cập nhật công thức trong bảng
        int rowsAffected = db.update(
                DatabaseHelper.TABLE_RECIPES,  // Tên bảng
                values,  // Giá trị cần cập nhật
                DatabaseHelper.COLUMN_ID + "=?",  // Điều kiện WHERE
                new String[]{String.valueOf(recipe.getId())}  // Tham số cho điều kiện WHERE
        );

        db.close();  // Đóng kết nối cơ sở dữ liệu

        // Nếu cập nhật công thức thành công, cập nhật các bước và nguyên liệu
        if (rowsAffected > 0) {
            // Cập nhật các bước nấu ăn
            StepDAO stepDAO = new StepDAO(context);

            // Đầu tiên xóa tất cả các bước cũ
            stepDAO.deleteStepsByRecipeId(recipe.getId());

            // Sau đó thêm các bước mới
            for (Step step : recipe.getSteps()) {
                step.setRecipeId(recipe.getId());  // Gán ID công thức cho bước
                stepDAO.insertStep(step);  // Thêm bước vào cơ sở dữ liệu
            }

            // Cập nhật các nguyên liệu
            IngredientDAO ingredientDAO = new IngredientDAO(context);

            // Đầu tiên xóa tất cả các nguyên liệu cũ
            ingredientDAO.deleteIngredientsByRecipeId(recipe.getId());

            // Sau đó thêm các nguyên liệu mới
            for (Ingredient ingredient : recipe.getIngredients()) {
                ingredient.setRecipeId(recipe.getId());  // Gán ID công thức cho nguyên liệu
                ingredientDAO.insertIngredient(ingredient);  // Thêm nguyên liệu vào cơ sở dữ liệu
            }
        }

        return rowsAffected;  // Trả về số dòng bị ảnh hưởng
    }

    public int deleteRecipe(int recipeId) {
        // Mở kết nối ghi cơ sở dữ liệu
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Đầu tiên xóa các bản ghi liên quan
        db.delete(DatabaseHelper.TABLE_STEPS, DatabaseHelper.COLUMN_RECIPE_ID + "=?", new String[]{String.valueOf(recipeId)});
        db.delete(DatabaseHelper.TABLE_INGREDIENTS, DatabaseHelper.COLUMN_RECIPE_ID + "=?", new String[]{String.valueOf(recipeId)});
        db.delete(DatabaseHelper.TABLE_FAVORITES, DatabaseHelper.COLUMN_RECIPE_ID + "=?", new String[]{String.valueOf(recipeId)});

        // Sau đó xóa công thức
        int rowsAffected = db.delete(
                DatabaseHelper.TABLE_RECIPES,
                DatabaseHelper.COLUMN_ID + "=?",
                new String[]{String.valueOf(recipeId)}
        );

        db.close();  // Đóng kết nối cơ sở dữ liệu
        return rowsAffected;  // Trả về số dòng bị ảnh hưởng
    }
}