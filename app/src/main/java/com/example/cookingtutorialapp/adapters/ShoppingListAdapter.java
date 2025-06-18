package com.example.cookingtutorialapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cookingtutorialapp.R;
import com.example.cookingtutorialapp.dao.IngredientDAO;
import com.example.cookingtutorialapp.models.Ingredient;

import java.util.List;

/**
 * ShoppingListAdapter - Adapter hiển thị danh sách mua sắm
 *
 * Adapter này hiển thị danh sách các nguyên liệu cần mua trong RecyclerView,
 * cho phép người dùng đánh dấu các nguyên liệu đã mua hoặc chưa mua.
 * Cập nhật trạng thái mua sắm vào cơ sở dữ liệu và áp dụng hiệu ứng trực quan.
 */
public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ShoppingListViewHolder> {
    private Context context;               // Context của ứng dụng
    private List<Ingredient> shoppingList; // Danh sách nguyên liệu cần mua
    private IngredientDAO ingredientDAO;   // DAO xử lý dữ liệu nguyên liệu

    public ShoppingListAdapter(Context context, List<Ingredient> shoppingList) {
        this.context = context;
        this.shoppingList = shoppingList;
        this.ingredientDAO = new IngredientDAO(context);  // Khởi tạo DAO quản lý nguyên liệu
    }

    @NonNull
    @Override
    public ShoppingListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Tạo view từ layout item_shopping_list
        View view = LayoutInflater.from(context).inflate(R.layout.item_shopping_list, parent, false);
        return new ShoppingListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShoppingListViewHolder holder, int position) {
        // Lấy nguyên liệu tại vị trí hiện tại
        Ingredient ingredient = shoppingList.get(position);

        // Hiển thị thông tin nguyên liệu
        holder.tvIngredient.setText(ingredient.toString());

        // Thiết lập trạng thái đã mua hay chưa
        holder.cbPurchased.setChecked(ingredient.isPurchased());

        // Thiết lập sự kiện khi người dùng thay đổi trạng thái mua hàng
        holder.cbPurchased.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Cập nhật trạng thái đã mua của nguyên liệu
            ingredient.setPurchased(isChecked);

            // Lưu trạng thái vào cơ sở dữ liệu
            ingredientDAO.updateShoppingListItemStatus(ingredient.getId(), isChecked);

            // Áp dụng hiệu ứng mờ nếu đã mua
            if (isChecked) {
                holder.tvIngredient.setAlpha(0.5f);  // Làm mờ văn bản nếu đã mua
            } else {
                holder.tvIngredient.setAlpha(1.0f);  // Hiển thị bình thường nếu chưa mua
            }
        });

        // Áp dụng hiệu ứng ban đầu dựa trên trạng thái mua hàng
        if (ingredient.isPurchased()) {
            holder.tvIngredient.setAlpha(0.5f);  // Làm mờ nếu đã mua
        } else {
            holder.tvIngredient.setAlpha(1.0f);  // Hiển thị bình thường nếu chưa mua
        }
    }

    @Override
    public int getItemCount() {
        return shoppingList.size();
    }

    public static class ShoppingListViewHolder extends RecyclerView.ViewHolder {
        TextView tvIngredient;  // Hiển thị tên và số lượng nguyên liệu
        CheckBox cbPurchased;   // Checkbox đánh dấu đã mua hay chưa

        public ShoppingListViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ các view từ layout
            tvIngredient = itemView.findViewById(R.id.tv_ingredient);
            cbPurchased = itemView.findViewById(R.id.cb_purchased);
        }
    }
}