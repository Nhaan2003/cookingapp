package com.example.cookingtutorialapp.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cookingtutorialapp.R;
import com.example.cookingtutorialapp.adapters.CookingStepAdapter;
import com.example.cookingtutorialapp.adapters.IngredientAdapter;
import com.example.cookingtutorialapp.dao.CategoryDAO;
import com.example.cookingtutorialapp.dao.FavoriteDAO;
import com.example.cookingtutorialapp.dao.IngredientDAO;
import com.example.cookingtutorialapp.dao.RecipeDAO;
import com.example.cookingtutorialapp.models.Category;
import com.example.cookingtutorialapp.models.Ingredient;
import com.example.cookingtutorialapp.models.Recipe;
import com.example.cookingtutorialapp.utils.ImageUtils;
import com.example.cookingtutorialapp.utils.SessionManager;

public class RecipeDetailActivity extends AppCompatActivity {
    private ImageView imgRecipe;            // Đổi tên biến từ ivRecipeImage
    private ImageButton btnFavorite;        // Đổi kiểu từ ImageView sang ImageButton
    private Toolbar toolbar;                // Thêm toolbar thay vì ivBack
    private TextView tvRecipeName, tvCategory, tvCookingTime, tvDescription;
    private Button btnStartCooking, btnAddToShopping;  // Đổi tên từ btnAddToShoppingList
    private RecyclerView rvIngredients, rvSteps;

    private RecipeDAO recipeDAO;
    private CategoryDAO categoryDAO;
    private FavoriteDAO favoriteDAO;
    private IngredientDAO ingredientDAO;
    private SessionManager sessionManager;

    private Recipe recipe;
    private int recipeId;
    private boolean isCookingMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        sessionManager = new SessionManager(this);
        recipeDAO = new RecipeDAO(this);
        categoryDAO = new CategoryDAO(this);
        favoriteDAO = new FavoriteDAO(this);
        ingredientDAO = new IngredientDAO(this);

        // Initialize UI components với ID đúng từ layout
        imgRecipe = findViewById(R.id.img_recipe);
        btnFavorite = findViewById(R.id.btn_favorite);
        toolbar = findViewById(R.id.toolbar);
        tvRecipeName = findViewById(R.id.tv_recipe_name);
        tvCategory = findViewById(R.id.tv_category);
        tvCookingTime = findViewById(R.id.tv_cooking_time);
        tvDescription = findViewById(R.id.tv_description);
        btnStartCooking = findViewById(R.id.btn_start_cooking);
        btnAddToShopping = findViewById(R.id.btn_add_to_shopping);
        rvIngredients = findViewById(R.id.rv_ingredients);
        rvSteps = findViewById(R.id.rv_steps);

        // Setup Toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Get recipe ID from intent
        recipeId = getIntent().getIntExtra("recipe_id", -1);

        if (recipeId == -1) {
            Toast.makeText(this, "Không tìm thấy công thức", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load recipe details
        loadRecipeDetails();

        // Setup favorite button
        btnFavorite.setOnClickListener(v -> {
            if (recipe.isFavorite()) {
                // Remove from favorites
                favoriteDAO.removeFromFavorites(sessionManager.getUserId(), recipe.getId());
                recipe.setFavorite(false);
            } else {
                // Add to favorites
                favoriteDAO.addToFavorites(sessionManager.getUserId(), recipe.getId());
                recipe.setFavorite(true);
            }

            // Update favorite icon
            updateFavoriteIcon();
        });

        // Setup start cooking button
        btnStartCooking.setOnClickListener(v -> {
            isCookingMode = !isCookingMode;
            updateCookingMode();
        });

        // Setup add to shopping list button
        btnAddToShopping.setOnClickListener(v -> {
            // Add all ingredients to shopping list
            for (Ingredient ingredient : recipe.getIngredients()) {
                ingredientDAO.addIngredientToShoppingList(sessionManager.getUserId(), ingredient.getId());
            }
            Toast.makeText(this, "Đã thêm vào danh sách mua sắm", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadRecipeDetails() {
        // Get recipe from database
        recipe = recipeDAO.getRecipeById(recipeId, sessionManager.getUserId());

        if (recipe == null) {
            Toast.makeText(this, "Không tìm thấy công thức", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set recipe details
        tvRecipeName.setText(recipe.getRecipeName());
        tvCookingTime.setText(recipe.getCookingTime());
        tvDescription.setText(recipe.getDescription());

        // Set recipe title in action bar
        getSupportActionBar().setTitle(recipe.getRecipeName());

        // Load image
        if (recipe.getImage() != null && !recipe.getImage().isEmpty()) {
            ImageUtils.loadImageFromStorage(this, imgRecipe, recipe.getImage());
        } else {
            // Kiểm tra nếu bg_image_placeholder tồn tại, nếu không thì sử dụng drawable khác
            try {
                imgRecipe.setImageResource(R.drawable.bg_image_placeholder);
            } catch (Exception e) {
                // Fallback sang drawable color mặc định nếu không tìm thấy resource
                imgRecipe.setImageResource(android.R.color.darker_gray);
            }
        }

        // Set category name
        Category category = categoryDAO.getCategoryById(recipe.getCategoryId());
        if (category != null) {
            tvCategory.setText(category.getCategoryName());
        }

        // Setup ingredients recycler view
        rvIngredients.setLayoutManager(new LinearLayoutManager(this));
        IngredientAdapter ingredientAdapter = new IngredientAdapter(this, recipe.getIngredients());
        rvIngredients.setAdapter(ingredientAdapter);

        // Setup steps recycler view (initially not in cooking mode)
        setupStepsRecyclerView(false);

        // Update favorite icon
        updateFavoriteIcon();
    }

    private void updateFavoriteIcon() {
        // Sử dụng drawable có sẵn
        try {
            if (recipe.isFavorite()) {
                // Thử sử dụng ic_favorite_filled
                try {
                    btnFavorite.setImageResource(R.drawable.ic_favorite_filled);
                } catch (Exception e) {
                    // Fallback sang ic_favorite
                    btnFavorite.setImageResource(R.drawable.ic_favorite);
                }
            } else {
                // Thử sử dụng ic_favorite_outline
                try {
                    btnFavorite.setImageResource(R.drawable.ic_favorite_outline);
                } catch (Exception e) {
                    // Fallback sang ic_favorite_border
                    btnFavorite.setImageResource(R.drawable.ic_favorite_border);
                }
            }
        } catch (Exception e) {
            // Trong trường hợp không có cả hai drawable
            Toast.makeText(this, "Lỗi hiển thị biểu tượng yêu thích", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateCookingMode() {
        if (isCookingMode) {
            btnStartCooking.setText(R.string.exit_cooking_mode);
            setupStepsRecyclerView(true);
        } else {
            btnStartCooking.setText(R.string.start_cooking);
            setupStepsRecyclerView(false);
        }
    }

    private void setupStepsRecyclerView(boolean cookingMode) {
        rvSteps.setLayoutManager(new LinearLayoutManager(this));
        if (cookingMode) {
            CookingStepAdapter cookingAdapter = new CookingStepAdapter(this, recipe.getSteps());
            rvSteps.setAdapter(cookingAdapter);
        } else {
            // Simple adapter for viewing only
            CookingStepAdapter viewAdapter = new CookingStepAdapter(this, recipe.getSteps());
            viewAdapter.setCookingMode(false); // Giả sử CookingStepAdapter có phương thức này
            rvSteps.setAdapter(viewAdapter);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}