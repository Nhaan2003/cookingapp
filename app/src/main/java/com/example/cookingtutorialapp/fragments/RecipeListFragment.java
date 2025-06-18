package com.example.cookingtutorialapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.cookingtutorialapp.R;
import com.example.cookingtutorialapp.adapters.CategoryAdapter;
import com.example.cookingtutorialapp.adapters.RecipeAdapter;
import com.example.cookingtutorialapp.dao.CategoryDAO;
import com.example.cookingtutorialapp.dao.RecipeDAO;
import com.example.cookingtutorialapp.models.Category;
import com.example.cookingtutorialapp.models.Recipe;
import com.example.cookingtutorialapp.utils.SessionManager;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

/**
 * RecipeListFragment - Fragment chính hiển thị danh sách công thức và tìm kiếm
 *
 * Fragment này là màn hình chính của ứng dụng, cung cấp khả năng duyệt công thức
 * theo danh mục và tìm kiếm công thức. Sử dụng ViewPager2 với TabLayout để hiển thị
 * các danh mục dưới dạng tab có thể vuốt qua lại.
 */
public class RecipeListFragment extends Fragment {
    // Các thành phần UI
    private ViewPager2 viewPager;    // ViewPager2 hiển thị tab danh mục
    private TabLayout tabLayout;     // TabLayout hiển thị tên các danh mục
    private SearchView searchView;   // SearchView để tìm kiếm công thức

    // Các đối tượng DAO và quản lý phiên đăng nhập
    private RecipeDAO recipeDAO;     // Đối tượng truy cập dữ liệu công thức
    private CategoryDAO categoryDAO;  // Đối tượng truy cập dữ liệu danh mục
    private SessionManager sessionManager; // Quản lý phiên đăng nhập

    // Dữ liệu danh mục
    private List<Category> categories; // Danh sách các danh mục
    private CategoryAdapter categoryAdapter; // Adapter cho ViewPager

    /**
     * Phương thức tạo và thiết lập giao diện cho fragment
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout cho fragment
        View view = inflater.inflate(R.layout.fragment_recipe_list, container, false);

        // Khởi tạo SessionManager và các đối tượng DAO
        sessionManager = new SessionManager(getContext());
        recipeDAO = new RecipeDAO(getContext());
        categoryDAO = new CategoryDAO(getContext());

        // Ánh xạ các thành phần UI từ layout
        viewPager = view.findViewById(R.id.view_pager);
        tabLayout = view.findViewById(R.id.tab_layout);
        searchView = view.findViewById(R.id.search_view);

        // Lấy tất cả danh mục từ cơ sở dữ liệu
        categories = categoryDAO.getAllCategories();

        // Thiết lập ViewPager với các tab danh mục
        categoryAdapter = new CategoryAdapter(getActivity(), categories);
        viewPager.setAdapter(categoryAdapter);

        // Kết nối TabLayout với ViewPager2 để hiển thị tên danh mục trên các tab
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(categories.get(position).getCategoryName())
        ).attach();

        // Thiết lập chức năng tìm kiếm
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            /**
             * Phương thức xử lý khi người dùng gửi truy vấn tìm kiếm
             */
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchRecipes(query); // Tìm kiếm công thức theo từ khóa
                return true;
            }

            /**
             * Phương thức xử lý khi nội dung tìm kiếm thay đổi
             */
            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    // Nếu trống, quay lại chế độ xem danh mục
                    viewPager.setVisibility(View.VISIBLE);
                    tabLayout.setVisibility(View.VISIBLE);
                    view.findViewById(R.id.rv_search_results).setVisibility(View.GONE);
                }
                return true;
            }
        });

        return view;
    }

    /**
     * Phương thức tìm kiếm công thức theo từ khóa
     */
    private void searchRecipes(String keyword) {
        // Lấy kết quả tìm kiếm từ cơ sở dữ liệu
        List<Recipe> searchResults = recipeDAO.searchRecipes(keyword, sessionManager.getUserId());

        // Ẩn chế độ xem danh mục
        viewPager.setVisibility(View.GONE);
        tabLayout.setVisibility(View.GONE);

        // Hiển thị kết quả tìm kiếm
        RecyclerView rvSearchResults = getView().findViewById(R.id.rv_search_results);
        rvSearchResults.setVisibility(View.VISIBLE);
        rvSearchResults.setLayoutManager(new LinearLayoutManager(getContext()));

        // Thiết lập adapter cho kết quả tìm kiếm
        RecipeAdapter adapter = new RecipeAdapter(getContext(), searchResults, sessionManager.getUserId());
        rvSearchResults.setAdapter(adapter);
    }
}