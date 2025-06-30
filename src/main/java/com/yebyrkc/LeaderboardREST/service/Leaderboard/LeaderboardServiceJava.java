package com.yebyrkc.LeaderboardREST.service.Leaderboard;

import com.yebyrkc.LeaderboardREST.exception.PlayerNotFoundException;
import com.yebyrkc.LeaderboardREST.model.LeaderboardEntry;
import com.yebyrkc.LeaderboardREST.repository.LeaderboardRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
//@Primary
@Profile("java")
public class LeaderboardServiceJava implements LeaderboardService {

    private static final Logger logger = LoggerFactory.getLogger(LeaderboardServiceJava.class);

    @Autowired
    private final LeaderboardRepository leaderboardRepository;

    public LeaderboardServiceJava(LeaderboardRepository leaderboardRepository) {
        this.leaderboardRepository = leaderboardRepository;
    }

    @Override
    public double incrementScore(String playerId, double increment) {
        logger.debug("Incrementing score for {} by {} in Java service", playerId, increment);
        LeaderboardEntry entry = leaderboardRepository.findById(playerId);
        if (entry == null) {
            logger.warn("Player {} not found", playerId);
            throw new PlayerNotFoundException("Player not found: " + playerId);
        }
        entry.setScore(entry.getScore() + increment);
        entry.setLastUpdated(Instant.now());
        leaderboardRepository.save(entry); // persist update
        return entry.getScore();
    }

    @Override
    public List<LeaderboardEntry> getTopPlayers(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("n must be greater than 0");
        }
        logger.debug("Retrieving top {} players from Java service", n);
        return leaderboardRepository.findAll().stream()
                .sorted((a, b) -> Double.compare(b.getScore(), a.getScore()))
                .limit(n)
                .collect(Collectors.toList());
    }

    @Override
    public LeaderboardEntry getPlayer(String playerId) {
        logger.debug("Retrieving player {} from Java service", playerId);
        LeaderboardEntry entry = leaderboardRepository.findById(playerId);
        if (entry == null) {
            throw new PlayerNotFoundException("Player not found: " + playerId);
        }
        return entry;
    }

    @Override
    public long getPlayerRank(String playerId) {
        logger.debug("Retrieving rank for {} from Java service", playerId);
        LeaderboardEntry player = leaderboardRepository.findById(playerId);
        if (player == null) {
            throw new PlayerNotFoundException("Player not found: " + playerId);
        }

        return leaderboardRepository.findAll().stream()
                .filter(e -> e.getScore() > player.getScore())
                .count() + 1;
    }

    @Override
    public void addPlayerEntry(String playerId, String username, int level, double initialScore) {
        logger.debug("Adding player to Java service with playerId={}", playerId);
        leaderboardRepository.save(
                new LeaderboardEntry(playerId, username, initialScore, level, Instant.now())
        );
    }

    @Override
    public void addPlayerEntries(List<LeaderboardEntry> entries) {
        logger.debug("Adding {} players to Java service", entries.size());
        leaderboardRepository.saveAll(entries);
    }

    @Override
    public void deletePlayerEntry(String playerId) {
        logger.debug("Deleting player {} from Java service", playerId);
        leaderboardRepository.delete(playerId);

    }

    @Override
    public void deleteAllPlayerEntries() {
        logger.debug("Deleting all players from Java service");
        leaderboardRepository.deleteAll();
    }
}
