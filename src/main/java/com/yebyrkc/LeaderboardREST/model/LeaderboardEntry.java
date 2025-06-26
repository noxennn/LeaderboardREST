package com.yebyrkc.LeaderboardREST.model;


import java.time.Instant;

public class LeaderboardEntry {
    private String playerId;
    private String username;
    private double score;
    private int level;
    private Instant lastUpdated;

    public LeaderboardEntry() {}

    public LeaderboardEntry(String playerId, String username, double score, int level, Instant lastUpdated) {
        this.playerId = playerId;
        this.username = username;
        this.score = score;
        this.level = level;
        this.lastUpdated = lastUpdated;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Instant lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}

