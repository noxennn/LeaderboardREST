package com.yebyrkc.LeaderboardREST.dto;

public class IncrementRequestDTO {
    String playerId;
    double incrementBy;

    public double getIncrementBy() {
        return incrementBy;
    }

    public void setIncrementBy(int incrementBy) {
        this.incrementBy = incrementBy;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }
}
