package com.example.cookingtutorialapp.activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.cookingtutorialapp.R;
import com.example.cookingtutorialapp.fragments.AccountFragment;
import com.example.cookingtutorialapp.fragments.AddRecipeFragment;
import com.example.cookingtutorialapp.fragments.RecipeListFragment;
import com.example.cookingtutorialapp.fragments.ShoppingListFragment;
import com.example.cookingtutorialapp.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionManager = new SessionManager(this);

        // Check if user is logged in
        if (!sessionManager.isLoggedIn()) {
            // Redirect to login activity
            finish();
        }

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        // Set the default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(
                    R.id.fragment_container, new RecipeListFragment()).commit();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {
                        case R.id.nav_recipes:
                            selectedFragment = new RecipeListFragment();
                            break;
                        case R.id.nav_shopping_list:
                            selectedFragment = new ShoppingListFragment();
                            break;
                        case R.id.nav_add_recipe:
                            selectedFragment = new AddRecipeFragment();
                            break;
                        case R.id.nav_account:
                            selectedFragment = new AccountFragment();
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(
                            R.id.fragment_container, selectedFragment).commit();

                    return true;
                }
            };
}