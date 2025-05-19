package com.example.hallmate.userside;

import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;


import androidx.appcompat.app.AppCompatActivity;

import com.example.hallmate.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MealToggleActivity extends AppCompatActivity {

    CalendarView calendarView;
    CheckBox mealCheckbox;
    Switch switchMeals;
    TextView welcomeMessage;
    FirebaseFirestore db;
    FirebaseAuth auth;
    String userId, hallName, firstName, roomNo;
    Date selectedDate;

    boolean globalMealOff = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_toggle);

        // Initialize Views
        calendarView = findViewById(R.id.calendarView);
        mealCheckbox = findViewById(R.id.mealCheckbox);
        switchMeals = findViewById(R.id.switchMeals);
        welcomeMessage = findViewById(R.id.welcomeMessage);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        userId = auth.getCurrentUser().getUid();

        hallName = getIntent().getStringExtra("HALL_NAME");
        firstName = getIntent().getStringExtra("FIRST_NAME");
        roomNo = getIntent().getStringExtra("ROOM_NO");

        if (firstName != null) {
            welcomeMessage.setText("Hi " + firstName.split(" ")[0]);
        } else {
            welcomeMessage.setText("Hi User");
        }

        selectedDate = new Date(calendarView.getDate());

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, dayOfMonth);
            selectedDate = calendar.getTime();
            checkMealStatus();
        });

        mealCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChangeAllowed(this,selectedDate)) {
                updateMealStatus(selectedDate, isChecked);
            } else {
                Toast.makeText(this, "You can't change status for tomorrow after 7 PM", Toast.LENGTH_SHORT).show();
                checkMealStatus();
            }
        });

        switchMeals.setOnCheckedChangeListener((buttonView, isChecked) -> {
            globalMealOff = !isChecked;
            switchMeals.setText(globalMealOff ? "All Meals On" : "All Meals Off");
            updateAllFutureMealStatus(globalMealOff);

            if (isChecked) {
                switchMeals.setThumbTintList(getResources().getColorStateList(R.color.red));
                switchMeals.setTrackTintList(getResources().getColorStateList(R.color.track_color_on));
            } else {
                switchMeals.setThumbTintList(getResources().getColorStateList(R.color.green));
                switchMeals.setTrackTintList(getResources().getColorStateList(R.color.track_color_off));
            }
        });

        ensureUserDocumentCreated();
        checkMealStatus();
    }

    private boolean isChangeAllowed(Context context, Date selectedDate) {
        Calendar now = Calendar.getInstance();

        // Get today's date (with time stripped)
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        // Get tomorrow's date (with time stripped)
        Calendar tomorrow = (Calendar) today.clone();
        tomorrow.add(Calendar.DAY_OF_YEAR, 1);

        // Set selected date (with time stripped)
        Calendar selected = Calendar.getInstance();
        selected.setTime(selectedDate);
        selected.set(Calendar.HOUR_OF_DAY, 0);
        selected.set(Calendar.MINUTE, 0);
        selected.set(Calendar.SECOND, 0);
        selected.set(Calendar.MILLISECOND, 0);

        // Disallow if selected date is before today
        if (selected.before(today)) {
            Toast.makeText(context, "Previous date status can't be changed", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Disallow if selected date is today (running date)
        if (selected.equals(today)) {
            Toast.makeText(context, "Running date status can't be changed", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Disallow if selected date is tomorrow but time is after 7 PM
        if (selected.equals(tomorrow) && now.get(Calendar.HOUR_OF_DAY) >= 19) {
            Toast.makeText(context, "Status can't be changed after 7 PM", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true; // Only allowed if selected date is tomorrow and before 7 PM
    }




    private void checkMealStatus() {
        if (hallName == null || userId == null) return;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dateKey = sdf.format(selectedDate);

        db.collection("MealStatus")
                .document(hallName)
                .collection("members")
                .document(userId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists() && doc.contains(dateKey)) {
                        mealCheckbox.setChecked(doc.getBoolean(dateKey));
                    } else {
                        mealCheckbox.setChecked(true);
                        updateMealStatus(selectedDate, true);
                    }
                });
    }

    private void updateMealStatus(Date date, boolean status) {
        if (hallName == null || userId == null) return;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dateKey = sdf.format(date);

        Map<String, Object> mealStatus = new HashMap<>();
        mealStatus.put(dateKey, status);

        db.collection("MealStatus")
                .document(hallName)
                .collection("members")
                .document(userId)
                .set(mealStatus, SetOptions.merge())
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Meal status updated", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to update meal status", Toast.LENGTH_SHORT).show());
    }

    private void updateAllFutureMealStatus(boolean status) {
        if (hallName == null || userId == null) return;

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1); // Start from tomorrow
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        Map<String, Object> updates = new HashMap<>();

        // Change for next 365 days
        for (int i = 0; i < 365; i++) {
            String dateKey = sdf.format(calendar.getTime());
            updates.put(dateKey, status);
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        db.collection("MealStatus")
                .document(hallName)
                .collection("members")
                .document(userId)
                .set(updates, SetOptions.merge())
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Future meals updated for 1 year", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to update future meals", Toast.LENGTH_SHORT).show());
    }

    private void ensureUserDocumentCreated() {
        if (hallName == null || userId == null || firstName == null || roomNo == null) return;

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("name", firstName);
        userInfo.put("room", roomNo);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        for (int i = 0; i < 30; i++) {
            String dateKey = sdf.format(calendar.getTime());
            userInfo.put(dateKey, true);
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        db.collection("MealStatus")
                .document(hallName)
                .collection("members")
                .document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().exists()) {
                            db.collection("MealStatus")
                                    .document(hallName)
                                    .collection("members")
                                    .document(userId)
                                    .set(userInfo, SetOptions.merge())
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(MealToggleActivity.this, "User document created with default meal status", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(MealToggleActivity.this, "Failed to create user document", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    }
                });
    }
}
