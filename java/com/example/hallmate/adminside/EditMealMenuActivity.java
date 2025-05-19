package com.example.hallmate.adminside;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hallmate.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditMealMenuActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private String hallName = "";

    // Store all EditTexts for easy mapping
    private final String[] days = {
            "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"
    };

    private final String[] meals = {"Breakfast", "Lunch", "Dinner"};

    private final Map<String, EditText> editTextMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_meal_menu);

        db = FirebaseFirestore.getInstance();
        hallName = getIntent().getStringExtra("hallName");

        for (String day : days) {
            for (String meal : meals) {
                String id = "editText" + day + meal;
                int resId = getResources().getIdentifier(id, "id", getPackageName());
                editTextMap.put(day + "_" + meal, findViewById(resId));
            }
        }

        Button btnSave = findViewById(R.id.buttonSaveMenu);
        btnSave.setOnClickListener(v -> saveWeeklyMenu());
    }

    private void saveWeeklyMenu() {
        Map<String, Map<String, String>> weeklyMenu = new HashMap<>();

        for (String day : days) {
            Map<String, String> dayMenu = new HashMap<>();
            for (String meal : meals) {
                EditText input = editTextMap.get(day + "_" + meal);
                if (input != null) {
                    dayMenu.put(meal, input.getText().toString().trim());
                }
            }
            weeklyMenu.put(day, dayMenu);
        }

        db.collection("MealMenus").document(hallName)
                .set(weeklyMenu)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Weekly Meal Menu Saved", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
