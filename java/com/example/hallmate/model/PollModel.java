package com.example.hallmate.model;

public class PollModel {
    private String id;
    private String title;

    public PollModel(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
}
