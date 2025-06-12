package com.example.cookingtutorialapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cookingtutorialapp.R;
import com.example.cookingtutorialapp.adapters.ShoppingListAdapter;
import com.example.cookingtutorialapp.dao.IngredientDAO;
import com.example.cookingtutorialapp.models.Ingredient;
import com.example.cookingtutorialapp.utils.SessionManager;

import java.util.List;

public class ShoppingListFragment extends Fragment {
    private RecyclerView recyclerView;
    private TextView tvEmpty;
    private IngredientDAO ingredientDAO;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shopping_list, container, false);

        sessionManager = new SessionManager(getContext());
        ingredientDAO = new IngredientDAO(getContext());

        recyclerView = view.findViewById(R.id.rv_shopping_list);
        tvEmpty = view.findViewById(R.id.tv_empty);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Load shopping list
        loadShoppingList();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Reload shopping list when fragment is resumed
        loadShoppingList();
    }

    private void loadShoppingList() {
        List<Ingredient> shoppingList = ingredientDAO.getShoppingList(sessionManager.getUserId());

        if (shoppingList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);

            ShoppingListAdapter adapter = new ShoppingListAdapter(getContext(), shoppingList);
            recyclerView.setAdapter(adapter);
        }
    }
}