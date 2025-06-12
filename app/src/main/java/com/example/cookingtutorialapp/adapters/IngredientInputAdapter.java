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

public class IngredientInputAdapter extends RecyclerView.Adapter<IngredientInputAdapter.IngredientViewHolder> {
    private Context context;
    private List<Ingredient> ingredients;

    public IngredientInputAdapter(Context context, List<Ingredient> ingredients) {
        this.context = context;
        this.ingredients = ingredients;
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ingredient_input, parent, false);
        return new IngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        Ingredient ingredient = ingredients.get(position);

        // Set values if already exists
        if (ingredient.getName() != null) {
            holder.etName.setText(ingredient.getName());
        }
        if (ingredient.getQuantity() > 0) {
            holder.etQuantity.setText(String.valueOf(ingredient.getQuantity()));
        }
        if (ingredient.getUnit() != null) {
            holder.etUnit.setText(ingredient.getUnit());
        }

        // Setup text change listeners
        holder.etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                ingredient.setName(s.toString());
            }
        });

        holder.etQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    if (!s.toString().isEmpty()) {
                        ingredient.setQuantity(Double.parseDouble(s.toString()));
                    }
                } catch (NumberFormatException e) {
                    holder.etQuantity.setText("");
                }
            }
        });

        holder.etUnit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                ingredient.setUnit(s.toString());
            }
        });

        // Setup remove button
        holder.btnRemove.setOnClickListener(v -> {
            ingredients.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, ingredients.size());
        });
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    public static class IngredientViewHolder extends RecyclerView.ViewHolder {
        EditText etName, etQuantity, etUnit;
        ImageButton btnRemove;

        public IngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            etName = itemView.findViewById(R.id.et_ingredient_name);
            etQuantity = itemView.findViewById(R.id.et_ingredient_quantity);
            etUnit = itemView.findViewById(R.id.et_ingredient_unit);
            btnRemove = itemView.findViewById(R.id.btn_remove_ingredient);
        }
    }
}