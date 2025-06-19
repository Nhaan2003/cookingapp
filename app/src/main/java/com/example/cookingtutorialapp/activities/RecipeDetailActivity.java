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

/**
 * RecipeDetailActivity - Màn hình hiển thị chi tiết công thức nấu ăn
 *
 * Chức năng chính:
 * - Hiển thị thông tin chi tiết của công thức nấu ăn: ảnh, tên, mô tả, thời gian nấu, danh mục
 * - Hiển thị danh sách nguyên liệu và các bước thực hiện
 * - Cho phép thêm/xóa công thức khỏi danh sách yêu thích
 * - Cho phép chuyển đổi giữa chế độ xem và chế độ nấu ăn
 * - Cho phép thêm nguyên liệu vào danh sách mua sắm
 */
public class RecipeDetailActivity extends AppCompatActivity {
    // Các thành phần giao diện
    private ImageView imgRecipe;            // Hiển thị ảnh công thức
    private ImageButton btnFavorite;        // Nút yêu thích
    private Toolbar toolbar;                // Thanh công cụ
    private TextView tvRecipeName, tvCategory, tvCookingTime, tvDescription;  // Các TextView hiển thị thông tin
    private Button btnStartCooking, btnAddToShopping;  // Các nút chức năng
    private RecyclerView rvIngredients, rvSteps;  // RecyclerView hiển thị nguyên liệu và các bước

    // Các đối tượng DAO để truy vấn dữ liệu
    private RecipeDAO recipeDAO;           // Truy vấn dữ liệu công thức
    private CategoryDAO categoryDAO;        // Truy vấn dữ liệu danh mục
    private FavoriteDAO favoriteDAO;        // Xử lý yêu thích
    private IngredientDAO ingredientDAO;    // Truy vấn dữ liệu nguyên liệu
    private SessionManager sessionManager;  // Quản lý phiên đăng nhập

    // Dữ liệu
    private Recipe recipe;                  // Đối tượng công thức hiện tại
    private int recipeId;                   // ID của công thức
    private boolean isCookingMode = false;  // Trạng thái chế độ nấu ăn

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        // Khởi tạo các đối tượng quản lý dữ liệu
        sessionManager = new SessionManager(this);
        recipeDAO = new RecipeDAO(this);
        categoryDAO = new CategoryDAO(this);
        favoriteDAO = new FavoriteDAO(this);
        ingredientDAO = new IngredientDAO(this);

        // Ánh xạ các thành phần giao diện từ layout
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

        // Thiết lập thanh công cụ
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // Hiển thị nút quay lại
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Lấy ID công thức từ Intent
        recipeId = getIntent().getIntExtra("recipe_id", -1);

        // Kiểm tra nếu không tìm thấy ID công thức
        if (recipeId == -1) {
            Toast.makeText(this, "Không tìm thấy công thức", Toast.LENGTH_SHORT).show();
            finish();  // Đóng màn hình hiện tại
            return;
        }

        // Tải thông tin chi tiết công thức
        loadRecipeDetails();

        // Thiết lập sự kiện click cho nút yêu thích
        btnFavorite.setOnClickListener(v -> {
            if (recipe.isFavorite()) {
                // Xóa khỏi danh sách yêu thích
                favoriteDAO.removeFromFavorites(sessionManager.getUserId(), recipe.getId());
                recipe.setFavorite(false);
                Toast.makeText(this, "Đã xóa khỏi danh sách yêu thích", Toast.LENGTH_SHORT).show();
            } else {
                // Thêm vào danh sách yêu thích
                favoriteDAO.addToFavorites(sessionManager.getUserId(), recipe.getId());
                recipe.setFavorite(true);
                Toast.makeText(this, "Đã thêm vào danh sách yêu thích", Toast.LENGTH_SHORT).show();
            }

            // Cập nhật biểu tượng yêu thích
            updateFavoriteIcon();
        });

        // Thiết lập sự kiện click cho nút bắt đầu nấu ăn
        btnStartCooking.setOnClickListener(v -> {
            isCookingMode = !isCookingMode;  // Đảo trạng thái chế độ nấu ăn
            updateCookingMode();  // Cập nhật giao diện theo chế độ nấu ăn
        });

