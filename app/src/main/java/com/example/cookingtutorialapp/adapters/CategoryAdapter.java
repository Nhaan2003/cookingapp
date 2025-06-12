package com.example.cookingtutorialapp.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.cookingtutorialapp.fragments.CategoryRecipesFragment;
import com.example.cookingtutorialapp.models.Category;

import java.util.List;

public class CategoryAdapter extends FragmentStateAdapter {
    private List<Category> categories;

    public CategoryAdapter(@NonNull FragmentActivity fragmentActivity, List<Category> categories) {
        super(fragmentActivity);
        this.categories = categories;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return CategoryRecipesFragment.newInstance(categories.get(position).getId());
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }
}