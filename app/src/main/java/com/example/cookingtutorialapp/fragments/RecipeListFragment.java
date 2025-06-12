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

public class RecipeListFragment extends Fragment {
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private SearchView searchView;
    private RecipeDAO recipeDAO;
    private CategoryDAO categoryDAO;
    private SessionManager sessionManager;
    private List<Category> categories;
    private CategoryAdapter categoryAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_list, container, false);

        sessionManager = new SessionManager(getContext());
        recipeDAO = new RecipeDAO(getContext());
        categoryDAO = new CategoryDAO(getContext());

        viewPager = view.findViewById(R.id.view_pager);
        tabLayout = view.findViewById(R.id.tab_layout);
        searchView = view.findViewById(R.id.search_view);

        // Get all categories
        categories = categoryDAO.getAllCategories();

        // Setup ViewPager with Category Tabs
        categoryAdapter = new CategoryAdapter(getActivity(), categories);
        viewPager.setAdapter(categoryAdapter);

        // Connect TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(categories.get(position).getCategoryName())
        ).attach();

        // Setup search functionality
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchRecipes(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    // Reset to category view
                    viewPager.setVisibility(View.VISIBLE);
                    tabLayout.setVisibility(View.VISIBLE);
                    view.findViewById(R.id.rv_search_results).setVisibility(View.GONE);
                }
                return true;
            }
        });

        return view;
    }

    private void searchRecipes(String keyword) {
        // Get search results
        List<Recipe> searchResults = recipeDAO.searchRecipes(keyword, sessionManager.getUserId());

        // Hide category view
        viewPager.setVisibility(View.GONE);
        tabLayout.setVisibility(View.GONE);

        // Show search results
        RecyclerView rvSearchResults = getView().findViewById(R.id.rv_search_results);
        rvSearchResults.setVisibility(View.VISIBLE);
        rvSearchResults.setLayoutManager(new LinearLayoutManager(getContext()));

        // Setup adapter for search results
        RecipeAdapter adapter = new RecipeAdapter(getContext(), searchResults, sessionManager.getUserId());
        rvSearchResults.setAdapter(adapter);
    }
}