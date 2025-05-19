package com.example.hallmate.adminside;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hallmate.R;
import com.example.hallmate.model.StudentModel;
import com.example.hallmate.adapter.StudentAdapter;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class StudentListActivity extends AppCompatActivity implements StudentAdapter.OnStudentClickListener {

    private RecyclerView recyclerView;
    private StudentAdapter adapter;
    private ArrayList<StudentModel> studentList;

    private FirebaseFirestore db;
    private String hallName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);

        recyclerView = findViewById(R.id.recyclerViewStudents);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        hallName = getIntent().getStringExtra("hallName");

        studentList = new ArrayList<>();
        adapter = new StudentAdapter(studentList, this);
        recyclerView.setAdapter(adapter);

        fetchStudents();
    }

    private void fetchStudents() {
        CollectionReference membersRef = db.collection("Users").document(hallName).collection("members");
        membersRef.whereEqualTo("role", "user")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    studentList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        StudentModel student = doc.toObject(StudentModel.class);
                        studentList.add(student);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(StudentListActivity.this, "Failed to fetch students.", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onTerminateClicked(int position) {
        StudentModel student = studentList.get(position);

        new AlertDialog.Builder(this)
                .setTitle("Terminate Student")
                .setMessage("Are you sure you want to terminate " + student.getName() + "?")
                .setPositiveButton("Yes", (dialog, which) -> terminateStudent(student.getStudentId()))
                .setNegativeButton("No", null)
                .show();
    }

    private void terminateStudent(String studentId) {
        db.collection("Users").document(hallName).collection("members")
                .document(studentId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(StudentListActivity.this, "Student terminated.", Toast.LENGTH_SHORT).show();
                    fetchStudents(); // Refresh list
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(StudentListActivity.this, "Failed to terminate student.", Toast.LENGTH_SHORT).show();
                });
    }
}
