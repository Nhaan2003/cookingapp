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

/**
 * ShoppingListFragment - Fragment hiển thị và quản lý danh sách mua sắm
 *
 * Fragment này cho phép người dùng xem danh sách nguyên liệu cần mua,
 * đánh dấu những nguyên liệu đã mua và xóa tất cả các mục trong danh sách.
 * Nguyên liệu sẽ được sắp xếp theo tên để dễ tìm kiếm khi mua sắm.
 */
public class ShoppingListFragment extends Fragment {
    // Các thành phần UI
    private RecyclerView recyclerView; // RecyclerView hiển thị danh sách mua sắm
    private TextView tvEmpty;         // TextView hiển thị khi không có mục nào
    private Button btnClearAll;       // Nút xóa tất cả mục trong danh sách

    // Đối tượng DAO và quản lý phiên đăng nhập
    private IngredientDAO ingredientDAO; // Đối tượng truy cập dữ liệu nguyên liệu
    private SessionManager sessionManager; // Quản lý phiên đăng nhập

    /**
     * Phương thức tạo và thiết lập giao diện cho fragment
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout cho fragment
        View view = inflater.inflate(R.layout.fragment_shopping_list, container, false);

        // Khởi tạo SessionManager và IngredientDAO
        sessionManager = new SessionManager(getContext());
        ingredientDAO = new IngredientDAO(getContext());

        // Ánh xạ các thành phần UI từ layout
        recyclerView = view.findViewById(R.id.rv_shopping_list);
        tvEmpty = view.findViewById(R.id.tv_empty);
        btnClearAll = view.findViewById(R.id.btn_clear_all);

        // Thiết lập LayoutManager cho RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Thiết lập sự kiện click cho nút xóa tất cả
        btnClearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showClearAllConfirmationDialog(); // Hiển thị hộp thoại xác nhận
            }
        });

        // Tải danh sách mua sắm
        loadShoppingList();

        return view;
    }

    /**
     * Phương thức được gọi khi fragment được hiển thị lại
     */
    @Override
    public void onResume() {
        super.onResume();
        // Tải lại danh sách mua sắm khi fragment được hiển thị lại
        loadShoppingList();
    }

    /**
     * Phương thức tải danh sách mua sắm từ cơ sở dữ liệu
     */
    private void loadShoppingList() {
        // Lấy danh sách mua sắm đã sắp xếp theo tên nguyên liệu
        List<Ingredient> shoppingList = ingredientDAO.getShoppingList(sessionManager.getUserId());

        if (shoppingList.isEmpty()) {
            // Nếu danh sách trống, hiển thị thông báo và ẩn các thành phần khác
            recyclerView.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
            btnClearAll.setVisibility(View.GONE); // Ẩn nút khi danh sách trống
        } else {
            // Nếu có dữ liệu, hiển thị danh sách và nút xóa tất cả
            recyclerView.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
            btnClearAll.setVisibility(View.VISIBLE); // Hiện nút khi danh sách có dữ liệu

            // Thiết lập adapter cho RecyclerView
            ShoppingListAdapter adapter = new ShoppingListAdapter(getContext(), shoppingList);
            recyclerView.setAdapter(adapter);
        }
    }

    /**
     * Hiển thị hộp thoại xác nhận trước khi xóa tất cả mục trong danh sách
     */
    private void showClearAllConfirmationDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa tất cả các mục trong danh sách mua sắm?")
                .setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        clearAllShoppingItems(); // Xóa tất cả mục nếu người dùng xác nhận
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // Đóng hộp thoại nếu người dùng hủy
                    }
                })
                .show();
    }

    /**
     * Xóa tất cả các mục khỏi danh sách mua sắm của người dùng hiện tại
     */
    private void clearAllShoppingItems() {
        // Thực hiện xóa tất cả các mục trong danh sách mua sắm
        boolean success = ingredientDAO.clearShoppingList(sessionManager.getUserId());

        if (success) {
            // Cập nhật lại danh sách trên UI
            loadShoppingList();

            // Hiển thị thông báo thành công
            Toast.makeText(getContext(), "Đã xóa tất cả các mục", Toast.LENGTH_SHORT).show();
        } else {
            // Hiển thị thông báo lỗi
            Toast.makeText(getContext(), "Không thể xóa danh sách. Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
        }
    }
}