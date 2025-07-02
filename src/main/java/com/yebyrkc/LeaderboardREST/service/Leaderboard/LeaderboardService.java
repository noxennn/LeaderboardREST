package com.yebyrkc.LeaderboardREST.service.Leaderboard;

import com.yebyrkc.LeaderboardREST.model.LeaderboardEntry;
import com.yebyrkc.LeaderboardREST.repository.LeaderboardRepository;
import com.yebyrkc.LeaderboardREST.repository.LeaderboardRepositoryRouter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeaderboardService {

    private final LeaderboardRepositoryRouter router;
    private final LeaderboardRepository defaultRepository;

    public LeaderboardService(LeaderboardRepositoryRouter router) {
        this.router = router;
        this.defaultRepository = router.resolve("java");
    }

    public double incrementScore(String playerId, double increment) {
        return defaultRepository.incrementScore(playerId, increment);
    }

    public List<LeaderboardEntry> getTopPlayers(int n) {
        return defaultRepository.getTopPlayers(n);
    }

    public LeaderboardEntry getPlayer(String playerId) {
        return defaultRepository.getPlayer(playerId);
    }

    public long getPlayerRank(String playerId) {
        return defaultRepository.getPlayerRank(playerId);
    }

    public void addPlayerEntry(String playerId, String username, int level, double initialScore) {
        defaultRepository.addPlayerEntry(playerId, username, level, initialScore);
    }

    public void addPlayerEntries(List<LeaderboardEntry> entries) {
        defaultRepository.addPlayerEntries(entries);
    }

    public void deletePlayerEntry(String playerId) {
        defaultRepository.deletePlayerEntry(playerId);
    }

    public void deleteAllPlayerEntries() {
        defaultRepository.deleteAllPlayerEntries();
    }

    public LeaderboardRepository resolve(String type) {
        return router.resolve(type);
    }

    public void addPlayerEntriesToAll(List<LeaderboardEntry> entries) {
        router.getAll().parallelStream().forEach(r -> r.addPlayerEntries(entries));
    }
}
