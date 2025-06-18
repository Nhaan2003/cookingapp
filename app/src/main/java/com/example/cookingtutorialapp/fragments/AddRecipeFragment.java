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

/**
 * AddRecipeFragment - Fragment cho phép người dùng thêm công thức mới
 *
 * Fragment này cung cấp giao diện để người dùng nhập các thông tin của một công thức mới,
 * bao gồm tên, mô tả, thời gian nấu, danh mục, hình ảnh, nguyên liệu và các bước thực hiện.
 * Người dùng có thể thêm nguyên liệu và các bước động, sau đó lưu công thức vào cơ sở dữ liệu.
 */
public class AddRecipeFragment extends Fragment {
    // Mã yêu cầu cho việc chọn hình ảnh
    private static final int PICK_IMAGE_REQUEST = 1;

    // Các thành phần UI
    private EditText etRecipeName;     // Trường nhập tên công thức
    private EditText etDescription;    // Trường nhập mô tả công thức
    private EditText etCookingTime;    // Trường nhập thời gian nấu
    private Spinner spinnerCategory;   // Spinner chọn danh mục
    private ImageView ivRecipeImage;   // ImageView hiển thị hình ảnh công thức
    private Button btnAddIngredient;   // Nút thêm nguyên liệu
    private Button btnAddStep;         // Nút thêm bước thực hiện
    private Button btnSubmitRecipe;    // Nút lưu công thức
    private Button btnSelectImage;     // Nút chọn hình ảnh
    private RecyclerView rvIngredients; // RecyclerView hiển thị các nguyên liệu
    private RecyclerView rvSteps;      // RecyclerView hiển thị các bước

    // Các đối tượng DAO và quản lý phiên đăng nhập
    private RecipeDAO recipeDAO;       // Đối tượng truy cập dữ liệu công thức
    private CategoryDAO categoryDAO;    // Đối tượng truy cập dữ liệu danh mục
    private SessionManager sessionManager; // Quản lý phiên đăng nhập

    // Dữ liệu của công thức
    private Uri imageUri;              // URI của hình ảnh đã chọn
    private List<Ingredient> ingredientList; // Danh sách nguyên liệu
    private List<Step> stepList;       // Danh sách các bước
    private IngredientInputAdapter ingredientAdapter; // Adapter cho RecyclerView nguyên liệu
    private StepInputAdapter stepAdapter; // Adapter cho RecyclerView các bước

    /**
     * Phương thức tạo và thiết lập giao diện cho fragment
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout cho fragment
        View view = inflater.inflate(R.layout.fragment_add_recipe, container, false);

        // Khởi tạo các đối tượng DAO và SessionManager
        sessionManager = new SessionManager(getContext());
        recipeDAO = new RecipeDAO(getContext());
        categoryDAO = new CategoryDAO(getContext());

        // Ánh xạ các thành phần UI từ layout
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

        // Thiết lập Spinner danh mục
        List<Category> categories = categoryDAO.getAllCategories(); // Lấy danh sách tất cả danh mục
        ArrayAdapter<Category> categoryAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        // Khởi tạo danh sách nguyên liệu và adapter
        ingredientList = new ArrayList<>();
        ingredientAdapter = new IngredientInputAdapter(getContext(), ingredientList);
        rvIngredients.setLayoutManager(new LinearLayoutManager(getContext()));
        rvIngredients.setAdapter(ingredientAdapter);

        // Khởi tạo danh sách các bước và adapter
        stepList = new ArrayList<>();
        stepAdapter = new StepInputAdapter(getContext(), stepList);
        rvSteps.setLayoutManager(new LinearLayoutManager(getContext()));
        rvSteps.setAdapter(stepAdapter);

        // Thiết lập sự kiện click cho các nút
        btnSelectImage.setOnClickListener(v -> openFileChooser()); // Chọn hình ảnh

        // Nút thêm nguyên liệu
        btnAddIngredient.setOnClickListener(v -> {
            ingredientList.add(new Ingredient()); // Thêm nguyên liệu mới vào danh sách
            ingredientAdapter.notifyItemInserted(ingredientList.size() - 1); // Cập nhật adapter
        });

        // Nút thêm bước thực hiện
        btnAddStep.setOnClickListener(v -> {
            Step step = new Step();
            step.setStepNumber(stepList.size() + 1); // Số thứ tự bước
            stepList.add(step); // Thêm bước mới vào danh sách
            stepAdapter.notifyItemInserted(stepList.size() - 1); // Cập nhật adapter
        });

        // Nút lưu công thức
        btnSubmitRecipe.setOnClickListener(v -> submitRecipe());

        return view;
    }

    /**
     * Mở hộp thoại chọn hình ảnh từ thiết bị
     */
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*"); // Chỉ chọn file hình ảnh
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    /**
     * Xử lý kết quả trả về từ hộp thoại chọn hình ảnh
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData(); // Lưu URI của hình ảnh đã chọn
            ivRecipeImage.setImageURI(imageUri); // Hiển thị hình ảnh đã chọn
        }
    }

    /**
     * Xác thực dữ liệu và lưu công thức vào cơ sở dữ liệu
     */
    private void submitRecipe() {
        // Lấy dữ liệu từ các trường nhập liệu
        String recipeName = etRecipeName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String cookingTime = etCookingTime.getText().toString().trim();

        // Kiểm tra các trường bắt buộc
        if (recipeName.isEmpty() || description.isEmpty() || cookingTime.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra danh sách nguyên liệu và các bước
        if (ingredientList.isEmpty() || stepList.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng thêm nguyên liệu và các bước thực hiện", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy danh mục đã chọn
        Category selectedCategory = (Category) spinnerCategory.getSelectedItem();

        // Tạo đối tượng Recipe mới
        Recipe recipe = new Recipe();
        recipe.setRecipeName(recipeName);
        recipe.setDescription(description);
        recipe.setCookingTime(cookingTime);
        recipe.setCategoryId(selectedCategory.getId());
        recipe.setUserId(sessionManager.getUserId()); // ID người dùng đang đăng nhập

        // Lưu hình ảnh vào bộ nhớ và lấy đường dẫn
        if (imageUri != null) {
            String imagePath = ImageUtils.saveImageToInternalStorage(getContext(), imageUri);
            recipe.setImage(imagePath);
        }

        // Thêm nguyên liệu vào công thức
        for (Ingredient ingredient : ingredientList) {
            recipe.addIngredient(ingredient);
        }

        // Thêm các bước vào công thức
        for (Step step : stepList) {
            recipe.addStep(step);
        }

        // Lưu công thức vào cơ sở dữ liệu
        long result = recipeDAO.insertRecipe(recipe);

        // Xử lý kết quả
        if (result > 0) {
            Toast.makeText(getContext(), "Thêm công thức thành công", Toast.LENGTH_SHORT).show();
            clearForm(); // Xóa form sau khi thêm thành công
        } else {
            Toast.makeText(getContext(), "Thêm công thức thất bại", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Xóa tất cả dữ liệu trong form sau khi thêm công thức thành công
     */
    private void clearForm() {
        etRecipeName.setText("");
        etDescription.setText("");
        etCookingTime.setText("");
        ivRecipeImage.setImageResource(R.drawable.placeholder_image);
        imageUri = null;

        // Xóa danh sách nguyên liệu
        ingredientList.clear();
        ingredientAdapter.notifyDataSetChanged();

        // Xóa danh sách các bước
        stepList.clear();
        stepAdapter.notifyDataSetChanged();
    }
}