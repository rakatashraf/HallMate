package com.example.hallmate.adminside;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hallmate.R;
import com.example.hallmate.adapter.MealStudentAdapter;
import com.example.hallmate.model.MealStudent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import android.text.TextWatcher;
import android.text.Editable;
import android.content.DialogInterface;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class AdminDashboard extends AppCompatActivity {

    private TextView tvTomorrowMealCount;
    private TextView btnViewStudents, btnEditMealCosts, btnEditMealMenu, btnViewComplaints, btnAnnouncements, btnSendPaymentNotification, btnLogout;
    private RecyclerView rvStudents;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private String hallName;
    private String firstName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        tvTomorrowMealCount = findViewById(R.id.tvMealCount);
        btnViewStudents = findViewById(R.id.btnViewStudents);
        btnEditMealCosts = findViewById(R.id.btnEditMealCosts);
        btnEditMealMenu = findViewById(R.id.btnEditMealMenu);
        btnViewComplaints = findViewById(R.id.btnViewComplaints);
        btnAnnouncements = findViewById(R.id.btnAnnouncements);
        //btnSendPaymentNotification = findViewById(R.id.btnSendPaymentNotification);
        btnLogout = findViewById(R.id.btnLogout);
        rvStudents = findViewById(R.id.rvStudents);
        rvStudents.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        firstName = getIntent().getStringExtra("FIRST_NAME");
        hallName = getIntent().getStringExtra("HALL_NAME");

        checkMealStatus();
        setupButtonListeners();
    }

    private void checkMealStatus() {
        Calendar now = Calendar.getInstance();
        int hour = now.get(Calendar.HOUR_OF_DAY);

        boolean isAfter7PM = hour >= 19;

        Calendar countCal = Calendar.getInstance();
        if (isAfter7PM) countCal.add(Calendar.DAY_OF_YEAR, 1);
        String countDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(countCal.getTime());

        Calendar listCal = Calendar.getInstance();
        String listDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(listCal.getTime());

        CollectionReference mealStatusRef = db.collection("MealStatus")
                .document(hallName)
                .collection("members");

        mealStatusRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<MealStudent> studentsToShow = new ArrayList<>();
                final AtomicInteger counter = new AtomicInteger();
                int totalMembers = task.getResult().size();
                final AtomicInteger mealCount = new AtomicInteger(0);

                if (totalMembers == 0) {
                    // No members, update UI accordingly
                    tvTomorrowMealCount.setText("🍽️ Tomorrow's Meals Count: 0");
                    setupStudentListRecyclerView(studentsToShow, listDate);
                    return;
                }

                for (DocumentSnapshot doc : task.getResult()) {
                    String userId = doc.getId();

                    db.collection("MealStatus")
                            .document(hallName)
                            .collection("members")
                            .document(userId)
                            .get()
                            .addOnSuccessListener(mealDoc -> {
                                Boolean countStatus = mealDoc.getBoolean(countDate);
                                Boolean listStatus = mealDoc.getBoolean(listDate);

                                if (Boolean.TRUE.equals(countStatus)) {
                                    mealCount.incrementAndGet();
                                }

                                if (Boolean.TRUE.equals(listStatus)) {
                                    db.collection("Users")
                                            .document(hallName)
                                            .collection("members")
                                            .document(userId)
                                            .get()
                                            .addOnSuccessListener(userDoc -> {
                                                String name = userDoc.getString("name");
                                                String roomNumber = userDoc.getString("roomNumber");
                                                String studentId = userDoc.getString("studentId");
                                                if (studentId == null) {
                                                    studentId = "Unknown";
                                                }

                                                DocumentReference mealDocRef = db.collection("Meals")
                                                        .document(hallName)
                                                        .collection("dates")
                                                        .document(listDate)
                                                        .collection("studentList")
                                                        .document(userId);

                                                String finalStudentId = studentId;
                                                mealDocRef.get().addOnSuccessListener(mealStatusDoc -> {
                                                    boolean breakfast = false, lunch = false, dinner = false;
                                                    if (mealStatusDoc.exists()) {
                                                        Boolean b = mealStatusDoc.getBoolean("breakfast");
                                                        Boolean l = mealStatusDoc.getBoolean("lunch");
                                                        Boolean d = mealStatusDoc.getBoolean("dinner");
                                                        breakfast = (b != null && b);
                                                        lunch = (l != null && l);
                                                        dinner = (d != null && d);
                                                    } else {
                                                        Map<String, Object> blankEntry = new HashMap<>();
                                                        blankEntry.put("breakfast", false);
                                                        blankEntry.put("lunch", false);
                                                        blankEntry.put("dinner", false);
                                                        mealDocRef.set(blankEntry);
                                                    }

                                                    MealStudent student = new MealStudent(userId, name, roomNumber, finalStudentId, breakfast, lunch, dinner, true);
                                                    studentsToShow.add(student);

                                                    if (counter.incrementAndGet() == totalMembers) {
                                                        setupStudentListRecyclerView(studentsToShow, listDate);
                                                        tvTomorrowMealCount.setText("🍽️ Tomorrow's Meals Count: " + mealCount.get());
                                                    }
                                                });
                                            });
                                } else {
                                    if (counter.incrementAndGet() == totalMembers) {
                                        setupStudentListRecyclerView(studentsToShow, listDate);
                                        tvTomorrowMealCount.setText("🍽️ Tomorrow's Meals Count: " + mealCount.get());
                                    }
                                }
                            });
                }
            } else {
                Toast.makeText(this, "Failed to load meal statuses", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupStudentListRecyclerView(List<MealStudent> studentList, String date) {
        MealStudentAdapter adapter = new MealStudentAdapter(studentList, hallName, date);
        rvStudents.setAdapter(adapter);

        adapter.setOnCheckedChangeListener((student, mealType, isChecked) -> {
            String userId = student.getUserId();
            DocumentReference docRef = db.collection("Meals")
                    .document(hallName)
                    .collection("dates")
                    .document(date)
                    .collection("studentList")
                    .document(userId);

            Map<String, Object> updates = new HashMap<>();
            updates.put(mealType, isChecked);
            docRef.set(updates, SetOptions.merge());
        });

        EditText searchBox = findViewById(R.id.etSearch);
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setupButtonListeners() {
        btnViewStudents.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboard.this, StudentListActivity.class);
            intent.putExtra("hallName", hallName);
            startActivity(intent);
        });

        btnEditMealCosts.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboard.this, MealCostSummaryActivity.class);
            intent.putExtra("HALL_NAME", hallName);
            startActivity(intent);
        });

        btnEditMealMenu.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboard.this, EditMealMenuActivity.class);
            intent.putExtra("hallName", hallName);
            startActivity(intent);
        });

        btnViewComplaints.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboard.this, AdminComplaintActivity.class);
            intent.putExtra("hallName", hallName);
            startActivity(intent);
        });

        btnAnnouncements.setOnClickListener(v -> showAnnouncementDialog());

       // btnSendPaymentNotification.setOnClickListener(v -> sendPaymentNotifications());

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent loginIntent = new Intent(AdminDashboard.this, com.example.hallmate.LoginActivity.class);
            startActivity(loginIntent);
            finish();
        });
    }

    private void showAnnouncementDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Write Announcement");

        final EditText input = new EditText(this);
        input.setHint("Type your message here");
        builder.setView(input);

        builder.setPositiveButton("Send", (dialog, which) -> {
            String message = input.getText().toString().trim();
            if (!message.isEmpty()) {
                saveAnnouncementToFirestore(message);
            } else {
                Toast.makeText(getApplicationContext(), "Message cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void saveAnnouncementToFirestore(String message) {
        Map<String, Object> announcement = new HashMap<>();
        announcement.put("message", message);
        announcement.put("hallName", hallName);
        announcement.put("firstName", firstName);
        announcement.put("timestamp", FieldValue.serverTimestamp());

        db.collection("Announcement")
                .add(announcement)
                .addOnSuccessListener(documentReference -> Toast.makeText(AdminDashboard.this, "Announcement sent", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(AdminDashboard.this, "Failed to send announcement", Toast.LENGTH_SHORT).show());
    }

   /* private void sendPaymentNotifications() {
        // Calculate date range from 16 days ago up to today
        Calendar endDateCal = Calendar.getInstance();
        String endDateStr = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(endDateCal.getTime());

        Calendar startDateCal = (Calendar) endDateCal.clone();
        startDateCal.add(Calendar.DAY_OF_YEAR, -16);
        String startDateStr = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(startDateCal.getTime());

        db.collection("Bills")
                .document(hallName)
                .collection("dates")
                .whereGreaterThanOrEqualTo("date", startDateStr)
                .whereLessThanOrEqualTo("date", endDateStr)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    Set<String> usersWithUnpaidBills = new HashSet<>();
                    for (DocumentSnapshot billDoc : querySnapshot.getDocuments()) {
                        Boolean isPaid = billDoc.getBoolean("isPaid");
                        String userId = billDoc.getString("userId");
                        if (Boolean.FALSE.equals(isPaid) && userId != null) {
                            usersWithUnpaidBills.add(userId);
                        }
                    }

                    if (usersWithUnpaidBills.isEmpty()) {
                        Toast.makeText(AdminDashboard.this, "No unpaid bills in last 16 days", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    db.collection("Users")
                            .document(hallName)
                            .collection("members")
                            .whereIn(FieldPath.documentId(), new ArrayList<>(usersWithUnpaidBills))
                            .get()
                            .addOnSuccessListener(userQuery -> {
                                StringBuilder notificationMessage = new StringBuilder();
                                for (DocumentSnapshot userDoc : userQuery.getDocuments()) {
                                    String name = userDoc.getString("name");
                                    String roomNumber = userDoc.getString("roomNumber");
                                    String studentId = userDoc.getString("studentId");
                                    notificationMessage.append(name)
                                            .append(" (Room ")
                                            .append(roomNumber)
                                            .append(", ID: ")
                                            .append(studentId)
                                            .append(")\n");
                                }
                                if (notificationMessage.length() == 0) {
                                    Toast.makeText(AdminDashboard.this, "No user info found for unpaid bills", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                AlertDialog.Builder builder = new AlertDialog.Builder(AdminDashboard.this);
                                builder.setTitle("Unpaid Bills Notification");
                                builder.setMessage("These students have unpaid bills for the last 16 days:\n\n" + notificationMessage.toString());
                                builder.setPositiveButton("OK", null);
                                builder.show();
                            })
                            .addOnFailureListener(e -> Toast.makeText(AdminDashboard.this, "Failed to fetch users", Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e -> Toast.makeText(AdminDashboard.this, "Failed to fetch bills", Toast.LENGTH_SHORT).show());
    }*/
}
