package com.example.hallmate.adapter;

public class MealCostItem {

    private String title;
    private String cost;

    // Constructor
    public MealCostItem(String title, String cost) {
        this.title = title;
        this.cost = cost;
    }

    // Getter for title
    public String getTitle() {
        return title;
    }

    // Getter for cost
    public String getCost() {
        return cost;
    }

    // Optional: You can also add setters if you need them later
    public void setTitle(String title) {
        this.title = title;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }
}
