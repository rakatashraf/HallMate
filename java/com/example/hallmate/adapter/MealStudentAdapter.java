package com.example.hallmate.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hallmate.R;
import com.example.hallmate.model.MealStudent;

import java.util.ArrayList;
import java.util.List;

public class MealStudentAdapter extends RecyclerView.Adapter<MealStudentAdapter.ViewHolder> {

    private List<MealStudent> studentList;
    private List<MealStudent> fullList;
    private String hallName, date;
    private OnCheckedChangeListener listener;

    public MealStudentAdapter(List<MealStudent> studentList, String hallName, String date) {
        this.studentList = studentList;
        this.fullList = new ArrayList<>(studentList);
        this.hallName = hallName;
        this.date = date;
    }

    public interface OnCheckedChangeListener {
        void onCheckedChanged(MealStudent student, String mealType, boolean isChecked);
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, roomNumber, studentId;
        CheckBox breakfast, lunch, dinner;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvName);
            roomNumber = itemView.findViewById(R.id.tvRoom);
            studentId = itemView.findViewById(R.id.tvStudentId);
            breakfast = itemView.findViewById(R.id.cbBreakfast);
            lunch = itemView.findViewById(R.id.cbLunch);
            dinner = itemView.findViewById(R.id.cbDinner);
        }

        public void bind(MealStudent student) {
            name.setText("Name: " + student.getName());
            roomNumber.setText("Room: " + student.getRoomNumber());
            studentId.setText("ID: " + student.getStudentId());


            breakfast.setChecked(student.isBreakfast());
            lunch.setChecked(student.isLunch());
            dinner.setChecked(student.isDinner());

            breakfast.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (listener != null) {
                    listener.onCheckedChanged(student, "breakfast", isChecked);
                }
            });

            lunch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (listener != null) {
                    listener.onCheckedChanged(student, "lunch", isChecked);
                }
            });

            dinner.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (listener != null) {
                    listener.onCheckedChanged(student, "dinner", isChecked);
                }
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(studentList.get(position));
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public void filter(String query) {
        query = query.toLowerCase();
        studentList.clear();
        for (MealStudent s : fullList) {
            if ((s.getName() != null && s.getName().toLowerCase().contains(query)) ||
                    (s.getRoomNumber() != null && s.getRoomNumber().toLowerCase().contains(query)) ||
                    (s.getStudentId() != null && s.getStudentId().toLowerCase().contains(query))) {
                studentList.add(s);
            }
        }
        notifyDataSetChanged();
    }

}
