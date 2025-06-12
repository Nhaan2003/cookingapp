package com.example.cookingtutorialapp.models;

public class Step {
    private int id;
    private int recipeId;
    private int stepNumber;
    private String description;
    private boolean completed;

    public Step() {
        completed = false;
    }

    public Step(int recipeId, int stepNumber, String description) {
        this.recipeId = recipeId;
        this.stepNumber = stepNumber;
        this.description = description;
        this.completed = false;
    }

    public Step(int id, int recipeId, int stepNumber, String description) {
        this.id = id;
        this.recipeId = recipeId;
        this.stepNumber = stepNumber;
        this.description = description;
        this.completed = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }

    public int getStepNumber() {
        return stepNumber;
    }

    public void setStepNumber(int stepNumber) {
        this.stepNumber = stepNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}