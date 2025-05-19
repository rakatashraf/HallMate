package com.example.hallmate.model;

public class DailyMealCostModel {

    private String date;
    private String studentMealCost;
    private String staffMealCost;
    private String utilityBill;

    // Constructor
    public DailyMealCostModel(String date, String studentMealCost, String staffMealCost, String utilityBill) {
        this.date = date;
        this.studentMealCost = studentMealCost;
        this.staffMealCost = staffMealCost;
        this.utilityBill = utilityBill;
    }

    // Getters
    public String getDate() {
        return date;
    }

    public String getStudentMealCost() {
        return studentMealCost;
    }

    public String getStaffMealCost() {
        return staffMealCost;
    }

    public String getUtilityBill() {
        return utilityBill;
    }

    // Setters
    public void setDate(String date) {
        this.date = date;
    }

    public void setStudentMealCost(String studentMealCost) {
        this.studentMealCost = studentMealCost;
    }

    public void setStaffMealCost(String staffMealCost) {
        this.staffMealCost = staffMealCost;
    }

    public void setUtilityBill(String utilityBill) {
        this.utilityBill = utilityBill;
    }
}
