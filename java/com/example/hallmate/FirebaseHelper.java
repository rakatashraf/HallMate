package com.example.hallmate;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class FirebaseHelper {

    private final FirebaseAuth auth;
    private final FirebaseFirestore firestore;
    private final Context context;

    public FirebaseHelper(Context context) {
        this.context = context;
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }

    public interface AuthCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    public void registerUser(String name, String email, String password, String phone, AuthCallback callback) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        if (firebaseUser != null) {
                            String uid = firebaseUser.getUid();

                            HashMap<String, Object> userMap = new HashMap<>();
                            userMap.put("uid", uid);
                            userMap.put("name", name);
                            userMap.put("email", email);
                            userMap.put("phone", phone);

                            // Save user to Firestore
                            firestore.collection("users")
                                    .document(uid)
                                    .set(userMap)
                                    .addOnSuccessListener(aVoid -> {
                                        // Send verification email
                                        firebaseUser.sendEmailVerification()
                                                .addOnSuccessListener(unused ->
                                                        Toast.makeText(context, "Verification email sent", Toast.LENGTH_SHORT).show()
                                                )
                                                .addOnFailureListener(e ->
                                                        Toast.makeText(context, "Verification email failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                                );

                                        Toast.makeText(context, "User registered & data saved!", Toast.LENGTH_SHORT).show();
                                        callback.onSuccess();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(context, "Data save failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        callback.onFailure(e.getMessage());
                                    });
                        }
                    } else {
                        String error = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                        Toast.makeText(context, "Registration failed: " + error, Toast.LENGTH_SHORT).show();
                        callback.onFailure(error);
                    }
                });
    }

    public void loginUser(String email, String password, AuthCallback callback) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null && user.isEmailVerified()) {
                            Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show();
                            callback.onSuccess();
                        } else {
                            Toast.makeText(context, "Please verify your email address first.", Toast.LENGTH_SHORT).show();
                            callback.onFailure("Email not verified");
                        }
                    } else {
                        String error = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                        Toast.makeText(context, "Login failed: " + error, Toast.LENGTH_SHORT).show();
                        callback.onFailure(error);
                    }
                });
    }

    public void logoutUser() {
        auth.signOut();
        Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show();
    }

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }
}
