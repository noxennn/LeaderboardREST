package com.yebyrkc.LeaderboardREST.service;

import com.yebyrkc.LeaderboardREST.model.LeaderboardEntry;
import com.yebyrkc.LeaderboardREST.repository.ILeaderboardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeaderboardService {

    @Autowired
    private final ILeaderboardRepository repository;

    public LeaderboardService(ILeaderboardRepository repository) {
        this.repository = repository;
    }

    public double incrementScore(String playerId, double increment) {
        return repository.incrementScore(playerId, increment);
    }

    public List<LeaderboardEntry> getTopPlayers(int n) {
        return repository.getTopPlayers(n);
    }

    public LeaderboardEntry getPlayer(String playerId) {
        return repository.getPlayer(playerId);
    }

    public long getPlayerRank(String playerId) {
        return repository.getPlayerRank(playerId);
    }

    public void addPlayerEntry(String playerId, String username, int level, double initialScore) {
        repository.addPlayerEntry(playerId, username, level, initialScore);
    }

    public void addPlayerEntries(List<LeaderboardEntry> entries) {
        repository.addPlayerEntries(entries);
    }

    public void deletePlayerEntry(String playerId) {
        repository.deletePlayerEntry(playerId);
    }

    public void deleteAllPlayerEntries() {
        repository.deleteAllPlayerEntries();
    }
}
