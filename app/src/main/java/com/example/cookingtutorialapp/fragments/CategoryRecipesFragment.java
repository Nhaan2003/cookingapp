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

public class CategoryRecipesFragment extends Fragment {
    private static final String ARG_CATEGORY_ID = "category_id";

    private int categoryId;
    private RecipeDAO recipeDAO;
    private SessionManager sessionManager;

    public static CategoryRecipesFragment newInstance(int categoryId) {
        CategoryRecipesFragment fragment = new CategoryRecipesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CATEGORY_ID, categoryId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            categoryId = getArguments().getInt(ARG_CATEGORY_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_recipes, container, false);

        sessionManager = new SessionManager(getContext());
        recipeDAO = new RecipeDAO(getContext());

        RecyclerView recyclerView = view.findViewById(R.id.rv_recipes);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Get recipes by category
        List<Recipe> recipes = recipeDAO.getRecipesByCategory(categoryId, sessionManager.getUserId());

        // Setup adapter
        RecipeAdapter adapter = new RecipeAdapter(getContext(), recipes, sessionManager.getUserId());
        recyclerView.setAdapter(adapter);

        return view;
    }
}