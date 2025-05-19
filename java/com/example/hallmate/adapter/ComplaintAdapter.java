package com.example.hallmate.adapter;

import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hallmate.R;
import com.example.hallmate.model.ComplaintModel;
import java.util.List;

public class ComplaintAdapter extends RecyclerView.Adapter<ComplaintAdapter.ComplaintViewHolder> {

    private List<ComplaintModel> complaintList;
    private OnSolveClickListener listener;

    public interface OnSolveClickListener {
        void onSolveClicked(ComplaintModel complaint);
    }

    public ComplaintAdapter(List<ComplaintModel> complaintList, OnSolveClickListener listener) {
        this.complaintList = complaintList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ComplaintViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_complaint, parent, false);
        return new ComplaintViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComplaintViewHolder holder, int position) {
        ComplaintModel complaint = complaintList.get(position);
        holder.tvTitle.setText(complaint.getTitle());
        holder.tvDescription.setText(complaint.getDescription());
        holder.tvName.setText("By: " + complaint.getName());
        holder.tvTimestamp.setText(complaint.getTimestamp().toDate().toString());


        holder.btnSolve.setEnabled(!complaint.isSolved());
        holder.btnSolve.setText(complaint.isSolved() ? "Solved" : "Solve");

        holder.btnSolve.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSolveClicked(complaint);
            }
        });
    }

    @Override
    public int getItemCount() {
        return complaintList.size();
    }

    static class ComplaintViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription, tvName, tvTimestamp;
        Button btnSolve;

        ComplaintViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvComplaintTitle);
            tvDescription = itemView.findViewById(R.id.tvComplaintDescription);
            tvName = itemView.findViewById(R.id.tvComplaintName);
            tvTimestamp = itemView.findViewById(R.id.tvComplaintTimestamp);
            btnSolve = itemView.findViewById(R.id.btnSolveComplaint);
        }
    }
}
