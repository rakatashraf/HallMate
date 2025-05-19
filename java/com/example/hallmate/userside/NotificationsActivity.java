package com.example.hallmate.userside;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hallmate.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class NotificationsActivity extends AppCompatActivity {

    private TextView tvAnnouncementMessage;

    private DocumentReference announcementRef;

    private String announcementId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_announcement);

        // Get announcementId from Intent extras
        announcementId = getIntent().getStringExtra("announcementId");

        if (announcementId == null) {
            Toast.makeText(this, "Announcement ID not found!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvAnnouncementMessage = findViewById(R.id.tvMessage);
        //tvTimestamp = findViewById(R.id.tvTimestamp);
        announcementRef = FirebaseFirestore.getInstance()
                .collection("Announcement")
                .document(announcementId);

        fetchAnnouncementMessage();
    }

    private void fetchAnnouncementMessage() {
        announcementRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(NotificationsActivity.this, "Failed to load announcement", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    String message = snapshot.getString("message");
                    if (message != null) {
                        tvAnnouncementMessage.setText(message);
                    } else {
                        tvAnnouncementMessage.setText("No message found");
                    }
                } else {
                    tvAnnouncementMessage.setText("Announcement not found");
                }
            }
        });
    }
}