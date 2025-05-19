package com.example.hallmate.model;

public class UserModel {
    private String name, studentId, phone, email, hallName, roomNumber, batch, department, session, role;

    public UserModel() {
        // Default constructor required for Firebase
    }

    public UserModel(String name, String studentId, String phone, String email,
                     String hallName, String roomNumber, String batch,
                     String department, String session, String role) {
        this.name = name;
        this.studentId = studentId;
        this.phone = phone;
        this.email = email;
        this.hallName = hallName;
        this.roomNumber = roomNumber;
        this.batch = batch;
        this.department = department;
        this.session = session;
        this.role = role;
    }

    // Getters and setters (optional if needed)
    public String getName() { return name; }
    public String getStudentId() { return studentId; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getHallName() { return hallName; }
    public String getRoomNumber() { return roomNumber; }
    public String getBatch() { return batch; }
    public String getDepartment() { return department; }
    public String getSession() { return session; }
    public String getRole() { return role; }

    public void setName(String name) { this.name = name; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setEmail(String email) { this.email = email; }
    public void setHallName(String hallName) { this.hallName = hallName; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    public void setBatch(String batch) { this.batch = batch; }
    public void setDepartment(String department) { this.department = department; }
    public void setSession(String session) { this.session = session; }
    public void setRole(String role) { this.role = role; }
}
