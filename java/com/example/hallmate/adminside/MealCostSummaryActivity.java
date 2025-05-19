package com.example.hallmate.adminside;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.hallmate.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class MealCostSummaryActivity extends AppCompatActivity {

    private TextView tvTotalMealsToday, tvTotalCostToday;
    private EditText etStudentMealCost, etStaffMealCost, etUtilityBill;
    private Button btnSaveCosts;

    private FirebaseFirestore db;
    private String todayDate, currentMonthYear;
    private String hallName = "";
    private double utilityBillValue = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_cost_summary);

        tvTotalMealsToday = findViewById(R.id.tvTotalMealsToday);
        tvTotalCostToday = findViewById(R.id.tvTotalCostToday);
        etStudentMealCost = findViewById(R.id.etStudentMealCost);
        etStaffMealCost = findViewById(R.id.etStaffMealCost);
        etUtilityBill = findViewById(R.id.etUtilityBill);
        btnSaveCosts = findViewById(R.id.btnSaveCosts);

        db = FirebaseFirestore.getInstance();

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        todayDate = sdf.format(new Date());

        SimpleDateFormat monthFormat = new SimpleDateFormat("MM-yyyy", Locale.getDefault());
        currentMonthYear = monthFormat.format(new Date());

        // Get hall name from Intent
        hallName = getIntent().getStringExtra("HALL_NAME");

        if (hallName == null || hallName.isEmpty()) {
            Toast.makeText(this, "Hall name not available. Please login again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadTodaySummary();
        loadTodayCosts();
        loadMonthlyUtilityBill();

        btnSaveCosts.setOnClickListener(v -> {
            saveCosts();
        });
    }

    private void loadTodaySummary() {
        DocumentReference summaryRef = db.collection("DailyMealSummary")
                .document(hallName)
                .collection("DateSummary")
                .document(todayDate);

        summaryRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Long totalMeals = documentSnapshot.getLong("totalMeals");
                Double totalCost = documentSnapshot.getDouble("totalCost");

                tvTotalMealsToday.setText("Total Meals Today: " + (totalMeals != null ? totalMeals : 0));
                tvTotalCostToday.setText("Total Cost Today: " + (totalCost != null ? totalCost : 0));
            }
        });
    }

    private void loadTodayCosts() {
        DocumentReference costRef = db.collection("DailyCost")
                .document(hallName)
                .collection("DateCosts")
                .document(todayDate);

        costRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Double studentCost = documentSnapshot.getDouble("studentMealCost");
                Double staffCost = documentSnapshot.getDouble("staffMealCost");

                if (studentCost != null) {
                    etStudentMealCost.setText(String.valueOf(studentCost));
                }
                if (staffCost != null) {
                    etStaffMealCost.setText(String.valueOf(staffCost));
                }
            }
        });
    }

    private void loadMonthlyUtilityBill() {
        DocumentReference billRef = db.collection("MonthlyBills")
                .document(currentMonthYear);

        billRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                utilityBillValue = documentSnapshot.getDouble("utilityBill") != null ? documentSnapshot.getDouble("utilityBill") : 0;
                etUtilityBill.setText(String.valueOf(utilityBillValue));
            }
        });
    }

    private void saveCosts() {
        String studentCost = etStudentMealCost.getText().toString();
        String staffCost = etStaffMealCost.getText().toString();
        String utilityBill = etUtilityBill.getText().toString();

        if (studentCost.isEmpty() || staffCost.isEmpty() || utilityBill.isEmpty()) {
            Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<String, Object> dailyCostData = new HashMap<>();
        dailyCostData.put("studentMealCost", Double.parseDouble(studentCost));
        dailyCostData.put("staffMealCost", Double.parseDouble(staffCost));
        dailyCostData.put("utilityBill", Double.parseDouble(utilityBill));
        dailyCostData.put("timestamp", FieldValue.serverTimestamp());

        db.collection("DailyCost")
                .document(hallName)
                .collection("DateCosts")
                .document(todayDate)
                .set(dailyCostData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(MealCostSummaryActivity.this, "Costs updated successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(MealCostSummaryActivity.this, "Error saving costs. Please try again.", Toast.LENGTH_SHORT).show();
                });
    }
}
