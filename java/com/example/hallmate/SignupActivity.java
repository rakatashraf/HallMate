package com.example.hallmate;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hallmate.adminside.AdminDashboard;
import com.example.hallmate.userside.UserDashboard;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class SignupActivity extends AppCompatActivity {

    private EditText nameET, idET, phoneET, emailET, passwordET, roomNumberET, batchET, departmentET, sessionET;
    private AutoCompleteTextView hallNameET;
    private Button signupBtn;
    private TextView backRegister;
    private ProgressBar progressBar;
    private ImageView passwordToggle;

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Initialize Views
        nameET = findViewById(R.id.name);
        idET = findViewById(R.id.id);
        phoneET = findViewById(R.id.phone);
        emailET = findViewById(R.id.email);
        passwordET = findViewById(R.id.password);
        roomNumberET = findViewById(R.id.roomNumber);
        batchET = findViewById(R.id.batch);
        departmentET = findViewById(R.id.department);
        sessionET = findViewById(R.id.session);
        hallNameET = findViewById(R.id.hallName);
        signupBtn = findViewById(R.id.signupBtn);
        backRegister = findViewById(R.id.backRegister);
        progressBar = findViewById(R.id.progressBar);
        passwordToggle = findViewById(R.id.passwordToggle);

        // Set hall names
        String[] halls = {
                "Boral Hall", "ChalanBill Hall", "Jomuna Hall",
                "Brahmaputra Hall", "Kopotakkho Hall", "Padma Hall", "Bonolota Hall"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, halls);
        hallNameET.setAdapter(adapter);

        // Set up toggle
        setupPasswordToggle();

        // Register click
        signupBtn.setOnClickListener(v -> checkStudentIdAndRegister());

        // Go to login
        backRegister.setOnClickListener(v -> {
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void setupPasswordToggle() {
        passwordToggle.setOnClickListener(v -> {
            isPasswordVisible = !isPasswordVisible;
            if (isPasswordVisible) {
                passwordET.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                passwordToggle.setImageResource(R.drawable.ic_visibility_off);
            } else {
                passwordET.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                passwordToggle.setImageResource(R.drawable.ic_visibility);
            }
            passwordET.setSelection(passwordET.getText().length()); // Keep cursor at end
        });
    }

    private void checkStudentIdAndRegister() {
        String studentId = idET.getText().toString().trim();

        if (TextUtils.isEmpty(studentId)) {
            Toast.makeText(this, "Student ID is required", Toast.LENGTH_SHORT).show();
            return;
        }

        firestore.collectionGroup("members")
                .whereEqualTo("studentId", studentId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            Toast.makeText(this, "Student ID already exists. Please login.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                            finish();
                        } else {
                            registerUser();
                        }
                    } else {
                        String error = (task.getException() != null) ? task.getException().getMessage() : "Unknown error";
                        Toast.makeText(this, "Error: " + error, Toast.LENGTH_SHORT).show();
                        Log.e("Firestore", error);
                    }
                });
    }

    private void registerUser() {
        String name = nameET.getText().toString().trim();
        String studentId = idET.getText().toString().trim();
        String phone = phoneET.getText().toString().trim();
        String email = emailET.getText().toString().trim();
        String password = passwordET.getText().toString().trim();
        String hallName = hallNameET.getText().toString().trim();
        String room = roomNumberET.getText().toString().trim();
        String batch = batchET.getText().toString().trim();
        String dept = departmentET.getText().toString().trim();
        String session = sessionET.getText().toString().trim();

        if (name.isEmpty() || studentId.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty() || hallName.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        signupBtn.setEnabled(false);

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    signupBtn.setEnabled(true);
                    if (task.isSuccessful()) {
                        String uid = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
                        if (uid == null) return;

                        HashMap<String, Object> userMap = new HashMap<>();
                        userMap.put("uid", uid);
                        userMap.put("name", name);
                        userMap.put("studentId", studentId);
                        userMap.put("phone", phone);
                        userMap.put("email", email);
                        userMap.put("hall", hallName);
                        userMap.put("roomNumber", room);
                        userMap.put("batch", batch);
                        userMap.put("department", dept);
                        userMap.put("session", session);
                        userMap.put("role", "user");

                        firestore.collection("UsersDirectory").document(uid)
                                .set(userMap)
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(SignupActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(SignupActivity.this, UserDashboard.class));
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(SignupActivity.this, "Firestore Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });

                        // Also store email and password in 'Users' collection for admin check
                        HashMap<String, Object> loginMap = new HashMap<>();
                        loginMap.put("email", email);
                        loginMap.put("password", password);
                        firestore.collection("Users").document(uid).set(loginMap);

                    } else {
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            Toast.makeText(this, "Email already in use. Please log in.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}