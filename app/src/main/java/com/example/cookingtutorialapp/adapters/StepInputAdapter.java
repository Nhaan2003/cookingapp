package com.example.cookingtutorialapp.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cookingtutorialapp.R;
import com.example.cookingtutorialapp.models.Step;

import java.util.List;

/**
 * StepInputAdapter - Adapter cho phép người dùng nhập và chỉnh sửa các bước nấu ăn
 *
 * Adapter này được sử dụng trong màn hình thêm hoặc chỉnh sửa công thức, cho phép
 * người dùng nhập mô tả chi tiết cho từng bước nấu ăn. Tự động đánh số bước và
 * cho phép xóa bước không cần thiết.
 */
public class StepInputAdapter extends RecyclerView.Adapter<StepInputAdapter.StepViewHolder> {
    private Context context;  // Context của ứng dụng
    private List<Step> steps; // Danh sách các bước nấu ăn

    public StepInputAdapter(Context context, List<Step> steps) {
        this.context = context;
        this.steps = steps;
    }

    @NonNull
    @Override
    public StepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Tạo view từ layout item_step_input
        View view = LayoutInflater.from(context).inflate(R.layout.item_step_input, parent, false);
        return new StepViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StepViewHolder holder, int position) {
        // Lấy bước nấu ăn tại vị trí hiện tại
        Step step = steps.get(position);

        // Thiết lập số thứ tự bước
        holder.tvStepNumber.setText("Bước " + (position + 1));
        step.setStepNumber(position + 1);  // Cập nhật số thứ tự bước trong đối tượng

        // Thiết lập mô tả nếu đã tồn tại
        if (step.getDescription() != null) {
            holder.etDescription.setText(step.getDescription());
        }

        // Thiết lập sự kiện lắng nghe thay đổi văn bản cho trường mô tả
        holder.etDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                // Cập nhật mô tả bước khi người dùng thay đổi văn bản
                step.setDescription(s.toString());
            }
        });

        // Thiết lập sự kiện click cho nút xóa bước
        holder.btnRemove.setOnClickListener(v -> {
            // Xóa bước khỏi danh sách
            steps.remove(position);
            // Thông báo cho adapter biết item đã bị xóa
            notifyItemRemoved(position);

            // Cập nhật số thứ tự cho các bước còn lại
            for (int i = position; i < steps.size(); i++) {
                steps.get(i).setStepNumber(i + 1);
            }

            // Thông báo cho adapter cập nhật lại các vị trí của các item còn lại
            notifyItemRangeChanged(position, steps.size());
        });
    }

    @Override
    public int getItemCount() {
        return steps.size();
    }

    public static class StepViewHolder extends RecyclerView.ViewHolder {
        TextView tvStepNumber;     // Hiển thị số thứ tự bước
        EditText etDescription;    // Trường nhập mô tả bước
        ImageButton btnRemove;     // Nút xóa bước

        public StepViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ các view từ layout
            tvStepNumber = itemView.findViewById(R.id.tv_step_number);
            etDescription = itemView.findViewById(R.id.et_step_description);
            btnRemove = itemView.findViewById(R.id.btn_remove_step);
        }
    }
}