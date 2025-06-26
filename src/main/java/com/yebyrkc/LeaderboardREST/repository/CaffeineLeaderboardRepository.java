package com.yebyrkc.LeaderboardREST.repository;

import com.yebyrkc.LeaderboardREST.model.LeaderboardEntry;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Repository
//@Primary
public class CaffeineLeaderboardRepository implements LeaderboardRepository {

    private final Cache<String, LeaderboardEntry> cache;

    public CaffeineLeaderboardRepository() {
        // Customize cache as you want (e.g. TTL, max size):
        this.cache = Caffeine.newBuilder()
                .maximumSize(100_000)                 // up to 10k players
                .expireAfterAccess(60, TimeUnit.MINUTES) // evict if not accessed in 60 mins
                .build();
    }

    @Override
    public void save(LeaderboardEntry entry) {
        cache.put(entry.getPlayerId(), entry);
    }

    @Override
    public LeaderboardEntry findById(String playerId) {
        return cache.getIfPresent(playerId);
    }

    @Override
    public List<LeaderboardEntry> findAll() {
        return new ArrayList<>(cache.asMap().values());
    }
}
