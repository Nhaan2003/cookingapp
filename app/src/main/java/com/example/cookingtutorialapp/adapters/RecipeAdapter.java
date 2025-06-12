package com.example.cookingtutorialapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cookingtutorialapp.R;
import com.example.cookingtutorialapp.activities.RecipeDetailActivity;
import com.example.cookingtutorialapp.dao.FavoriteDAO;
import com.example.cookingtutorialapp.models.Recipe;
import com.example.cookingtutorialapp.utils.ImageUtils;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    private Context context;
    private List<Recipe> recipeList;
    private int userId;
    private FavoriteDAO favoriteDAO;

    public RecipeAdapter(Context context, List<Recipe> recipeList, int userId) {
        this.context = context;
        this.recipeList = recipeList;
        this.userId = userId;
        this.favoriteDAO = new FavoriteDAO(context);
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);

        holder.tvRecipeName.setText(recipe.getRecipeName());
        holder.tvCookingTime.setText(recipe.getCookingTime());

        // Load image
        if (recipe.getImage() != null && !recipe.getImage().isEmpty()) {
            ImageUtils.loadImageFromStorage(context, holder.ivRecipeImage, recipe.getImage());
        } else {
            holder.ivRecipeImage.setImageResource(R.drawable.bg_image_placeholder);
        }

        // Set favorite icon
        updateFavoriteIcon(holder.ivFavorite, recipe.isFavorite());

        // Handle click on recipe item
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, RecipeDetailActivity.class);
            intent.putExtra("recipe_id", recipe.getId());
            context.startActivity(intent);
        });

        // Handle click on favorite icon
        holder.ivFavorite.setOnClickListener(v -> {
            if (recipe.isFavorite()) {
                // Remove from favorites
                favoriteDAO.removeFromFavorites(userId, recipe.getId());
                recipe.setFavorite(false);
            } else {
                // Add to favorites
                favoriteDAO.addToFavorites(userId, recipe.getId());
                recipe.setFavorite(true);
            }

            // Update favorite icon
            updateFavoriteIcon(holder.ivFavorite, recipe.isFavorite());
        });
    }

    private void updateFavoriteIcon(ImageView imageView, boolean isFavorite) {
        if (isFavorite) {
            imageView.setImageResource(R.drawable.ic_favorite_filled);
        } else {
            imageView.setImageResource(R.drawable.ic_favorite_outline);
        }
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        ImageView ivRecipeImage, ivFavorite;
        TextView tvRecipeName, tvCookingTime;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            ivRecipeImage = itemView.findViewById(R.id.iv_recipe_image);
            ivFavorite = itemView.findViewById(R.id.iv_favorite);
            tvRecipeName = itemView.findViewById(R.id.tv_recipe_name);
            tvCookingTime = itemView.findViewById(R.id.tv_cooking_time);
        }
    }
}