package com.example.hallmate.adminside;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.*;
import com.example.hallmate.R;
import com.example.hallmate.adapter.ComplaintAdapter;
import com.example.hallmate.model.ComplaintModel;
import com.google.firebase.firestore.*;
import com.google.firebase.Timestamp;

import java.util.*;

public class ComplaintListActivity extends AppCompatActivity implements ComplaintAdapter.OnSolveClickListener {

    private RecyclerView recyclerView;
    private ComplaintAdapter adapter;
    private List<ComplaintModel> complaintList;
    private FirebaseFirestore db;
    private String hallName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_complaints);

        hallName = getIntent().getStringExtra("hallName");
        if (hallName == null) {
            Toast.makeText(this, "Hall name not found!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.complaintRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        complaintList = new ArrayList<>();
        adapter = new ComplaintAdapter(complaintList, this);
        recyclerView.setAdapter(adapter);

        fetchComplaints();
    }

    private void fetchComplaints() {
        db.collection("Complaints").document(hallName).collection("submissions")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    complaintList.clear();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        try {
                            String id = doc.getId();
                            String title = doc.getString("title");
                            String description = doc.getString("description");
                            String name = doc.getString("name");
                            String userId = doc.getString("userId");
                            Boolean solved = doc.getBoolean("solved");

                            Timestamp timestamp = doc.getTimestamp("timestamp");
                            if (timestamp == null) {
                                timestamp = Timestamp.now(); // fallback to current time
                            }

                            ComplaintModel complaint = new ComplaintModel(
                                    id, title, description, name, userId, timestamp, solved != null && solved
                            );

                            complaintList.add(complaint);  // FIXED: was mistakenly `model` before
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Error Fetching Complaint Data", Toast.LENGTH_SHORT).show();
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    Toast.makeText(this, "Failed to load complaints.", Toast.LENGTH_SHORT).show();
                });
    }




    @Override
    public void onSolveClicked(ComplaintModel complaint) {
        Map<String, Object> updated = new HashMap<>();
        updated.put("solved", true);

        DocumentReference complaintRef = db.collection("Complaints")
                .document(hallName)
                .collection("submissions")
                .document(complaint.getId());

        complaintRef.update(updated)
                .addOnSuccessListener(aVoid -> {
                    db.collection("Complaints")
                            .document(hallName)
                            .collection("archived")
                            .document(complaint.getId())
                            .set(complaint)
                            .addOnSuccessListener(a -> {
                                complaintRef.delete();
                                fetchComplaints();
                                Toast.makeText(this, "Marked as solved & archived", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to update status", Toast.LENGTH_SHORT).show());
    }
}
