package com.example.hallmate.model;

public class StudentModel {
    private String name;
    private String studentId;
    private String roomNumber;

    public StudentModel() {
        // Empty constructor required for Firebase
    }

    public StudentModel(String name, String studentId, String roomNumber) {
        this.name = name;
        this.studentId = studentId;
        this.roomNumber = roomNumber;
    }

    public String getName() {
        return name;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }
}
