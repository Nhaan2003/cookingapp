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

public class CookingStepAdapter extends RecyclerView.Adapter<CookingStepAdapter.StepViewHolder> {
    private Context context;
    private List<Step> steps;
    private boolean cookingMode = true;

    public CookingStepAdapter(Context context, List<Step> steps) {
        this.context = context;
        this.steps = steps;
    }

    public void setCookingMode(boolean cookingMode) {
        this.cookingMode = cookingMode;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cooking_step, parent, false);
        return new StepViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StepViewHolder holder, int position) {
        Step step = steps.get(position);

        holder.tvStepNumber.setText("Step " + step.getStepNumber());
        holder.tvStepDescription.setText(step.getDescription());
        holder.cbCompleted.setChecked(step.isCompleted());

        // Handle checkbox state change
        holder.cbCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            step.setCompleted(isChecked);

            // Apply visual effects based on completion status
            applyCompletionEffect(holder, isChecked);
        });

        // Apply initial visual effect based on completion status
        applyCompletionEffect(holder, step.isCompleted());
    }

    private void applyCompletionEffect(StepViewHolder holder, boolean isCompleted) {
        if (isCompleted) {
            holder.tvStepNumber.setAlpha(0.5f);
            holder.tvStepDescription.setAlpha(0.5f);
        } else {
            holder.tvStepNumber.setAlpha(1.0f);
            holder.tvStepDescription.setAlpha(1.0f);
        }
    }

    @Override
    public int getItemCount() {
        return steps.size();
    }

    public static class StepViewHolder extends RecyclerView.ViewHolder {
        TextView tvStepNumber, tvStepDescription;
        CheckBox cbCompleted;

        public StepViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStepNumber = itemView.findViewById(R.id.tv_step_number);
            tvStepDescription = itemView.findViewById(R.id.tv_step_description);
            cbCompleted = itemView.findViewById(R.id.checkbox_step);
        }
    }
}