package com.yebyrkc.LeaderboardREST.service.Leaderboard;

import com.yebyrkc.LeaderboardREST.exception.PlayerNotFoundException;
import com.yebyrkc.LeaderboardREST.model.LeaderboardEntry;
import com.yebyrkc.LeaderboardREST.repository.LeaderboardRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
//@Primary
public class LeaderboardServiceJava implements LeaderboardService {

    private static final Logger logger = LoggerFactory.getLogger(LeaderboardServiceJava.class);

    @Autowired
    private final LeaderboardRepository leaderboardRepository;

    public LeaderboardServiceJava(LeaderboardRepository leaderboardRepository) {
        this.leaderboardRepository = leaderboardRepository;
    }

    @Override
    public double incrementScore(String playerId, double increment) {
        logger.debug("JavaService.incrementScore - playerId={}, increment={}", playerId, increment);
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
        logger.debug("JavaService.getTopPlayers - count={}", n);
        return leaderboardRepository.findAll().stream()
                .sorted((a, b) -> Double.compare(b.getScore(), a.getScore()))
                .limit(n)
                .collect(Collectors.toList());
    }

    @Override
    public LeaderboardEntry getPlayer(String playerId) {
        logger.debug("JavaService.getPlayer - playerId={}", playerId);
        LeaderboardEntry entry = leaderboardRepository.findById(playerId);
        if (entry == null) {
            throw new PlayerNotFoundException("Player not found: " + playerId);
        }
        return entry;
    }

    @Override
    public long getPlayerRank(String playerId) {
        logger.debug("JavaService.getPlayerRank - playerId={}", playerId);
        LeaderboardEntry player = leaderboardRepository.findById(playerId);
        if (player == null) {
            throw new PlayerNotFoundException("Player not found: " + playerId);
        }

        return leaderboardRepository.findAll().stream()
                .filter(e -> e.getScore() > player.getScore())
                .count() + 1;
    }

    @Override
    public void addPlayer(String playerId, String username, int level, double initialScore) {
        logger.debug("JavaService.addPlayer - playerId={}", playerId);
        leaderboardRepository.save(
                new LeaderboardEntry(playerId, username, initialScore, level, Instant.now())
        );
    }

    @Override
    public void addPlayers(List<LeaderboardEntry> entries) {
        logger.debug("JavaService.addPlayers - count={}", entries.size());
        leaderboardRepository.saveAll(entries);
    }

    @Override
    public void deletePlayer(String playerId) {
        logger.debug("JavaService.deletePlayer - playerId={}", playerId);
        leaderboardRepository.delete(playerId);

    }

    @Override
    public void deleteAllPlayers() {
        logger.debug("JavaService.deleteAllPlayers");
        leaderboardRepository.deleteAll();
    }
}
