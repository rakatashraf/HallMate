package com.example.hallmate.model;

import com.google.firebase.Timestamp;

public class ComplaintModel {
    private String id;
    private String title;
    private String description;
    private String name;
    private Timestamp timestamp;  // Firebase timestamp
    private String userId;
    private boolean solved;

    public ComplaintModel() {
        // Required for Firestore
    }

    public ComplaintModel(String id, String title, String description, String name, String userId, Timestamp timestamp, boolean solved) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.name = name;
        this.userId = userId;
        this.timestamp = timestamp;
        this.solved = solved;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public boolean isSolved() {
        return solved;
    }

    public void setSolved(boolean solved) {
        this.solved = solved;
    }
}
