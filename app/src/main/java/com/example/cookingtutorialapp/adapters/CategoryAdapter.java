package com.example.cookingtutorialapp.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.cookingtutorialapp.fragments.CategoryRecipesFragment;
import com.example.cookingtutorialapp.models.Category;

import java.util.List;

/**
 * CategoryAdapter - Adapter cho ViewPager2 để hiển thị các tab danh mục
 *
 * Adapter này kết nối danh sách các danh mục với ViewPager2, cho phép người dùng
 * vuốt qua lại giữa các tab danh mục khác nhau trên giao diện.
 * Mỗi trang trong ViewPager2 là một CategoryRecipesFragment hiển thị các công thức
 * thuộc danh mục tương ứng.
 */
public class CategoryAdapter extends FragmentStateAdapter {
    // Danh sách các danh mục được hiển thị
    private List<Category> categories;

    public CategoryAdapter(@NonNull FragmentActivity fragmentActivity, List<Category> categories) {
        super(fragmentActivity);
        this.categories = categories;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Tạo một fragment mới với ID của danh mục ở vị trí hiện tại
        return CategoryRecipesFragment.newInstance(categories.get(position).getId());
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }
}