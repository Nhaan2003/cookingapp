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

public class RecipeDAO {
    private DatabaseHelper dbHelper;
    private Context context;

    public RecipeDAO(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);
    }

    public long insertRecipe(Recipe recipe) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_RECIPE_NAME, recipe.getRecipeName());
        values.put(DatabaseHelper.COLUMN_RECIPE_DESCRIPTION, recipe.getDescription());
        values.put(DatabaseHelper.COLUMN_RECIPE_IMAGE, recipe.getImage());
        values.put(DatabaseHelper.COLUMN_CATEGORY_ID, recipe.getCategoryId());
        values.put(DatabaseHelper.COLUMN_USER_ID, recipe.getUserId());
        values.put(DatabaseHelper.COLUMN_RECIPE_COOKING_TIME, recipe.getCookingTime());

        long recipeId = db.insert(DatabaseHelper.TABLE_RECIPES, null, values);

        if (recipeId != -1) {
            // Insert steps
            StepDAO stepDAO = new StepDAO(context);
            for (Step step : recipe.getSteps()) {
                step.setRecipeId((int) recipeId);
                stepDAO.insertStep(step);
            }

            // Insert ingredients
            IngredientDAO ingredientDAO = new IngredientDAO(context);
            for (Ingredient ingredient : recipe.getIngredients()) {
                ingredient.setRecipeId((int) recipeId);
                ingredientDAO.insertIngredient(ingredient);
            }
        }

        db.close();
        return recipeId;
    }

    public Recipe getRecipeById(int id, int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_RECIPES,
                new String[]{
                        DatabaseHelper.COLUMN_ID,
                        DatabaseHelper.COLUMN_RECIPE_NAME,
                        DatabaseHelper.COLUMN_RECIPE_DESCRIPTION,
                        DatabaseHelper.COLUMN_RECIPE_IMAGE,
                        DatabaseHelper.COLUMN_CATEGORY_ID,
                        DatabaseHelper.COLUMN_USER_ID,
                        DatabaseHelper.COLUMN_RECIPE_COOKING_TIME
                },
                DatabaseHelper.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null
        );

        Recipe recipe = null;

        if (cursor != null && cursor.moveToFirst()) {
            recipe = new Recipe(
                    cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RECIPE_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RECIPE_DESCRIPTION)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RECIPE_IMAGE)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_ID)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RECIPE_COOKING_TIME))
            );
            cursor.close();

            // Load steps
            StepDAO stepDAO = new StepDAO(context);
            recipe.setSteps(stepDAO.getStepsByRecipeId(recipe.getId()));

            // Load ingredients
            IngredientDAO ingredientDAO = new IngredientDAO(context);
            recipe.setIngredients(ingredientDAO.getIngredientsByRecipeId(recipe.getId()));

            // Check if recipe is favorite
            FavoriteDAO favoriteDAO = new FavoriteDAO(context);
            recipe.setFavorite(favoriteDAO.checkFavorite(userId, recipe.getId()));
        }

        db.close();
        return recipe;
    }

    public List<Recipe> getAllRecipes(int userId) {
        List<Recipe> recipeList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLE_RECIPES;

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

                // Check if recipe is favorite
                FavoriteDAO favoriteDAO = new FavoriteDAO(context);
                recipe.setFavorite(favoriteDAO.checkFavorite(userId, recipe.getId()));

                recipeList.add(recipe);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return recipeList;
    }

    public List<Recipe> getRecipesByCategory(int categoryId, int userId) {
        List<Recipe> recipeList = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_RECIPES,
                new String[]{
                        DatabaseHelper.COLUMN_ID,
                        DatabaseHelper.COLUMN_RECIPE_NAME,
                        DatabaseHelper.COLUMN_RECIPE_DESCRIPTION,
                        DatabaseHelper.COLUMN_RECIPE_IMAGE,
                        DatabaseHelper.COLUMN_CATEGORY_ID,
                        DatabaseHelper.COLUMN_USER_ID,
                        DatabaseHelper.COLUMN_RECIPE_COOKING_TIME
                },
                DatabaseHelper.COLUMN_CATEGORY_ID + "=?",
                new String[]{String.valueOf(categoryId)},
                null, null, null
        );

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

                // Check if recipe is favorite
                FavoriteDAO favoriteDAO = new FavoriteDAO(context);
                recipe.setFavorite(favoriteDAO.checkFavorite(userId, recipe.getId()));

                recipeList.add(recipe);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return recipeList;
    }

    public List<Recipe> searchRecipes(String keyword, int userId) {
        List<Recipe> recipeList = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLE_RECIPES +
                " WHERE " + DatabaseHelper.COLUMN_RECIPE_NAME + " LIKE '%" + keyword + "%' OR " +
                DatabaseHelper.COLUMN_RECIPE_DESCRIPTION + " LIKE '%" + keyword + "%'";

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

                // Check if recipe is favorite
                FavoriteDAO favoriteDAO = new FavoriteDAO(context);
                recipe.setFavorite(favoriteDAO.checkFavorite(userId, recipe.getId()));

                recipeList.add(recipe);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return recipeList;
    }

    public int updateRecipe(Recipe recipe) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_RECIPE_NAME, recipe.getRecipeName());
        values.put(DatabaseHelper.COLUMN_RECIPE_DESCRIPTION, recipe.getDescription());
        values.put(DatabaseHelper.COLUMN_RECIPE_IMAGE, recipe.getImage());
        values.put(DatabaseHelper.COLUMN_CATEGORY_ID, recipe.getCategoryId());
        values.put(DatabaseHelper.COLUMN_USER_ID, recipe.getUserId());
        values.put(DatabaseHelper.COLUMN_RECIPE_COOKING_TIME, recipe.getCookingTime());

        int rowsAffected = db.update(
                DatabaseHelper.TABLE_RECIPES,
                values,
                DatabaseHelper.COLUMN_ID + "=?",
                new String[]{String.valueOf(recipe.getId())}
        );

        db.close();

        if (rowsAffected > 0) {
            // Update steps
            StepDAO stepDAO = new StepDAO(context);

            // First delete all steps
            stepDAO.deleteStepsByRecipeId(recipe.getId());

            // Then insert new steps
            for (Step step : recipe.getSteps()) {
                step.setRecipeId(recipe.getId());
                stepDAO.insertStep(step);
            }

            // Update ingredients
            IngredientDAO ingredientDAO = new IngredientDAO(context);

            // First delete all ingredients
            ingredientDAO.deleteIngredientsByRecipeId(recipe.getId());

            // Then insert new ingredients
            for (Ingredient ingredient : recipe.getIngredients()) {
                ingredient.setRecipeId(recipe.getId());
                ingredientDAO.insertIngredient(ingredient);
            }
        }

        return rowsAffected;
    }

    public int deleteRecipe(int recipeId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // First delete related records
        db.delete(DatabaseHelper.TABLE_STEPS, DatabaseHelper.COLUMN_RECIPE_ID + "=?", new String[]{String.valueOf(recipeId)});
        db.delete(DatabaseHelper.TABLE_INGREDIENTS, DatabaseHelper.COLUMN_RECIPE_ID + "=?", new String[]{String.valueOf(recipeId)});
        db.delete(DatabaseHelper.TABLE_FAVORITES, DatabaseHelper.COLUMN_RECIPE_ID + "=?", new String[]{String.valueOf(recipeId)});

        // Then delete recipe
        int rowsAffected = db.delete(
                DatabaseHelper.TABLE_RECIPES,
                DatabaseHelper.COLUMN_ID + "=?",
                new String[]{String.valueOf(recipeId)}
        );

        db.close();
        return rowsAffected;
    }
}