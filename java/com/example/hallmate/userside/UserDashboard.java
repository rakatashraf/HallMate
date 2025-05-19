package com.example.hallmate.userside;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.hallmate.LoginActivity;
import com.example.hallmate.R;
import com.google.firebase.auth.FirebaseAuth;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.widget.Toast;
import com.google.firebase.firestore.FirebaseFirestore;



public class UserDashboard extends AppCompatActivity {

    private TextView welcomeText;
    private FirebaseAuth mAuth;

    private TextView btnMealToggle, btnDailyMealCost, btnMonthlyBill, btnComplainBox, btnWeeklyMenu,btnNotifications, btnLogout;

    private String firstName;
    private String hallName;
    private String roomNo; // 👈 Added roomNo

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        // Automatically set default meal status
        DefaultMealStatusSetter setter = new DefaultMealStatusSetter();
        setter.setDefaultMealStatusIfNotExists();

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize UI
        welcomeText = findViewById(R.id.welcomeText);

        // Get values from Intent
        firstName = getIntent().getStringExtra("FIRST_NAME");
        hallName = getIntent().getStringExtra("HALL_NAME");
        roomNo = getIntent().getStringExtra("ROOM_NO"); // 👈 Receive ROOM_NO

        if (firstName != null && !firstName.isEmpty()) {
            String[] nameParts = firstName.split(" ");
            String firstNameOnly = nameParts[0];
            welcomeText.setText("Hi, " + firstNameOnly);
        } else {
            welcomeText.setText("Hi, User");
        }

        // Buttons
        btnMealToggle = findViewById(R.id.btnMealToggle);
        btnDailyMealCost = findViewById(R.id.btnDailyMealCost);
        btnMonthlyBill = findViewById(R.id.btnMonthlyBill);
        btnComplainBox = findViewById(R.id.btnComplainBox);
        btnWeeklyMenu = findViewById(R.id.btnWeeklyMenu);
        btnNotifications = findViewById(R.id.btnNotifications);
        btnLogout = findViewById(R.id.btnLogout);

        // Meal Toggle Button - with full intent including ROOM_NO
        btnMealToggle.setOnClickListener(v -> {
            Intent intent = new Intent(UserDashboard.this, MealToggleActivity.class);
            intent.putExtra("HALL_NAME", hallName);
            intent.putExtra("FIRST_NAME", firstName);
            intent.putExtra("ROOM_NO", roomNo); // ✅ Added
            startActivity(intent);
        });

       btnDailyMealCost.setOnClickListener(v -> {
            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            Intent intent = new Intent(UserDashboard.this, DailyMealCostActivity.class);
            intent.putExtra("HALL_NAME", hallName);
            intent.putExtra("DATE", currentDate);
            startActivity(intent);
        });


        btnMonthlyBill.setOnClickListener(v -> startActivity(new Intent(this, MonthlyBillActivity.class)));
        btnComplainBox.setOnClickListener(v -> startActivity(new Intent(this, StudentComplaintActivity.class)));
        btnWeeklyMenu.setOnClickListener(v -> startActivity(new Intent(this, WeeklyMenuActivity.class)));


        btnNotifications.setOnClickListener(v -> {
            String userId = FirebaseAuth.getInstance().getCurrentUser() != null
                    ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                    : null;

            if (userId == null) {
                Toast.makeText(UserDashboard.this, "User not logged in", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            // Example: get the latest announcement doc id (you need to customize this query)
            db.collection("Announcement")
                    .limit(1)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            String announcementId = queryDocumentSnapshots.getDocuments().get(0).getId();

                            Intent intent = new Intent(UserDashboard.this, NotificationsActivity.class);
                            intent.putExtra("announcementId", announcementId);
                            startActivity(intent);
                        } else {
                            Toast.makeText(UserDashboard.this, "No announcements found", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(UserDashboard.this, "Failed to get announcement", Toast.LENGTH_SHORT).show();
                    });
        });




        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(UserDashboard.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
