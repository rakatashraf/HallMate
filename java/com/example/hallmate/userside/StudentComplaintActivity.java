package com.example.hallmate.userside;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hallmate.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class StudentComplaintActivity extends AppCompatActivity {

    EditText etTitle, etDescription;
    Button btnSubmit;

    FirebaseFirestore db;
    FirebaseAuth auth;
    String userId, hallName, userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_box);

        etTitle = findViewById(R.id.etComplaintTitle);
        etDescription = findViewById(R.id.etComplaintDescription);
        btnSubmit = findViewById(R.id.btnSubmitComplaint);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        userId = auth.getCurrentUser().getUid();

        // Fetch hallName and userName from UsersDirectory
        db.collection("UsersDirectory").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    hallName = documentSnapshot.getString("hallName");
                    userName = documentSnapshot.getString("name"); // must exist
                });

        btnSubmit.setOnClickListener(view -> {
            String title = etTitle.getText().toString().trim();
            String description = etDescription.getText().toString().trim();

            if (title.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> complaint = new HashMap<>();
            complaint.put("userId", userId);
            complaint.put("name", userName);
            complaint.put("title", title);
            complaint.put("description", description);
            complaint.put("timestamp", FieldValue.serverTimestamp());
            complaint.put("solved", false); // Initial state is not solved

            db.collection("Complaints")
                    .document(hallName)
                    .collection("submissions")
                    .add(complaint)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Complaint submitted", Toast.LENGTH_SHORT).show();
                        etTitle.setText("");
                        etDescription.setText("");
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to submit complaint", Toast.LENGTH_SHORT).show();
                    });
        });
    }
}
