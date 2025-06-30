package com.yebyrkc.LeaderboardREST.repository;

import com.yebyrkc.LeaderboardREST.model.LeaderboardEntry;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@Primary
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
    public void save(LeaderboardEntry entry) {
        logger.debug("CaffeineRepository.save - playerId={}", entry.getPlayerId());
        cache.put(entry.getPlayerId(), entry);
    }

    @Override
    public void saveAll(List<LeaderboardEntry> entries) {
        logger.debug("CaffeineRepository.saveAll - count={}", entries.size());
        Map<String, LeaderboardEntry> map = entries.stream()
                .collect(Collectors.toMap(
                        LeaderboardEntry::getPlayerId,
                        Function.identity(),
                        (existing, replacement) -> replacement // keep the new one
                ));
        cache.putAll(map);
    }

    @Override
    public LeaderboardEntry findById(String playerId) {
        logger.debug("CaffeineRepository.findById - playerId={}", playerId);
        return cache.getIfPresent(playerId);
    }

    @Override
    public List<LeaderboardEntry> findAll() {
        logger.debug("CaffeineRepository.findAll");
        return new ArrayList<>(cache.asMap().values());
    }

    @Override
    public void delete(String playerId) {
        logger.debug("CaffeineRepository.delete - playerId={}", playerId);
        cache.invalidate(playerId);
    }

    @Override
    public void deleteAll() {
        logger.debug("CaffeineRepository.deleteAll");
        cache.invalidateAll();
    }
}
