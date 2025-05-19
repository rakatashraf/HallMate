package com.example.hallmate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hallmate.adminside.AdminDashboard;
import com.example.hallmate.userside.UserDashboard;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private EditText emailET, passwordET;
    private Button loginBtn;
    private TextView forgotPasswordBtn, goToSignup;
    private ProgressBar progressBar;

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    private boolean dashboardLaunched = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailET = findViewById(R.id.email);
        passwordET = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginBtn);
        forgotPasswordBtn = findViewById(R.id.forgotPasswordBtn);
        goToSignup = findViewById(R.id.goToSignup);
        progressBar = findViewById(R.id.progressBar);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        loginBtn.setOnClickListener(v -> {
            String email = emailET.getText().toString().trim();
            String password = passwordET.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailET.setError("Enter a valid email address");
                emailET.requestFocus();
                return;
            }

            if (password.length() < 6) {
                passwordET.setError("Password must be at least 6 characters");
                passwordET.requestFocus();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);
            loginBtn.setEnabled(false);

            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                progressBar.setVisibility(View.GONE);
                loginBtn.setEnabled(true);

                if (task.isSuccessful()) {
                    FirebaseUser user = auth.getCurrentUser();
                    if (user != null) {
                        if ("fardinjahan1000@gmail.com".equalsIgnoreCase(user.getEmail())) {
                            launchAdminDashboard();
                        } else {
                            fetchUserDetails(user.getUid());
                        }
                    }
                } else {
                    Toast.makeText(this, "Login Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });

        forgotPasswordBtn.setOnClickListener(v -> {
            String email = emailET.getText().toString().trim();
            if (email.isEmpty()) {
                emailET.setError("Please enter your email");
                emailET.requestFocus();
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailET.setError("Enter a valid email address");
                emailET.requestFocus();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);
            forgotPasswordBtn.setEnabled(false);

            auth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                progressBar.setVisibility(View.GONE);
                forgotPasswordBtn.setEnabled(true);
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Reset link sent to your email.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });

        goToSignup.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignupActivity.class));
        });
    }

    private void fetchUserDetails(String uid) {
        firestore.collection("UsersDirectory")
                .document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String hallName = doc.getString("hallName");
                        if (hallName == null || hallName.isEmpty()) {
                            Toast.makeText(this, "Hall name missing. Contact admin.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        firestore.collection("Users")
                                .document(hallName)
                                .collection("members")
                                .document(uid)
                                .get()
                                .addOnSuccessListener(userSnapshot -> {
                                    if (userSnapshot.exists()) {
                                        String role = userSnapshot.getString("role");
                                        String firstName = userSnapshot.getString("name");

                                        if (role != null && !role.isEmpty()) {
                                            launchDashboard(role, firstName, hallName);
                                        } else {
                                            Toast.makeText(this, "User role not found.", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(this, "User data not found under hall: " + hallName, Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Failed to fetch user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Toast.makeText(this, "User directory not found. Signup might have failed.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error fetching user directory: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void launchDashboard(String role, String name, String hallName) {
        if (dashboardLaunched) return;
        dashboardLaunched = true;

        Intent intent;
        if ("admin".equalsIgnoreCase(role)) {
            Toast.makeText(this, "Welcome Admin", Toast.LENGTH_SHORT).show();
            intent = new Intent(this, AdminDashboard.class);
        } else {
            Toast.makeText(this, "Welcome User", Toast.LENGTH_SHORT).show();
            intent = new Intent(this, UserDashboard.class);
        }

        intent.putExtra("FIRST_NAME", name);
        intent.putExtra("HALL_NAME", hallName);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void launchAdminDashboard() {
        if (dashboardLaunched) return;
        dashboardLaunched = true;

        Toast.makeText(this, "Welcome Admin", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, AdminDashboard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}