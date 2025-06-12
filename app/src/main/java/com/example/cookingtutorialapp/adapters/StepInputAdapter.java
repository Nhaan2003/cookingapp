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

public class StepInputAdapter extends RecyclerView.Adapter<StepInputAdapter.StepViewHolder> {
    private Context context;
    private List<Step> steps;

    public StepInputAdapter(Context context, List<Step> steps) {
        this.context = context;
        this.steps = steps;
    }

    @NonNull
    @Override
    public StepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_step_input, parent, false);
        return new StepViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StepViewHolder holder, int position) {
        Step step = steps.get(position);

        // Set step number
        holder.tvStepNumber.setText("Step " + (position + 1));
        step.setStepNumber(position + 1);

        // Set description if already exists
        if (step.getDescription() != null) {
            holder.etDescription.setText(step.getDescription());
        }

        // Setup text change listener
        holder.etDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                step.setDescription(s.toString());
            }
        });

        // Setup remove button
        holder.btnRemove.setOnClickListener(v -> {
            steps.remove(position);
            notifyItemRemoved(position);

            // Update step numbers for remaining steps
            for (int i = position; i < steps.size(); i++) {
                steps.get(i).setStepNumber(i + 1);
            }

            notifyItemRangeChanged(position, steps.size());
        });
    }

    @Override
    public int getItemCount() {
        return steps.size();
    }

    public static class StepViewHolder extends RecyclerView.ViewHolder {
        TextView tvStepNumber;
        EditText etDescription;
        ImageButton btnRemove;

        public StepViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStepNumber = itemView.findViewById(R.id.tv_step_number);
            etDescription = itemView.findViewById(R.id.et_step_description);
            btnRemove = itemView.findViewById(R.id.btn_remove_step);
        }
    }
}