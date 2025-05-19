package com.example.hallmate.userside;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hallmate.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class WeeklyMenuActivity extends AppCompatActivity {

    private TableLayout tableLayout;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String userId, hallName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_menu);

        tableLayout = findViewById(R.id.tableLayoutMenu);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        userId = auth.getCurrentUser().getUid();

        // Add header
        addTableHeader();

        db.collection("UsersDirectory").document(userId).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        hallName = doc.getString("hallName");
                        if (hallName != null && !hallName.trim().isEmpty()) {
                            loadMenu(hallName.trim());
                        } else {
                            Toast.makeText(this, "hallName is null or empty", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error getting user info", Toast.LENGTH_SHORT).show();
                });
    }

    private void loadMenu(String hallName) {
        db.collection("MealMenus")
                .document(hallName)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        Toast.makeText(this, "Menu not found for hall: " + hallName, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String[] days = {"Saturday", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};

                    for (String day : days) {
                        if (documentSnapshot.contains(day)) {
                            Object dayDataObj = documentSnapshot.get(day);
                            if (dayDataObj instanceof Map) {
                                Map<String, Object> dayData = (Map<String, Object>) dayDataObj;

                                String breakfast = (String) dayData.get("Breakfast");
                                String lunch = (String) dayData.get("Lunch");
                                String dinner = (String) dayData.get("Dinner");

                                TableRow row = new TableRow(this);
                                row.addView(createStyledTextView(day));
                                row.addView(createStyledTextView(breakfast));
                                row.addView(createStyledTextView(lunch));
                                row.addView(createStyledTextView(dinner));
                                tableLayout.addView(row);
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load menu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void addTableHeader() {
        TableRow header = new TableRow(this);
        header.addView(createHeaderTextView("Day"));
        header.addView(createHeaderTextView("Breakfast"));
        header.addView(createHeaderTextView("Lunch"));
        header.addView(createHeaderTextView("Dinner"));
        tableLayout.addView(header);
    }

    private TextView createStyledTextView(String text) {
        TextView tv = new TextView(this);
        tv.setText(text != null ? text : "-");
        tv.setTextSize(15);
        tv.setTextColor(Color.BLACK);
        tv.setPadding(32, 24, 32, 24);
        tv.setBackgroundResource(R.drawable.table_cell_row_bg);

        TableRow.LayoutParams params = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(8, 8, 8, 8);
        tv.setLayoutParams(params);

        return tv;
    }

    private TextView createHeaderTextView(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextColor(Color.WHITE);
        tv.setTypeface(null, Typeface.BOLD);
        tv.setTextSize(15);
        tv.setPadding(32, 24, 32, 24);
        tv.setBackgroundResource(R.drawable.table_cell_header_bg);

        TableRow.LayoutParams params = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(8, 8, 8, 8);
        tv.setLayoutParams(params);

        return tv;
    }
}
