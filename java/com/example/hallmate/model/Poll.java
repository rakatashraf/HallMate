package com.example.hallmate.model;

import java.util.List;
import java.util.Map;

public class Poll {
    private String pollTitle;
    private List<String> options;
    private Map<String, Integer> votes;
    private String createdBy;
    private String hallName;
    private long createdAt;

    // Empty constructor needed for Firestore
    public Poll() {}

    public Poll(String pollTitle, List<String> options, Map<String, Integer> votes, String createdBy, String hallName, long createdAt) {
        this.pollTitle = pollTitle;
        this.options = options;
        this.votes = votes;
        this.createdBy = createdBy;
        this.hallName = hallName;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public String getPollTitle() { return pollTitle; }
    public void setPollTitle(String pollTitle) { this.pollTitle = pollTitle; }

    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }

    public Map<String, Integer> getVotes() { return votes; }
    public void setVotes(Map<String, Integer> votes) { this.votes = votes; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getHallName() { return hallName; }
    public void setHallName(String hallName) { this.hallName = hallName; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
