package com.example.hallmate.userside;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hallmate.R;
import com.example.hallmate.adapter.DailyMealCostAdapter;
import com.example.hallmate.model.DailyMealCostModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class DailyMealCostActivity extends AppCompatActivity {

    private static final String TAG = "DailyMealCostActivity";
    private RecyclerView recyclerView;
    private DailyMealCostAdapter adapter;
    private List<DailyMealCostModel> dailyMealCostList;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String userId, hallName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_meal_cost);

        initializeViews();
        setupFirebase();
        fetchUserHall();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recyclerViewMealCosts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dailyMealCostList = new ArrayList<>();
        adapter = new DailyMealCostAdapter(dailyMealCostList);
        recyclerView.setAdapter(adapter);
    }

    private void setupFirebase() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    private void fetchUserHall() {
        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        userId = auth.getCurrentUser().getUid();
        db.collection("UsersDirectory").document(userId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    hallName = snapshot.getString("hallName");
                    if (hallName != null) {
                        loadDailyMealCosts();
                    } else {
                        Toast.makeText(this, "Hall information not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to fetch user data", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error fetching user hall", e);
                });
    }

    private void loadDailyMealCosts() {
        db.collection("DailyCost").document(hallName).collection("DateCosts")
                .get()
                .addOnSuccessListener(dateSnapshots -> {
                    if (dateSnapshots.isEmpty()) {
                        Toast.makeText(this, "No meal cost data available", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    for (QueryDocumentSnapshot dateDoc : dateSnapshots) {
                        String rawDate = dateDoc.getId(); // likely in dd-MM-yyyy format
                        String firestoreDate = convertDateToFirestoreFormat(rawDate);
                        processDateCost(firestoreDate, dateDoc);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to fetch meal costs", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error loading daily meal costs", e);
                });
    }

    private void processDateCost(String date, DocumentSnapshot dateDoc) {
        Log.d("MealCount", "Processing date: " + date);

        db.collection("MealStatus").document(hallName)
                .collection("members")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    int studentCount = 0;
                    List<String> mealUserIds = new ArrayList<>();

                    for (DocumentSnapshot doc : querySnapshot) {
                        Boolean hadMeal = doc.getBoolean(date); // Date key in yyyy-MM-dd format

                        Log.d("MealCount", "User: " + doc.getId() + " - " + date + " field value: " + hadMeal);

                        if (Boolean.TRUE.equals(hadMeal)) {
                            studentCount++;
                            mealUserIds.add(doc.getId());
                        }
                    }

                    Log.d("MealCount", "Total students with meal on " + date + ": " + studentCount);

                    double totalStudentMealCost = getDouble(dateDoc, "studentMealCost");
                    double staffMealCost = getDouble(dateDoc, "staffMealCost");
                    double utilityBill = getDouble(dateDoc, "utilityBill");

                    double perStudentMealCost = (studentCount == 0) ? 0.0 : totalStudentMealCost / studentCount;

                    Log.d("MealCostDebug", "Total Student Meal Cost for " + date + ": " + totalStudentMealCost);
                    Log.d("MealCostDebug", "Total Student Count (from boolean check): " + studentCount);

                    db.collection("Users").document(hallName).collection("members")
                            .whereEqualTo("role", "user")
                            .get()
                            .addOnSuccessListener(userSnapshot -> {
                                int totalUsers = userSnapshot.size();
                                double myStaffCost = calculateSharedCost(staffMealCost, totalUsers);
                                double myUtilityBill = calculateSharedCost(utilityBill, totalUsers);

                                addDailyMealCostModel(date, perStudentMealCost, myStaffCost, myUtilityBill);

                                boolean userHadMeal = mealUserIds.contains(userId);
                                double finalStudentCost = userHadMeal ? perStudentMealCost : 0.0;

                                updateUserBills(date, finalStudentCost, myStaffCost, myUtilityBill);
                                updateStudentStatus(date, userHadMeal, finalStudentCost);
                            })
                            .addOnFailureListener(e -> Log.e(TAG, "Error fetching users", e));
                })
                .addOnFailureListener(e -> Log.e("MealCount", "Failed to fetch meal data", e));
    }

    // Converts from dd-MM-yyyy to yyyy-MM-dd for Firestore date field keys
    private String convertDateToFirestoreFormat(String inputDate) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = inputFormat.parse(inputDate);
            return outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return inputDate;  // fallback to original string
        }
    }

    private double calculateSharedCost(double totalCost, int userCount) {
        if (userCount == 0) return 0.0;

        return totalCost / userCount;
    }

    private void addDailyMealCostModel(String date, double studentCost, double staffCost, double utilityBill) {
        DailyMealCostModel model = new DailyMealCostModel(
                date,
                String.format("%.2f", studentCost),
                String.format("%.2f", staffCost),
                String.format("%.2f", utilityBill)
        );

        dailyMealCostList.add(model);
        adapter.notifyDataSetChanged();
    }

    private void updateUserBills(String date, double studentCost, double staffCost, double utilityBill) {
        String month = extractMonthFromDate(date);

        Map<String, Object> bill = new HashMap<>();
        bill.put("studentMealCost", studentCost);
        bill.put("staffMealCost", staffCost);
        bill.put("utilityBill", utilityBill);
        bill.put("date", date);
        bill.put("timestamp", FieldValue.serverTimestamp());

        db.collection("UserBills").document(userId)
                .collection("Month-" + month).document(date)
                .set(bill);

        db.collection("UserBillsHallWise").document(hallName)
                .collection("Month-" + month).document(date)
                .collection("Users").document(userId)
                .set(bill);
    }

    private void updateStudentStatus(String date, boolean userMealStatus, double studentCost) {
        Map<String, Object> studentData = new HashMap<>();
        studentData.put("studentMealCost", studentCost);
        studentData.put("hasMeal", userMealStatus);

        db.collection("Meals").document(hallName)
                .collection("dates").document(date)
                .collection("studentList").document(userId)
                .set(studentData);

        db.collection("Users").document(hallName)
                .collection("members").document(userId)
                .update("studentMealCost", studentCost);
    }

    private String extractMonthFromDate(String date) {
        String[] parts = date.split("-");
        return parts.length > 1 ? parts[1] : "01";
    }

    private double getDouble(DocumentSnapshot doc, String field) {
        Object value = doc.get(field);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return 0.0;
    }
}
