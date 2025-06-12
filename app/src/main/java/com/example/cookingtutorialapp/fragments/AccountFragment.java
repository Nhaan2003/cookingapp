package com.example.cookingtutorialapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cookingtutorialapp.R;
import com.example.cookingtutorialapp.activities.LoginActivity;
import com.example.cookingtutorialapp.adapters.RecipeAdapter;
import com.example.cookingtutorialapp.dao.FavoriteDAO;
import com.example.cookingtutorialapp.dao.UserDAO;
import com.example.cookingtutorialapp.models.Recipe;
import com.example.cookingtutorialapp.models.User;
import com.example.cookingtutorialapp.utils.SessionManager;

import java.util.List;

public class AccountFragment extends Fragment {
    private TextView tvUsername, tvEmail, tvNoFavorites;
    private Button btnLogout;
    private RecyclerView rvFavorites;

    private UserDAO userDAO;
    private FavoriteDAO favoriteDAO;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        sessionManager = new SessionManager(getContext());
        userDAO = new UserDAO(getContext());
        favoriteDAO = new FavoriteDAO(getContext());

        tvUsername = view.findViewById(R.id.tv_username);
        tvEmail = view.findViewById(R.id.tv_email);
        tvNoFavorites = view.findViewById(R.id.tv_no_favorites);
        btnLogout = view.findViewById(R.id.btn_logout);
        rvFavorites = view.findViewById(R.id.rv_favorites);

        rvFavorites.setLayoutManager(new LinearLayoutManager(getContext()));

        // Load user info
        User user = userDAO.getUserById(sessionManager.getUserId());
        if (user != null) {
            tvUsername.setText(user.getUsername());
            tvEmail.setText(user.getEmail());
        }

        // Load favorite recipes
        loadFavoriteRecipes();

        // Setup logout button
        btnLogout.setOnClickListener(v -> {
            sessionManager.logoutUser();
            startActivity(new Intent(getContext(), LoginActivity.class));
            getActivity().finish();
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Reload favorite recipes when fragment is resumed
        loadFavoriteRecipes();
    }

    private void loadFavoriteRecipes() {
        List<Recipe> favoriteRecipes = favoriteDAO.getFavoriteRecipes(sessionManager.getUserId());

        if (favoriteRecipes.isEmpty()) {
            rvFavorites.setVisibility(View.GONE);
            tvNoFavorites.setVisibility(View.VISIBLE);
        } else {
            rvFavorites.setVisibility(View.VISIBLE);
            tvNoFavorites.setVisibility(View.GONE);

            RecipeAdapter adapter = new RecipeAdapter(getContext(), favoriteRecipes, sessionManager.getUserId());
            rvFavorites.setAdapter(adapter);
        }
    }
}