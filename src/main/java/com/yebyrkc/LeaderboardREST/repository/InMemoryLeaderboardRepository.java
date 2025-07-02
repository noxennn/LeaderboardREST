package com.yebyrkc.LeaderboardREST.repository;

import com.yebyrkc.LeaderboardREST.exception.PlayerNotFoundException;
import com.yebyrkc.LeaderboardREST.model.LeaderboardEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class InMemoryLeaderboardRepository implements LeaderboardRepository {

    private static final Logger logger = LoggerFactory.getLogger(InMemoryLeaderboardRepository.class);

    private final Map<String, LeaderboardEntry> store = new HashMap<>();


    @Override
    public double incrementScore(String playerId, double increment) {

        LeaderboardEntry entry = store.get(playerId);

        entry.setScore(entry.getScore() + increment);
        entry.setLastUpdated(Instant.now());
        store.put(entry.getPlayerId(), entry); // persist update
        return entry.getScore();
    }

    @Override
    public List<LeaderboardEntry> getTopPlayers(int n) {
        return new ArrayList<>(store.values()).stream()
                .sorted((a, b) -> Double.compare(b.getScore(), a.getScore()))
                .limit(n)
                .collect(Collectors.toList());
    }

    @Override
    public LeaderboardEntry getPlayer(String playerId) {

        return store.get(playerId);
    }

    @Override
    public long getPlayerRank(String playerId) {
        logger.debug("Retrieving rank for {} from Java service", playerId);
        LeaderboardEntry player = store.get(playerId);
        if (player == null) {
            throw new PlayerNotFoundException("Player not found: " + playerId);
        }

        return new ArrayList<>(store.values()).stream()
                .filter(e -> e.getScore() > player.getScore())
                .count() + 1;
    }

    @Override
    public void addPlayerEntry(String playerId, String username, int level, double initialScore) {
        store.put(playerId, new LeaderboardEntry(playerId, username, initialScore, level, Instant.now()));
    }

    @Override
    public void addPlayerEntries(List<LeaderboardEntry> entries) {
        Map<String, LeaderboardEntry> map = entries.stream()
                .collect(Collectors.toMap(
                        LeaderboardEntry::getPlayerId,
                        Function.identity(),
                        (existing, replacement) -> replacement // keep the new one
                ));
        store.putAll(map);
    }

    @Override
    public void deletePlayerEntry(String playerId) {
        logger.debug("Deleting player {} from Java service", playerId);
        store.remove(playerId);

    }

    @Override
    public void deleteAllPlayerEntries() {
        logger.debug("Deleting all players from Java service");
        store.clear();
    }

}
