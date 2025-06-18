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
import com.example.cookingtutorialapp.models.Step;

import java.util.List;

/**
 * CookingStepAdapter - Adapter hiển thị các bước nấu ăn trong RecyclerView
 *
 * Adapter này hiển thị danh sách các bước nấu ăn của một công thức, cho phép
 * người dùng đánh dấu đã hoàn thành từng bước trong quá trình nấu.
 * Có thể chuyển đổi giữa chế độ nấu ăn và chế độ xem thường.
 */
public class CookingStepAdapter extends RecyclerView.Adapter<CookingStepAdapter.StepViewHolder> {
    private Context context;          // Context của ứng dụng
    private List<Step> steps;         // Danh sách các bước nấu ăn
    private boolean cookingMode = true;  // Trạng thái chế độ nấu ăn, mặc định là bật

    public CookingStepAdapter(Context context, List<Step> steps) {
        this.context = context;
        this.steps = steps;
    }

    public void setCookingMode(boolean cookingMode) {
        this.cookingMode = cookingMode;
        notifyDataSetChanged();  // Cập nhật lại toàn bộ RecyclerView
    }

    @NonNull
    @Override
    public StepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Tạo view từ layout item_cooking_step
        View view = LayoutInflater.from(context).inflate(R.layout.item_cooking_step, parent, false);
        return new StepViewHolder(view);
    }

    /**
     * Hiển thị dữ liệu tại một vị trí cụ thể
     */
    @Override
    public void onBindViewHolder(@NonNull StepViewHolder holder, int position) {
        // Lấy bước nấu ăn tại vị trí hiện tại
        Step step = steps.get(position);

        // Thiết lập dữ liệu cho ViewHolder
        holder.tvStepNumber.setText("Bước " + step.getStepNumber());
        holder.tvStepDescription.setText(step.getDescription());
        holder.cbCompleted.setChecked(step.isCompleted());

        // Xử lý sự kiện khi người dùng đánh dấu hoàn thành bước
        holder.cbCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Cập nhật trạng thái hoàn thành của bước
            step.setCompleted(isChecked);

            // Áp dụng hiệu ứng trực quan dựa trên trạng thái hoàn thành
            applyCompletionEffect(holder, isChecked);
        });

        // Áp dụng hiệu ứng ban đầu dựa trên trạng thái hoàn thành
        applyCompletionEffect(holder, step.isCompleted());
    }

    private void applyCompletionEffect(StepViewHolder holder, boolean isCompleted) {
        if (isCompleted) {
            // Nếu bước đã hoàn thành, làm mờ văn bản để thể hiện đã xong
            holder.tvStepNumber.setAlpha(0.5f);
            holder.tvStepDescription.setAlpha(0.5f);
        } else {
            // Nếu bước chưa hoàn thành, hiển thị văn bản với độ đậm bình thường
            holder.tvStepNumber.setAlpha(1.0f);
            holder.tvStepDescription.setAlpha(1.0f);
        }
    }

    @Override
    public int getItemCount() {
        return steps.size();
    }

    public static class StepViewHolder extends RecyclerView.ViewHolder {
        TextView tvStepNumber;       // Hiển thị số thứ tự của bước
        TextView tvStepDescription;  // Hiển thị mô tả của bước
        CheckBox cbCompleted;        // Checkbox đánh dấu hoàn thành

        public StepViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ các view từ layout
            tvStepNumber = itemView.findViewById(R.id.tv_step_number);
            tvStepDescription = itemView.findViewById(R.id.tv_step_description);
            cbCompleted = itemView.findViewById(R.id.checkbox_step);
        }
    }
}