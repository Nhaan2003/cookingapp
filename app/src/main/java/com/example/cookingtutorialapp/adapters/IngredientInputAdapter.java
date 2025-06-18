package com.example.cookingtutorialapp.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cookingtutorialapp.R;
import com.example.cookingtutorialapp.models.Ingredient;

import java.util.List;

/**
 * IngredientInputAdapter - Adapter cho phép người dùng nhập và chỉnh sửa danh sách nguyên liệu
 *
 * Adapter này được sử dụng trong màn hình thêm hoặc chỉnh sửa công thức, cho phép
 * người dùng nhập thông tin chi tiết về từng nguyên liệu bao gồm tên, số lượng và đơn vị.
 * Mỗi nguyên liệu có thể được xóa bằng nút xóa.
 */
public class IngredientInputAdapter extends RecyclerView.Adapter<IngredientInputAdapter.IngredientViewHolder> {
    private Context context;              // Context của ứng dụng
    private List<Ingredient> ingredients; // Danh sách các nguyên liệu đang được chỉnh sửa

    public IngredientInputAdapter(Context context, List<Ingredient> ingredients) {
        this.context = context;
        this.ingredients = ingredients;
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Tạo view từ layout item_ingredient_input
        View view = LayoutInflater.from(context).inflate(R.layout.item_ingredient_input, parent, false);
        return new IngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        // Lấy nguyên liệu tại vị trí hiện tại
        Ingredient ingredient = ingredients.get(position);

        // Thiết lập giá trị ban đầu cho các trường nhập liệu (nếu có)
        if (ingredient.getName() != null) {
            holder.etName.setText(ingredient.getName());
        }
        if (ingredient.getQuantity() > 0) {
            holder.etQuantity.setText(String.valueOf(ingredient.getQuantity()));
        }
        if (ingredient.getUnit() != null) {
            holder.etUnit.setText(ingredient.getUnit());
        }

        // Thiết lập sự kiện lắng nghe thay đổi văn bản cho trường tên nguyên liệu
        holder.etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                // Cập nhật tên nguyên liệu khi người dùng thay đổi văn bản
                ingredient.setName(s.toString());
            }
        });

        // Thiết lập sự kiện lắng nghe thay đổi văn bản cho trường số lượng
        holder.etQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                // Cập nhật số lượng nguyên liệu khi người dùng thay đổi văn bản
                try {
                    if (!s.toString().isEmpty()) {
                        ingredient.setQuantity(Double.parseDouble(s.toString()));
                    }
                } catch (NumberFormatException e) {
                    // Xử lý lỗi nếu người dùng nhập không phải số
                    holder.etQuantity.setText("");
                }
            }
        });

        // Thiết lập sự kiện lắng nghe thay đổi văn bản cho trường đơn vị đo
        holder.etUnit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                // Cập nhật đơn vị đo khi người dùng thay đổi văn bản
                ingredient.setUnit(s.toString());
            }
        });

        // Thiết lập sự kiện click cho nút xóa nguyên liệu
        holder.btnRemove.setOnClickListener(v -> {
            // Xóa nguyên liệu khỏi danh sách
            ingredients.remove(position);
            // Thông báo cho adapter biết item đã bị xóa
            notifyItemRemoved(position);
            // Thông báo cho adapter cập nhật lại các vị trí của các item còn lại
            notifyItemRangeChanged(position, ingredients.size());
        });
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    public static class IngredientViewHolder extends RecyclerView.ViewHolder {
        EditText etName;      // Trường nhập tên nguyên liệu
        EditText etQuantity;  // Trường nhập số lượng
        EditText etUnit;      // Trường nhập đơn vị đo
        ImageButton btnRemove; // Nút xóa nguyên liệu

        public IngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ các view từ layout
            etName = itemView.findViewById(R.id.et_ingredient_name);
            etQuantity = itemView.findViewById(R.id.et_ingredient_quantity);
            etUnit = itemView.findViewById(R.id.et_ingredient_unit);
            btnRemove = itemView.findViewById(R.id.btn_remove_ingredient);
        }
    }
}