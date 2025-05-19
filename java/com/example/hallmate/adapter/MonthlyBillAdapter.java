package com.example.hallmate.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hallmate.R;
import com.example.hallmate.model.MonthlyBill;
import java.util.List;

public class MonthlyBillAdapter extends RecyclerView.Adapter<MonthlyBillAdapter.BillViewHolder> {

    private final List<MonthlyBill> billList;

    public MonthlyBillAdapter(List<MonthlyBill> billList) {
        this.billList = billList;
    }

    @NonNull
    @Override
    public BillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bill, parent, false);
        return new BillViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BillViewHolder holder, int position) {
        MonthlyBill bill = billList.get(position);
        holder.tvStartDate.setText("📅 Start Date: " + bill.startDate);
        holder.tvEndDate.setText("📅 End Date: " + bill.endDate);
        holder.tvPaidDate.setText("💰 Paid Date: " + bill.paidDate);
        holder.tvStatus.setText("📌 Status: " + bill.paymentStatus);
        holder.tvStaffCost.setText("👨‍🏫 Staff Meal Cost: ৳" + bill.staffMealCost);
        holder.tvStudentCost.setText("👨‍🎓 Student Meal Cost: ৳" + bill.studentMealCost);
        holder.tvUtilityBill.setText("💡 Utility Bill: ৳" + bill.utilityBill);
        holder.tvTotal.setText("📊 Total: ৳" + bill.total);
        holder.tvPenaltyAmount.setText("⚠️ Penalty Amount: ৳" + bill.penaltyAmount);
        holder.tvPenaltyDays.setText("📅 Penalty Days: " + bill.penaltyDays);
    }


    @Override
    public int getItemCount() {
        return billList.size();
    }

    public static class BillViewHolder extends RecyclerView.ViewHolder {
        TextView tvStartDate, tvEndDate, tvPaidDate, tvStatus, tvStaffCost, tvStudentCost, tvUtilityBill, tvTotal;
        TextView tvPenaltyAmount, tvPenaltyDays;

        public BillViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStartDate = itemView.findViewById(R.id.tvStartDate);
            tvEndDate = itemView.findViewById(R.id.tvEndDate);
            tvPaidDate = itemView.findViewById(R.id.tvPaidDate);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvStaffCost = itemView.findViewById(R.id.tvStaffCost);
            tvStudentCost = itemView.findViewById(R.id.tvStudentCost);
            tvUtilityBill = itemView.findViewById(R.id.tvUtilityBill);
            tvTotal = itemView.findViewById(R.id.tvTotal);
            tvPenaltyAmount = itemView.findViewById(R.id.tvPenaltyAmount);
            tvPenaltyDays = itemView.findViewById(R.id.tvPenaltyDays);
        }
    }
}
