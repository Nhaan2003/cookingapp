package com.example.cookingtutorialapp.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.cookingtutorialapp.database.DatabaseHelper;
import com.example.cookingtutorialapp.models.Recipe;

import java.util.ArrayList;
import java.util.List;

public class FavoriteDAO {
    private DatabaseHelper dbHelper;
    private Context context;

    public FavoriteDAO(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);
    }

    public long addToFavorites(int userId, int recipeId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_USER_ID, userId);
        values.put(DatabaseHelper.COLUMN_RECIPE_ID, recipeId);

        long id = db.insert(DatabaseHelper.TABLE_FAVORITES, null, values);
        db.close();

        return id;
    }

    public int removeFromFavorites(int userId, int recipeId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int rowsAffected = db.delete(
                DatabaseHelper.TABLE_FAVORITES,
                DatabaseHelper.COLUMN_USER_ID + "=? AND " + DatabaseHelper.COLUMN_RECIPE_ID + "=?",
                new String[]{String.valueOf(userId), String.valueOf(recipeId)}
        );

        db.close();
        return rowsAffected;
    }

    public boolean checkFavorite(int userId, int recipeId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_FAVORITES,
                new String[]{DatabaseHelper.COLUMN_ID},
                DatabaseHelper.COLUMN_USER_ID + "=? AND " + DatabaseHelper.COLUMN_RECIPE_ID + "=?",
                new String[]{String.valueOf(userId), String.valueOf(recipeId)},
                null, null, null
        );

        boolean isFavorite = cursor != null && cursor.getCount() > 0;

        if (cursor != null) {
            cursor.close();
        }

        db.close();
        return isFavorite;
    }

    public List<Recipe> getFavoriteRecipes(int userId) {
        List<Recipe> favoritesList = new ArrayList<>();

        String selectQuery = "SELECT r.* FROM " + DatabaseHelper.TABLE_RECIPES + " r " +
                "INNER JOIN " + DatabaseHelper.TABLE_FAVORITES + " f " +
                "ON r." + DatabaseHelper.COLUMN_ID + " = f." + DatabaseHelper.COLUMN_RECIPE_ID + " " +
                "WHERE f." + DatabaseHelper.COLUMN_USER_ID + " = " + userId;

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Recipe recipe = new Recipe(
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RECIPE_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RECIPE_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RECIPE_IMAGE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RECIPE_COOKING_TIME))
                );

                // Set as favorite
                recipe.setFavorite(true);

                favoritesList.add(recipe);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return favoritesList;
    }
}