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

/**
 * RecipeAdapter - Adapter hiển thị danh sách công thức nấu ăn
 *
 * Adapter này hiển thị danh sách các công thức nấu ăn dưới dạng các card trong RecyclerView,
 * bao gồm hình ảnh, tên công thức, thời gian nấu và biểu tượng yêu thích.
 * Cho phép người dùng tương tác để xem chi tiết công thức hoặc thêm/xóa khỏi danh sách yêu thích.
 */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    private Context context;          // Context của ứng dụng
    private List<Recipe> recipeList;  // Danh sách công thức nấu ăn
    private int userId;               // ID của người dùng hiện tại
    private FavoriteDAO favoriteDAO;  // DAO xử lý danh sách yêu thích

    public RecipeAdapter(Context context, List<Recipe> recipeList, int userId) {
        this.context = context;
        this.recipeList = recipeList;
        this.userId = userId;
        this.favoriteDAO = new FavoriteDAO(context);  // Khởi tạo DAO quản lý yêu thích
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Tạo view từ layout item_recipe
        View view = LayoutInflater.from(context).inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        // Lấy công thức tại vị trí hiện tại
        Recipe recipe = recipeList.get(position);

        // Thiết lập dữ liệu cơ bản
        holder.tvRecipeName.setText(recipe.getRecipeName());  // Tên công thức
        holder.tvCookingTime.setText(recipe.getCookingTime()); // Thời gian nấu

        // Tải và hiển thị hình ảnh công thức
        if (recipe.getImage() != null && !recipe.getImage().isEmpty()) {
            // Nếu có hình ảnh, tải từ bộ nhớ
            ImageUtils.loadImageFromStorage(context, holder.ivRecipeImage, recipe.getImage());
        } else {
            // Nếu không có hình ảnh, hiển thị hình mặc định
            holder.ivRecipeImage.setImageResource(R.drawable.bg_image_placeholder);
        }

        // Cập nhật biểu tượng yêu thích dựa trên trạng thái hiện tại
        updateFavoriteIcon(holder.ivFavorite, recipe.isFavorite());

        // Thiết lập sự kiện click cho item công thức
        holder.itemView.setOnClickListener(v -> {
            // Khi người dùng nhấn vào công thức, mở màn hình chi tiết
            Intent intent = new Intent(context, RecipeDetailActivity.class);
            intent.putExtra("recipe_id", recipe.getId());  // Truyền ID công thức
            context.startActivity(intent);
        });

        // Thiết lập sự kiện click cho biểu tượng yêu thích
        holder.ivFavorite.setOnClickListener(v -> {
            if (recipe.isFavorite()) {
                // Nếu đang là yêu thích, xóa khỏi danh sách yêu thích
                favoriteDAO.removeFromFavorites(userId, recipe.getId());
                recipe.setFavorite(false);
            } else {
                // Nếu chưa là yêu thích, thêm vào danh sách yêu thích
                favoriteDAO.addToFavorites(userId, recipe.getId());
                recipe.setFavorite(true);
            }

            // Cập nhật biểu tượng yêu thích theo trạng thái mới
            updateFavoriteIcon(holder.ivFavorite, recipe.isFavorite());
        });
    }

    private void updateFavoriteIcon(ImageView imageView, boolean isFavorite) {
        if (isFavorite) {
            // Nếu đã yêu thích, hiển thị biểu tượng tim đầy
            imageView.setImageResource(R.drawable.ic_favorite);
        } else {
            // Nếu chưa yêu thích, hiển thị biểu tượng tim rỗng
            imageView.setImageResource(R.drawable.ic_favorite_border);
        }
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        ImageView ivRecipeImage;  // Hiển thị hình ảnh công thức
        ImageView ivFavorite;     // Biểu tượng yêu thích
        TextView tvRecipeName;    // Tên công thức
        TextView tvCookingTime;   // Thời gian nấu

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ các view từ layout
            ivRecipeImage = itemView.findViewById(R.id.iv_recipe_image);
            ivFavorite = itemView.findViewById(R.id.iv_favorite);
            tvRecipeName = itemView.findViewById(R.id.tv_recipe_name);
            tvCookingTime = itemView.findViewById(R.id.tv_cooking_time);
        }
    }
}