package com.example.cookingtutorialapp.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.cookingtutorialapp.database.DatabaseHelper;
import com.example.cookingtutorialapp.models.Step;

import java.util.ArrayList;
import java.util.List;

public class StepDAO {
    private DatabaseHelper dbHelper;

    public StepDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public long insertStep(Step step) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_RECIPE_ID, step.getRecipeId());
        values.put(DatabaseHelper.COLUMN_STEP_NUMBER, step.getStepNumber());
        values.put(DatabaseHelper.COLUMN_STEP_DESCRIPTION, step.getDescription());

        long id = db.insert(DatabaseHelper.TABLE_STEPS, null, values);
        db.close();

        return id;
    }

    public List<Step> getStepsByRecipeId(int recipeId) {
        List<Step> stepList = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_STEPS,
                new String[]{DatabaseHelper.COLUMN_ID, DatabaseHelper.COLUMN_RECIPE_ID,
                        DatabaseHelper.COLUMN_STEP_NUMBER, DatabaseHelper.COLUMN_STEP_DESCRIPTION},
                DatabaseHelper.COLUMN_RECIPE_ID + "=?",
                new String[]{String.valueOf(recipeId)},
                null, null, DatabaseHelper.COLUMN_STEP_NUMBER + " ASC"
        );

        if (cursor.moveToFirst()) {
            do {
                Step step = new Step(
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RECIPE_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STEP_NUMBER)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STEP_DESCRIPTION))
                );
                stepList.add(step);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return stepList;
    }

    public void deleteStepsByRecipeId(int recipeId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_STEPS, DatabaseHelper.COLUMN_RECIPE_ID + "=?",
                new String[]{String.valueOf(recipeId)});
        db.close();
    }
}