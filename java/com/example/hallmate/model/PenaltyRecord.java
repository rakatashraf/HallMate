package com.example.hallmate.model;


public class PenaltyRecord {
    public double penaltyAmount;
    public int penaltyDays;

    public PenaltyRecord() {
        // Needed for Firebase
    }

    public PenaltyRecord(double penaltyAmount, int penaltyDays) {
        this.penaltyAmount = penaltyAmount;
        this.penaltyDays = penaltyDays;
    }
}
