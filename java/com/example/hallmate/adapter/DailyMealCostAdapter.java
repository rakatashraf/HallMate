package com.example.hallmate.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull; // ✅ Import added
import androidx.recyclerview.widget.RecyclerView;

import com.example.hallmate.R;
import com.example.hallmate.model.DailyMealCostModel;

import java.util.List;

public class DailyMealCostAdapter extends RecyclerView.Adapter<DailyMealCostAdapter.ViewHolder> {

    private final List<DailyMealCostModel> dailyMealCostList;

    public DailyMealCostAdapter(List<DailyMealCostModel> dailyMealCostList) {
        this.dailyMealCostList = dailyMealCostList;
    }

    @NonNull
    @Override
    public DailyMealCostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_daily_meal_cost, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DailyMealCostAdapter.ViewHolder holder, int position) {
        DailyMealCostModel model = dailyMealCostList.get(position);

        holder.dateTextView.setText("📅 Date: " + model.getDate());
        holder.studentCostTextView.setText("👨‍🎓 Student Meal Cost: ৳" + model.getStudentMealCost());
        holder.staffCostTextView.setText("👨‍🏫 Staff Meal Cost: ৳" + model.getStaffMealCost());
        holder.utilityBillTextView.setText("💡 Utility Bill: ৳" + model.getUtilityBill());
    }

    @Override
    public int getItemCount() {
        return dailyMealCostList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView dateTextView;
        public TextView studentCostTextView;
        public TextView staffCostTextView;
        public TextView utilityBillTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            dateTextView = itemView.findViewById(R.id.tvDate);
            studentCostTextView = itemView.findViewById(R.id.tvMealCost);
            staffCostTextView = itemView.findViewById(R.id.tvStaffCost);
            utilityBillTextView = itemView.findViewById(R.id.tvUtilityBill);
        }
    }
}
