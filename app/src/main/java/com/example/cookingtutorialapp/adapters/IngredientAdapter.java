package com.example.cookingtutorialapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cookingtutorialapp.R;
import com.example.cookingtutorialapp.models.Ingredient;

import java.util.List;

/**
 * IngredientAdapter - Adapter hiển thị danh sách nguyên liệu trong RecyclerView
 *
 * Adapter này hiển thị danh sách các nguyên liệu cần thiết cho một công thức nấu ăn.
 * Mỗi item hiển thị tên nguyên liệu, số lượng và đơn vị đo.
 */
public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder> {
    private Context context;              // Context của ứng dụng
    private List<Ingredient> ingredients; // Danh sách các nguyên liệu

    public IngredientAdapter(Context context, List<Ingredient> ingredients) {
        this.context = context;
        this.ingredients = ingredients;
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Tạo view từ layout item_ingredient
        View view = LayoutInflater.from(context).inflate(R.layout.item_ingredient, parent, false);
        return new IngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        // Lấy nguyên liệu tại vị trí hiện tại
        Ingredient ingredient = ingredients.get(position);
        // Hiển thị thông tin nguyên liệu (sử dụng phương thức toString của lớp Ingredient)
        holder.tvIngredient.setText(ingredient.toString());

        // Lưu ý: phương thức toString() của Ingredient có thể trả về chuỗi dạng:
        // "250g bột mì" hoặc "2 quả trứng" tùy vào cách định dạng
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    public static class IngredientViewHolder extends RecyclerView.ViewHolder {
        TextView tvIngredient;  // TextView hiển thị thông tin nguyên liệu

        public IngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ TextView từ layout
            tvIngredient = itemView.findViewById(R.id.tv_ingredient);
        }
    }
}