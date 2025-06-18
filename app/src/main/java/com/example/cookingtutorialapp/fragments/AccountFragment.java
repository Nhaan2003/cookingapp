package com.example.cookingtutorialapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cookingtutorialapp.R;
import com.example.cookingtutorialapp.activities.LoginActivity;
import com.example.cookingtutorialapp.adapters.RecipeAdapter;
import com.example.cookingtutorialapp.dao.FavoriteDAO;
import com.example.cookingtutorialapp.dao.UserDAO;
import com.example.cookingtutorialapp.models.Recipe;
import com.example.cookingtutorialapp.models.User;
import com.example.cookingtutorialapp.utils.SessionManager;

import java.util.List;

/**
 * AccountFragment - Fragment hiển thị thông tin tài khoản người dùng
 *
 * Fragment này hiển thị thông tin cá nhân của người dùng đang đăng nhập,
 * danh sách công thức yêu thích và nút đăng xuất. Đây là nơi người dùng có thể
 * quản lý tài khoản của họ trong ứng dụng.
 */
public class AccountFragment extends Fragment {
    // Các thành phần UI
    private TextView tvUsername;     // TextView hiển thị tên người dùng
    private TextView tvEmail;        // TextView hiển thị email
    private TextView tvNoFavorites;  // TextView hiển thị khi không có công thức yêu thích
    private Button btnLogout;        // Nút đăng xuất
    private RecyclerView rvFavorites; // RecyclerView hiển thị danh sách công thức yêu thích

    // Các đối tượng DAO và quản lý phiên đăng nhập
    private UserDAO userDAO;           // Đối tượng truy cập dữ liệu người dùng
    private FavoriteDAO favoriteDAO;   // Đối tượng truy cập dữ liệu yêu thích
    private SessionManager sessionManager; // Quản lý phiên đăng nhập

    /**
     * Phương thức tạo và thiết lập giao diện cho fragment
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout cho fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        // Khởi tạo SessionManager và các đối tượng DAO
        sessionManager = new SessionManager(getContext());
        userDAO = new UserDAO(getContext());
        favoriteDAO = new FavoriteDAO(getContext());

        // Ánh xạ các thành phần UI từ layout
        tvUsername = view.findViewById(R.id.tv_username);
        tvEmail = view.findViewById(R.id.tv_email);
        tvNoFavorites = view.findViewById(R.id.tv_no_favorites);
        btnLogout = view.findViewById(R.id.btn_logout);
        rvFavorites = view.findViewById(R.id.rv_favorites);

        // Thiết lập LayoutManager cho RecyclerView
        rvFavorites.setLayoutManager(new LinearLayoutManager(getContext()));

        // Tải thông tin người dùng
        User user = userDAO.getUserById(sessionManager.getUserId());
        if (user != null) {
            // Hiển thị tên và email của người dùng
            tvUsername.setText(user.getUsername());
            tvEmail.setText(user.getEmail());
        }

        // Tải danh sách công thức yêu thích
        loadFavoriteRecipes();

        // Thiết lập sự kiện click cho nút đăng xuất
        btnLogout.setOnClickListener(v -> {
            // Đăng xuất người dùng
            sessionManager.logoutUser();
            // Chuyển đến màn hình đăng nhập
            startActivity(new Intent(getContext(), LoginActivity.class));
            // Kết thúc activity hiện tại
            getActivity().finish();
        });

        return view;
    }

    /**
     * Phương thức được gọi khi fragment được hiển thị lại
     */
    @Override
    public void onResume() {
        super.onResume();
        // Tải lại danh sách công thức yêu thích khi fragment được hiển thị lại
        loadFavoriteRecipes();
    }

    /**
     * Phương thức tải danh sách công thức yêu thích của người dùng
     */
    private void loadFavoriteRecipes() {
        // Lấy danh sách công thức yêu thích từ cơ sở dữ liệu
        List<Recipe> favoriteRecipes = favoriteDAO.getFavoriteRecipes(sessionManager.getUserId());

        // Kiểm tra nếu danh sách trống
        if (favoriteRecipes.isEmpty()) {
            // Ẩn RecyclerView và hiển thị thông báo không có công thức yêu thích
            rvFavorites.setVisibility(View.GONE);
            tvNoFavorites.setVisibility(View.VISIBLE);
        } else {
            // Hiển thị RecyclerView và ẩn thông báo không có công thức
            rvFavorites.setVisibility(View.VISIBLE);
            tvNoFavorites.setVisibility(View.GONE);

            // Tạo adapter và thiết lập cho RecyclerView
            RecipeAdapter adapter = new RecipeAdapter(getContext(), favoriteRecipes, sessionManager.getUserId());
            rvFavorites.setAdapter(adapter);
        }
    }
}