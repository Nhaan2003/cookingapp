package com.example.cookingtutorialapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cookingtutorialapp.R;
import com.example.cookingtutorialapp.dao.IngredientDAO;
import com.example.cookingtutorialapp.models.Ingredient;

import java.util.List;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ShoppingListViewHolder> {
    private Context context;
    private List<Ingredient> shoppingList;
    private IngredientDAO ingredientDAO;

    public ShoppingListAdapter(Context context, List<Ingredient> shoppingList) {
        this.context = context;
        this.shoppingList = shoppingList;
        this.ingredientDAO = new IngredientDAO(context);
    }

    @NonNull
    @Override
    public ShoppingListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_shopping_list, parent, false);
        return new ShoppingListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShoppingListViewHolder holder, int position) {
        Ingredient ingredient = shoppingList.get(position);

        holder.tvIngredient.setText(ingredient.toString());
        holder.cbPurchased.setChecked(ingredient.isPurchased());

        holder.cbPurchased.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Update ingredient purchased status
            ingredient.setPurchased(isChecked);
            ingredientDAO.updateShoppingListItemStatus(ingredient.getId(), isChecked);

            // Apply strike-through effect on text if checked
            if (isChecked) {
                holder.tvIngredient.setAlpha(0.5f);
            } else {
                holder.tvIngredient.setAlpha(1.0f);
            }
        });

        // Apply initial visual effect based on purchased status
        if (ingredient.isPurchased()) {
            holder.tvIngredient.setAlpha(0.5f);
        } else {
            holder.tvIngredient.setAlpha(1.0f);
        }
    }

    @Override
    public int getItemCount() {
        return shoppingList.size();
    }

    public static class ShoppingListViewHolder extends RecyclerView.ViewHolder {
        TextView tvIngredient;
        CheckBox cbPurchased;

        public ShoppingListViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIngredient = itemView.findViewById(R.id.tv_ingredient);
            cbPurchased = itemView.findViewById(R.id.cb_purchased);
        }
    }
}