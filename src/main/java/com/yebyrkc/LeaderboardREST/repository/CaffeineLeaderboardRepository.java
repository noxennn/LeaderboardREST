package com.yebyrkc.LeaderboardREST.repository;

import com.yebyrkc.LeaderboardREST.exception.PlayerNotFoundException;
import com.yebyrkc.LeaderboardREST.model.LeaderboardEntry;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@Profile("caffeine")
public class CaffeineLeaderboardRepository implements LeaderboardRepository {

    private static final Logger logger = LoggerFactory.getLogger(CaffeineLeaderboardRepository.class);

    private final Cache<String, LeaderboardEntry> cache;

    public CaffeineLeaderboardRepository() {
        // Customize cache as you want (e.g. TTL, max size):
        this.cache = Caffeine.newBuilder()
                .maximumSize(2_000_000)                 // up to 10k players
                .expireAfterAccess(60, TimeUnit.MINUTES) // evict if not accessed in 60 mins
                .build();
    }


    @Override
    public double incrementScore(String playerId, double increment) {

        LeaderboardEntry entry = cache.getIfPresent(playerId);
        entry.setScore(entry.getScore() + increment);
        entry.setLastUpdated(Instant.now());
        cache.put(entry.getPlayerId(), entry);

        return entry.getScore();
    }


    @Override
    public List<LeaderboardEntry> getTopPlayers(int n) {

        return new ArrayList<>(cache.asMap().values()).stream()
                .sorted((a, b) -> Double.compare(b.getScore(), a.getScore()))
                .limit(n)
                .collect(Collectors.toList());
    }

    @Override
    public LeaderboardEntry getPlayer(String playerId) {
        return cache.getIfPresent(playerId);
    }

    @Override
    public long getPlayerRank(String playerId) {
        LeaderboardEntry player = cache.getIfPresent(playerId);
        return new ArrayList<>(cache.asMap().values()).stream()
                .filter(e -> e.getScore() > player.getScore())
                .count() + 1;
    }

    @Override
    public void addPlayerEntry(String playerId, String username, int level, double initialScore) {
        cache.put(playerId, new LeaderboardEntry(playerId, username, initialScore, level, Instant.now()));
    }

    @Override
    public void addPlayerEntries(List<LeaderboardEntry> entries) {

        Map<String, LeaderboardEntry> map = entries.stream()
                .collect(Collectors.toMap(
                        LeaderboardEntry::getPlayerId,
                        Function.identity(),
                        (existing, replacement) -> replacement // keep the new one
                ));
        cache.putAll(map);
    }

    @Override
    public void deletePlayerEntry(String playerId) {
        cache.invalidate(playerId);
    }

    @Override
    public void deleteAllPlayerEntries() {
        cache.invalidateAll();
    }
}
