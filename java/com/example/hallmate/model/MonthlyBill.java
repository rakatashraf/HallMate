package com.example.hallmate.model;

public class MonthlyBill {
    public String startDate;
    public String endDate;
    public String paidDate;
    public String paymentStatus;
    public double staffMealCost;
    public double studentMealCost;
    public double total;
    public double utilityBill;
    public double penaltyAmount;
    public int penaltyDays;

    public MonthlyBill() {
        // Required empty constructor for Firebase
    }

    public MonthlyBill(String startDate, String endDate, String paidDate, String paymentStatus,
                       double staffMealCost, double studentMealCost, double total, double utilityBill,
                       double penaltyAmount, int penaltyDays) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.paidDate = paidDate;
        this.paymentStatus = paymentStatus;
        this.staffMealCost = staffMealCost;
        this.studentMealCost = studentMealCost;
        this.total = total;
        this.utilityBill = utilityBill;
        this.penaltyAmount = penaltyAmount;
        this.penaltyDays = penaltyDays;
    }
}