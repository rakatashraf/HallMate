package com.example.hallmate.model;

public class MealStudent {
    private String userId, name, studentId, roomNumber;
    private boolean breakfast, lunch, dinner, mealstatus;

    public MealStudent() {} // Needed for Firestore

    public MealStudent(String userId, String name, String roomNumber, String studentId,
                       boolean breakfast, boolean lunch, boolean dinner, boolean mealstatus) {
        this.userId = userId;
        this.name = name;
        this.studentId = studentId;
        this.roomNumber = roomNumber;
        this.breakfast = breakfast;
        this.lunch = lunch;
        this.dinner = dinner;
        this.mealstatus = mealstatus;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getName() { return name; }
    public String getRoomNumber() { return roomNumber; }

    public String getStudentId() { return studentId; }

    public boolean isBreakfast() { return breakfast; }
    public boolean isLunch() { return lunch; }
    public boolean isDinner() { return dinner; }
    public boolean isMealstatus() { return mealstatus; }

    public void setName(String name) { this.name = name; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public void setBreakfast(boolean breakfast) { this.breakfast = breakfast; }
    public void setLunch(boolean lunch) { this.lunch = lunch; }
    public void setDinner(boolean dinner) { this.dinner = dinner; }
    public void setMealstatus(boolean mealstatus) { this.mealstatus = mealstatus; }
}
