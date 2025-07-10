package com.yebyrkc.LeaderboardREST.repository;

import com.yebyrkc.LeaderboardREST.model.LeaderboardEntry;

import java.util.List;

public interface ILeaderboardRepository {

    double incrementScore(String playerId, double increment);

    List<LeaderboardEntry> getTopPlayers(int n);

    LeaderboardEntry getPlayer(String playerId);

    long getPlayerRank(String playerId);

    void addPlayerEntry(String playerId, String username, int level, double initialScore);

    void addPlayerEntries(List<LeaderboardEntry> entries);

    void deletePlayerEntry(String playerId);

    void deleteAllPlayerEntries();
}
