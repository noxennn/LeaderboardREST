package com.yebyrkc.LeaderboardREST.service.Leaderboard;

import com.yebyrkc.LeaderboardREST.model.LeaderboardEntry;

import java.util.List;

public interface LeaderboardService {

    double incrementScore(String playerId, double increment);

    List<LeaderboardEntry> getTopPlayers(int n);

    LeaderboardEntry getPlayer(String playerId);

    long getPlayerRank(String playerId);

    void addPlayer(String playerId, String username, int level, double initialScore);

    void addPlayers(List<LeaderboardEntry> entries);

    void deletePlayer(String playerId);

    void deleteAllPlayers();
}