        // Thiết lập sự kiện click cho nút thêm vào danh sách mua sắm
        btnAddToShopping.setOnClickListener(v -> {
            // Thêm tất cả nguyên liệu vào danh sách mua sắm
            for (Ingredient ingredient : recipe.getIngredients()) {
                ingredientDAO.addIngredientToShoppingList(sessionManager.getUserId(), ingredient.getId());
            }
            Toast.makeText(this, "Đã thêm vào danh sách mua sắm", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Tải thông tin chi tiết công thức từ cơ sở dữ liệu và cập nhật giao diện
     */
    private void loadRecipeDetails() {
        // Lấy thông tin công thức từ cơ sở dữ liệu
        recipe = recipeDAO.getRecipeById(recipeId, sessionManager.getUserId());

        // Kiểm tra nếu không tìm thấy công thức
        if (recipe == null) {
            Toast.makeText(this, "Không tìm thấy công thức", Toast.LENGTH_SHORT).show();
            finish();  // Đóng màn hình hiện tại
            return;
        }

        // Thiết lập thông tin chi tiết công thức lên giao diện
        tvRecipeName.setText(recipe.getRecipeName());
        tvCookingTime.setText(recipe.getCookingTime());
        tvDescription.setText(recipe.getDescription());

        // Đặt tên công thức làm tiêu đề cho thanh công cụ
        getSupportActionBar().setTitle(recipe.getRecipeName());

        // Tải hình ảnh công thức
        if (recipe.getImage() != null && !recipe.getImage().isEmpty()) {
            ImageUtils.loadImageFromStorage(this, imgRecipe, recipe.getImage());
        } else {
            // Kiểm tra và sử dụng hình ảnh mặc định
            try {
                imgRecipe.setImageResource(R.drawable.bg_image_placeholder);
            } catch (Exception e) {
                // Sử dụng màu mặc định nếu không tìm thấy hình ảnh
                imgRecipe.setImageResource(android.R.color.darker_gray);
            }
        }

        // Lấy và hiển thị tên danh mục
        Category category = categoryDAO.getCategoryById(recipe.getCategoryId());
        if (category != null) {
            tvCategory.setText(category.getCategoryName());
        }

        // Thiết lập RecyclerView cho danh sách nguyên liệu
        rvIngredients.setLayoutManager(new LinearLayoutManager(this));
        IngredientAdapter ingredientAdapter = new IngredientAdapter(this, recipe.getIngredients());
        rvIngredients.setAdapter(ingredientAdapter);

        // Thiết lập RecyclerView cho các bước nấu ăn (ban đầu không ở chế độ nấu)
        setupStepsRecyclerView(false);

        // Cập nhật biểu tượng yêu thích theo trạng thái hiện tại
        updateFavoriteIcon();
    }

    /**
     * Cập nhật biểu tượng yêu thích dựa trên trạng thái hiện tại của công thức
     */
    private void updateFavoriteIcon() {
        // Sử dụng các drawable phù hợp để hiển thị trạng thái yêu thích
        try {
            if (recipe.isFavorite()) {
                // Nếu đã yêu thích, hiển thị biểu tượng tim đầyhấy
                    btnFavorite.setImageResource(R.drawable.ic_favorite);
            } else {
                // Nếu chưa yêu thích, hiển thị biểu tượng tim rỗng
                    btnFavorite.setImageResource(R.drawable.ic_favorite_border);
            }
        } catch (Exception e) {
            // Thông báo lỗi nếu không thể hiển thị biểu tượng
            Toast.makeText(this, "Lỗi hiển thị biểu tượng yêu thích", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Cập nhật giao diện theo chế độ nấu ăn
     */
    private void updateCookingMode() {
        if (isCookingMode) {
            // Đang ở chế độ nấu ăn
            btnStartCooking.setText(R.string.exit_cooking_mode);  // "Thoát chế độ nấu ăn"
            setupStepsRecyclerView(true);  // Hiển thị các bước ở chế độ nấu ăn
        } else {
            // Đang ở chế độ xem thông thường
            btnStartCooking.setText(R.string.start_cooking);  // "Bắt đầu nấu"
            setupStepsRecyclerView(false);  // Hiển thị các bước ở chế độ xem thường
        }
    }

    /**
     * Thiết lập RecyclerView hiển thị các bước nấu ăn theo chế độ
     */
    private void setupStepsRecyclerView(boolean cookingMode) {
        rvSteps.setLayoutManager(new LinearLayoutManager(this));
        if (cookingMode) {
            // Chế độ nấu ăn: hiển thị từng bước chi tiết với tính năng đánh dấu hoàn thành
            CookingStepAdapter cookingAdapter = new CookingStepAdapter(this, recipe.getSteps());
            rvSteps.setAdapter(cookingAdapter);
        } else {
            // Chế độ xem thường: hiển thị tất cả các bước dưới dạng danh sách
            CookingStepAdapter viewAdapter = new CookingStepAdapter(this, recipe.getSteps());
            viewAdapter.setCookingMode(false);  // Đặt chế độ hiển thị thường
            rvSteps.setAdapter(viewAdapter);
        }
    }

    /**
     * Xử lý sự kiện khi người dùng chọn các mục trong menu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();  // Quay lại màn hình trước đó khi nhấn nút quay lại
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}