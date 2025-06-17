package com.example.cookingtutorialapp.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
    private Button btnClearAll;
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
        btnClearAll = view.findViewById(R.id.btn_clear_all);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Set click listener for clear all button
        btnClearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showClearAllConfirmationDialog();
            }
        });

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
        // Lấy danh sách mua sắm đã sắp xếp theo tên nguyên liệu
        List<Ingredient> shoppingList = ingredientDAO.getShoppingList(sessionManager.getUserId());

        if (shoppingList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
            btnClearAll.setVisibility(View.GONE); // Ẩn nút khi danh sách trống
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
            btnClearAll.setVisibility(View.VISIBLE); // Hiện nút khi danh sách có dữ liệu

            ShoppingListAdapter adapter = new ShoppingListAdapter(getContext(), shoppingList);
            recyclerView.setAdapter(adapter);
        }
    }

    /**
     * Shows a confirmation dialog before clearing the shopping list
     */
    private void showClearAllConfirmationDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa tất cả các mục trong danh sách mua sắm?")
                .setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        clearAllShoppingItems();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    /**
     * Clears all items from the shopping list for the current user
     */
    private void clearAllShoppingItems() {
        // Thực hiện xóa tất cả các mục trong danh sách mua sắm
        boolean success = ingredientDAO.clearShoppingList(sessionManager.getUserId());

        if (success) {
            // Cập nhật lại danh sách trên UI
            loadShoppingList();

            // Hiển thị thông báo
            Toast.makeText(getContext(), "Đã xóa tất cả các mục", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Không thể xóa danh sách. Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
        }
    }
}