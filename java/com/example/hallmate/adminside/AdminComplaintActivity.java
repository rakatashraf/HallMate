package com.example.hallmate.adminside;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hallmate.R;
import com.example.hallmate.model.ComplaintModel;
import com.example.hallmate.adapter.ComplaintAdapter;
import com.google.firebase.firestore.*;
import com.google.firebase.Timestamp;

import java.util.*;

public class AdminComplaintActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<ComplaintModel> complaintList = new ArrayList<>();
    private ComplaintAdapter adapter;
    private String hallName;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_complaints);

        hallName = getIntent().getStringExtra("hallName");
        if (hallName == null || hallName.isEmpty()) {
            Toast.makeText(this, "Hall name not found!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        recyclerView = findViewById(R.id.complaintRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        adapter = new ComplaintAdapter(complaintList, this::onSolveClicked);
        recyclerView.setAdapter(adapter);

        loadComplaints();
    }

    private void loadComplaints() {
        db.collection("Complaints")
                .document(hallName)
                .collection("submissions")
                .get()
                .addOnSuccessListener(querySnapshots -> {
                    complaintList.clear();
                    for (QueryDocumentSnapshot doc : querySnapshots) {
                        try {
                            Timestamp timestamp = doc.getTimestamp("timestamp");
                            if (timestamp == null) timestamp = Timestamp.now();

                            boolean solved = doc.getBoolean("solved") != null && doc.getBoolean("solved");

                            ComplaintModel complaint = new ComplaintModel(
                                    doc.getId(),
                                    doc.getString("title"),
                                    doc.getString("description"),
                                    doc.getString("name"),
                                    doc.getString("userId"),
                                    timestamp,
                                    solved
                            );

                            if (!complaint.isSolved()) {
                                complaintList.add(complaint);
                            }
                        } catch (Exception e) {
                            Toast.makeText(this, "Error fetching complaint data.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to fetch complaints.", Toast.LENGTH_SHORT).show()
                );
    }

    private void onSolveClicked(ComplaintModel complaint) {
        DocumentReference complaintRef = db.collection("Complaints")
                .document(hallName)
                .collection("submissions")
                .document(complaint.getId());

        complaintRef.update("solved", true)
                .addOnSuccessListener(unused -> archiveComplaint(complaint))
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to mark as solved.", Toast.LENGTH_SHORT).show()
                );
    }

    private void archiveComplaint(ComplaintModel complaint) {
        DocumentReference archiveRef = db.collection("Complaints")
                .document(hallName)
                .collection("archived")
                .document(complaint.getId());

        Map<String, Object> archivedData = new HashMap<>();
        archivedData.put("title", complaint.getTitle());
        archivedData.put("description", complaint.getDescription());
        archivedData.put("name", complaint.getName());
        archivedData.put("userId", complaint.getUserId());
        archivedData.put("timestamp", complaint.getTimestamp());
        archivedData.put("solved", true);

        archiveRef.set(archivedData)
                .addOnSuccessListener(unused -> {
                    db.collection("Complaints").document(hallName)
                            .collection("submissions").document(complaint.getId())
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "Complaint solved and archived.", Toast.LENGTH_SHORT).show();
                                loadComplaints();
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, "Failed to delete original complaint.", Toast.LENGTH_SHORT).show()
                            );
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to archive complaint.", Toast.LENGTH_SHORT).show()
                );
    }
}
