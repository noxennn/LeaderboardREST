package com.yebyrkc.LeaderboardREST.service.Leaderboard;

import com.yebyrkc.LeaderboardREST.model.LeaderboardEntry;
import com.yebyrkc.LeaderboardREST.repository.LeaderboardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Primary
public class LeaderboardServiceJava implements LeaderboardService {

    @Autowired
    private final LeaderboardRepository leaderboardRepository;

    public LeaderboardServiceJava(LeaderboardRepository leaderboardRepository) {
        this.leaderboardRepository = leaderboardRepository;
    }

    @Override
    public double incrementScore(String playerId, double increment) {
        LeaderboardEntry entry = leaderboardRepository.findById(playerId);
        if (entry == null) {
            throw new NoSuchElementException("Player not found: " + playerId);
        }
        entry.setScore(entry.getScore() + increment);
        entry.setLastUpdated(Instant.now());
        leaderboardRepository.save(entry); // persist update
        return entry.getScore();
    }

    @Override
    public List<LeaderboardEntry> getTopPlayers(int n) {
        return leaderboardRepository.findAll().stream()
                .sorted((a, b) -> Double.compare(b.getScore(), a.getScore()))
                .limit(n)
                .collect(Collectors.toList());
    }

    @Override
    public LeaderboardEntry getPlayer(String playerId) {
        return leaderboardRepository.findById(playerId);
    }

    @Override
    public long getPlayerRank(String playerId) {
        LeaderboardEntry player = leaderboardRepository.findById(playerId);
        if (player == null) return -1;

        return leaderboardRepository.findAll().stream()
                .filter(e -> e.getScore() > player.getScore())
                .count() + 1;
    }

    @Override
    public void addPlayer(String playerId, String username, int level, double initialScore) {
        leaderboardRepository.save(
                new LeaderboardEntry(playerId, username, initialScore, level, Instant.now())
        );
    }

    @Override
    public void addPlayers(List<LeaderboardEntry> entries) {

    }
}
