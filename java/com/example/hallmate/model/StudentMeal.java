package com.example.hallmate.model;  // Replace with your package if needed

public class StudentMeal {

    private String studentName;
    private double studentMealCost;

    // Constructor
    public StudentMeal(String studentName, double studentMealCost) {
        this.studentName = studentName;
        this.studentMealCost = studentMealCost;
    }

    // Getter for student name
    public String getStudentName() {
        return studentName;
    }

    // Getter for student meal cost
    public double getStudentMealCost() {
        return studentMealCost;
    }

    // Optionally, setter methods if you want to modify these values later
    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public void setStudentMealCost(double studentMealCost) {
        this.studentMealCost = studentMealCost;
    }
}
