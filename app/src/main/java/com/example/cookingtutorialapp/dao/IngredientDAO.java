package com.example.cookingtutorialapp.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.cookingtutorialapp.database.DatabaseHelper;
import com.example.cookingtutorialapp.models.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class IngredientDAO {
    private DatabaseHelper dbHelper;

    public IngredientDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public long insertIngredient(Ingredient ingredient) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_RECIPE_ID, ingredient.getRecipeId());
        values.put(DatabaseHelper.COLUMN_INGREDIENT_NAME, ingredient.getName());
        values.put(DatabaseHelper.COLUMN_INGREDIENT_QUANTITY, ingredient.getQuantity());
        values.put(DatabaseHelper.COLUMN_INGREDIENT_UNIT, ingredient.getUnit());

        long id = db.insert(DatabaseHelper.TABLE_INGREDIENTS, null, values);
        db.close();

        return id;
    }

    public List<Ingredient> getIngredientsByRecipeId(int recipeId) {
        List<Ingredient> ingredientList = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_INGREDIENTS,
                new String[]{DatabaseHelper.COLUMN_ID, DatabaseHelper.COLUMN_RECIPE_ID,
                        DatabaseHelper.COLUMN_INGREDIENT_NAME, DatabaseHelper.COLUMN_INGREDIENT_QUANTITY,
                        DatabaseHelper.COLUMN_INGREDIENT_UNIT},
                DatabaseHelper.COLUMN_RECIPE_ID + "=?",
                new String[]{String.valueOf(recipeId)},
                null, null, null
        );

        if (cursor.moveToFirst()) {
            do {
                Ingredient ingredient = new Ingredient(
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RECIPE_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_INGREDIENT_NAME)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_INGREDIENT_QUANTITY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_INGREDIENT_UNIT))
                );
                ingredientList.add(ingredient);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return ingredientList;
    }

    public void deleteIngredientsByRecipeId(int recipeId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_INGREDIENTS, DatabaseHelper.COLUMN_RECIPE_ID + "=?",
                new String[]{String.valueOf(recipeId)});
        db.close();
    }

    public void addIngredientToShoppingList(int userId, int ingredientId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_USER_ID, userId);
        values.put(DatabaseHelper.COLUMN_INGREDIENT_ID, ingredientId);
        values.put(DatabaseHelper.COLUMN_IS_PURCHASED, 0);

        db.insert(DatabaseHelper.TABLE_SHOPPING_LIST, null, values);
        db.close();
    }

    public void updateShoppingListItemStatus(int shoppingListId, boolean purchased) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_IS_PURCHASED, purchased ? 1 : 0);

        db.update(
                DatabaseHelper.TABLE_SHOPPING_LIST,
                values,
                DatabaseHelper.COLUMN_ID + "=?",
                new String[]{String.valueOf(shoppingListId)}
        );

        db.close();
    }

    public List<Ingredient> getShoppingList(int userId) {
        List<Ingredient> shoppingList = new ArrayList<>();

        String selectQuery = "SELECT s." + DatabaseHelper.COLUMN_ID + ", i.* " +
                "FROM " + DatabaseHelper.TABLE_SHOPPING_LIST + " s " +
                "INNER JOIN " + DatabaseHelper.TABLE_INGREDIENTS + " i ON s." +
                DatabaseHelper.COLUMN_INGREDIENT_ID + "=i." + DatabaseHelper.COLUMN_ID + " " +
                "WHERE s." + DatabaseHelper.COLUMN_USER_ID + "=" + userId;

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Ingredient ingredient = new Ingredient(
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RECIPE_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_INGREDIENT_NAME)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_INGREDIENT_QUANTITY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_INGREDIENT_UNIT))
                );
                // Get the shopping list item ID
                ingredient.setId(cursor.getInt(0));

                // Get the purchased status
                ingredient.setPurchased(getPurchasedStatus(cursor.getInt(0)));

                shoppingList.add(ingredient);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return shoppingList;
    }

    private boolean getPurchasedStatus(int shoppingListId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_SHOPPING_LIST,
                new String[]{DatabaseHelper.COLUMN_IS_PURCHASED},
                DatabaseHelper.COLUMN_ID + "=?",
                new String[]{String.valueOf(shoppingListId)},
                null, null, null
        );

        boolean purchased = false;

        if (cursor != null && cursor.moveToFirst()) {
            purchased = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IS_PURCHASED)) == 1;
            cursor.close();
        }

        return purchased;
    }
}