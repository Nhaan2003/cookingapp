package com.example.cookingtutorialapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cookingtutorialapp.R;
import com.example.cookingtutorialapp.adapters.RecipeAdapter;
import com.example.cookingtutorialapp.dao.RecipeDAO;
import com.example.cookingtutorialapp.models.Recipe;
import com.example.cookingtutorialapp.utils.SessionManager;

import java.util.List;

/**
 * CategoryRecipesFragment - Fragment hiển thị danh sách công thức theo danh mục
 *
 * Fragment này được sử dụng trong ViewPager của RecipeListFragment để hiển thị
 * danh sách các công thức thuộc một danh mục cụ thể. Mỗi tab trong ViewPager
 * là một instance của fragment này với một ID danh mục khác nhau.
 */
public class CategoryRecipesFragment extends Fragment {
    // Khóa argument để truyền ID danh mục
    private static final String ARG_CATEGORY_ID = "category_id";

    // Dữ liệu và đối tượng DAO
    private int categoryId; // ID của danh mục hiện tại
    private RecipeDAO recipeDAO; // Đối tượng truy cập dữ liệu công thức
    private SessionManager sessionManager; // Quản lý phiên đăng nhập

    /**
     * Phương thức tạo instance mới của fragment với ID danh mục được truyền vào
     */
    public static CategoryRecipesFragment newInstance(int categoryId) {
        CategoryRecipesFragment fragment = new CategoryRecipesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CATEGORY_ID, categoryId); // Thêm ID danh mục vào arguments
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Phương thức được gọi khi fragment được tạo
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Lấy ID danh mục từ arguments
        if (getArguments() != null) {
            categoryId = getArguments().getInt(ARG_CATEGORY_ID);
        }
    }

    /**
     * Phương thức tạo và thiết lập giao diện cho fragment
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout cho fragment
        View view = inflater.inflate(R.layout.fragment_category_recipes, container, false);

        // Khởi tạo SessionManager và RecipeDAO
        sessionManager = new SessionManager(getContext());
        recipeDAO = new RecipeDAO(getContext());

        // Ánh xạ RecyclerView từ layout
        RecyclerView recyclerView = view.findViewById(R.id.rv_recipes);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Lấy danh sách công thức theo danh mục
        List<Recipe> recipes = recipeDAO.getRecipesByCategory(categoryId, sessionManager.getUserId());

        // Thiết lập adapter cho RecyclerView
        RecipeAdapter adapter = new RecipeAdapter(getContext(), recipes, sessionManager.getUserId());
        recyclerView.setAdapter(adapter);

        return view;
    }
}