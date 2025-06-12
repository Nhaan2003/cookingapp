package com.example.cookingtutorialapp.models;

import java.util.ArrayList;
import java.util.List;

public class Recipe {
    private int id;
    private String recipeName;
    private String description;
    private String image;
    private int categoryId;
    private int userId;
    private String cookingTime;
    private List<Step> steps;
    private List<Ingredient> ingredients;
    private boolean isFavorite;

    public Recipe() {
        steps = new ArrayList<>();
        ingredients = new ArrayList<>();
    }

    public Recipe(String recipeName, String description, String image, int categoryId, int userId, String cookingTime) {
        this.recipeName = recipeName;
        this.description = description;
        this.image = image;
        this.categoryId = categoryId;
        this.userId = userId;
        this.cookingTime = cookingTime;
        steps = new ArrayList<>();
        ingredients = new ArrayList<>();
    }

    public Recipe(int id, String recipeName, String description, String image, int categoryId, int userId, String cookingTime) {
        this.id = id;
        this.recipeName = recipeName;
        this.description = description;
        this.image = image;
        this.categoryId = categoryId;
        this.userId = userId;
        this.cookingTime = cookingTime;
        steps = new ArrayList<>();
        ingredients = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getCookingTime() {
        return cookingTime;
    }

    public void setCookingTime(String cookingTime) {
        this.cookingTime = cookingTime;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    public void addStep(Step step) {
        steps.add(step);
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public void addIngredient(Ingredient ingredient) {
        ingredients.add(ingredient);
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}