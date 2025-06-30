package com.yebyrkc.LeaderboardREST.service.PlayerGenerator;

public interface PlayerGeneratorService {
    /**
     * Generates a specified number of players with random data
     * and inserts them into the leaderboard.
     */
    void generatePlayerEntries(int count);
}

