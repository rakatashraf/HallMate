package com.example.hallmate.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hallmate.R;
import com.example.hallmate.model.StudentModel;

import java.util.ArrayList;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {

    private ArrayList<StudentModel> studentList;
    private OnStudentClickListener listener;

    public interface OnStudentClickListener {
        void onTerminateClicked(int position);
    }

    public StudentAdapter(ArrayList<StudentModel> studentList, OnStudentClickListener listener) {
        this.studentList = studentList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student, parent, false);
        return new StudentViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        StudentModel student = studentList.get(position);
        holder.tvName.setText(student.getName());
        holder.tvStudentId.setText("ID: " + student.getStudentId());
        holder.tvRoomNumber.setText("Room: " + student.getRoomNumber());
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public static class StudentViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvStudentId, tvRoomNumber;
        Button btnTerminate;

        public StudentViewHolder(@NonNull View itemView, OnStudentClickListener listener) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvStudentId = itemView.findViewById(R.id.tvStudentId);
            tvRoomNumber = itemView.findViewById(R.id.tvRoomNumber);
            btnTerminate = itemView.findViewById(R.id.btnTerminate);

            btnTerminate.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onTerminateClicked(position);
                    }
                }
            });
        }
    }
}
