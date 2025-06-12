package com.example.cookingtutorialapp.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cookingtutorialapp.R;
import com.example.cookingtutorialapp.adapters.IngredientInputAdapter;
import com.example.cookingtutorialapp.adapters.StepInputAdapter;
import com.example.cookingtutorialapp.dao.CategoryDAO;
import com.example.cookingtutorialapp.dao.RecipeDAO;
import com.example.cookingtutorialapp.models.Category;
import com.example.cookingtutorialapp.models.Ingredient;
import com.example.cookingtutorialapp.models.Recipe;
import com.example.cookingtutorialapp.models.Step;
import com.example.cookingtutorialapp.utils.ImageUtils;
import com.example.cookingtutorialapp.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class AddRecipeFragment extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText etRecipeName, etDescription, etCookingTime;
    private Spinner spinnerCategory;
    private ImageView ivRecipeImage;
    private Button btnAddIngredient, btnAddStep, btnSubmitRecipe, btnSelectImage;
    private RecyclerView rvIngredients, rvSteps;

    private RecipeDAO recipeDAO;
    private CategoryDAO categoryDAO;
    private SessionManager sessionManager;

    private Uri imageUri;
    private List<Ingredient> ingredientList;
    private List<Step> stepList;
    private IngredientInputAdapter ingredientAdapter;
    private StepInputAdapter stepAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_recipe, container, false);

        sessionManager = new SessionManager(getContext());
        recipeDAO = new RecipeDAO(getContext());
        categoryDAO = new CategoryDAO(getContext());

        // Initialize UI components
        etRecipeName = view.findViewById(R.id.et_recipe_name);
        etDescription = view.findViewById(R.id.et_description);
        etCookingTime = view.findViewById(R.id.et_cooking_time);
        spinnerCategory = view.findViewById(R.id.spinner_category);
        ivRecipeImage = view.findViewById(R.id.iv_recipe_image);
        btnAddIngredient = view.findViewById(R.id.btn_add_ingredient);
        btnAddStep = view.findViewById(R.id.btn_add_step);
        btnSubmitRecipe = view.findViewById(R.id.btn_submit_recipe);
        btnSelectImage = view.findViewById(R.id.btn_select_image);
        rvIngredients = view.findViewById(R.id.rv_ingredients);
        rvSteps = view.findViewById(R.id.rv_steps);

        // Setup category spinner
        List<Category> categories = categoryDAO.getAllCategories();
        ArrayAdapter<Category> categoryAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        // Initialize ingredient list and adapter
        ingredientList = new ArrayList<>();
        ingredientAdapter = new IngredientInputAdapter(getContext(), ingredientList);
        rvIngredients.setLayoutManager(new LinearLayoutManager(getContext()));
        rvIngredients.setAdapter(ingredientAdapter);

        // Initialize step list and adapter
        stepList = new ArrayList<>();
        stepAdapter = new StepInputAdapter(getContext(), stepList);
        rvSteps.setLayoutManager(new LinearLayoutManager(getContext()));
        rvSteps.setAdapter(stepAdapter);

        // Setup button click listeners
        btnSelectImage.setOnClickListener(v -> openFileChooser());

        btnAddIngredient.setOnClickListener(v -> {
            ingredientList.add(new Ingredient());
            ingredientAdapter.notifyItemInserted(ingredientList.size() - 1);
        });

        btnAddStep.setOnClickListener(v -> {
            Step step = new Step();
            step.setStepNumber(stepList.size() + 1);
            stepList.add(step);
            stepAdapter.notifyItemInserted(stepList.size() - 1);
        });

        btnSubmitRecipe.setOnClickListener(v -> submitRecipe());

        return view;
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            ivRecipeImage.setImageURI(imageUri);
        }
    }

    private void submitRecipe() {
        String recipeName = etRecipeName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String cookingTime = etCookingTime.getText().toString().trim();

        if (recipeName.isEmpty() || description.isEmpty() || cookingTime.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (ingredientList.isEmpty() || stepList.isEmpty()) {
            Toast.makeText(getContext(), "Please add ingredients and steps", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get selected category
        Category selectedCategory = (Category) spinnerCategory.getSelectedItem();

        // Create recipe object
        Recipe recipe = new Recipe();
        recipe.setRecipeName(recipeName);
        recipe.setDescription(description);
        recipe.setCookingTime(cookingTime);
        recipe.setCategoryId(selectedCategory.getId());
        recipe.setUserId(sessionManager.getUserId());

        // Save image to storage and get path
        if (imageUri != null) {
            String imagePath = ImageUtils.saveImageToInternalStorage(getContext(), imageUri);
            recipe.setImage(imagePath);
        }

        // Add ingredients
        for (Ingredient ingredient : ingredientList) {
            recipe.addIngredient(ingredient);
        }

        // Add steps
        for (Step step : stepList) {
            recipe.addStep(step);
        }

        // Save recipe to database
        long result = recipeDAO.insertRecipe(recipe);

        if (result > 0) {
            Toast.makeText(getContext(), "Recipe added successfully", Toast.LENGTH_SHORT).show();
            clearForm();
        } else {
            Toast.makeText(getContext(), "Failed to add recipe", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearForm() {
        etRecipeName.setText("");
        etDescription.setText("");
        etCookingTime.setText("");
        ivRecipeImage.setImageResource(R.drawable.placeholder_image);
        imageUri = null;

        ingredientList.clear();
        ingredientAdapter.notifyDataSetChanged();

        stepList.clear();
        stepAdapter.notifyDataSetChanged();
    }
}