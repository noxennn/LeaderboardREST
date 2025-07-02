package com.yebyrkc.LeaderboardREST.repository;

import com.yebyrkc.LeaderboardREST.model.LeaderboardEntry;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

import java.util.List;

/**
 * Decorates a {@link LeaderboardRepository} with Micrometer metrics.
 */
public class InstrumentedLeaderboardRepository implements LeaderboardRepository {

    private final LeaderboardRepository delegate;
    private final MeterRegistry meterRegistry;
    private final Timer incrementScoreTimer;
    private final Timer getTopPlayersTimer;
    private final Timer getPlayerTimer;
    private final Timer getPlayerRankTimer;
    private final Timer addPlayerEntryTimer;
    private final Timer addPlayerEntriesTimer;
    private final Timer deletePlayerEntryTimer;
    private final Timer deleteAllPlayerEntriesTimer;
    private final Counter playerHitCounter;
    private final Counter playerMissCounter;

    public InstrumentedLeaderboardRepository(LeaderboardRepository delegate, MeterRegistry registry, String backendType) {
        this.delegate = delegate;
        this.meterRegistry = registry;
        this.incrementScoreTimer = registry.timer("leaderboard.incrementScore", "backend", backendType);
        this.getTopPlayersTimer = registry.timer("leaderboard.getTopPlayers", "backend", backendType);
        this.getPlayerTimer = registry.timer("leaderboard.getPlayer", "backend", backendType);
        this.getPlayerRankTimer = registry.timer("leaderboard.getPlayerRank", "backend", backendType);
        this.addPlayerEntryTimer = registry.timer("leaderboard.addPlayerEntry", "backend", backendType);
        this.addPlayerEntriesTimer = registry.timer("leaderboard.addPlayerEntries", "backend", backendType);
        this.deletePlayerEntryTimer = registry.timer("leaderboard.deletePlayerEntry", "backend", backendType);
        this.deleteAllPlayerEntriesTimer = registry.timer("leaderboard.deleteAllPlayerEntries", "backend", backendType);
        this.playerHitCounter = registry.counter("leaderboard.getPlayer.hit", "backend", backendType);
        this.playerMissCounter = registry.counter("leaderboard.getPlayer.miss", "backend", backendType);
    }

    @Override
    public double incrementScore(String playerId, double increment) {
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            return delegate.incrementScore(playerId, increment);
        } finally {
            sample.stop(incrementScoreTimer);
        }
    }

    @Override
    public List<LeaderboardEntry> getTopPlayers(int n) {
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            return delegate.getTopPlayers(n);
        } finally {
            sample.stop(getTopPlayersTimer);
        }
    }

    @Override
    public LeaderboardEntry getPlayer(String playerId) {
        Timer.Sample sample = Timer.start(meterRegistry);
        LeaderboardEntry entry = null;
        try {
            entry = delegate.getPlayer(playerId);
            return entry;
        } finally {
            sample.stop(getPlayerTimer);
            if (entry != null) {
                playerHitCounter.increment();
            } else {
                playerMissCounter.increment();
            }
        }
    }

    @Override
    public long getPlayerRank(String playerId) {
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            return delegate.getPlayerRank(playerId);
        } finally {
            sample.stop(getPlayerRankTimer);
        }
    }

    @Override
    public void addPlayerEntry(String playerId, String username, int level, double initialScore) {
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            delegate.addPlayerEntry(playerId, username, level, initialScore);
        } finally {
            sample.stop(addPlayerEntryTimer);
        }
    }

    @Override
    public void addPlayerEntries(List<LeaderboardEntry> entries) {
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            delegate.addPlayerEntries(entries);
        } finally {
            sample.stop(addPlayerEntriesTimer);
        }
    }

    @Override
    public void deletePlayerEntry(String playerId) {
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            delegate.deletePlayerEntry(playerId);
        } finally {
            sample.stop(deletePlayerEntryTimer);
        }
    }

    @Override
    public void deleteAllPlayerEntries() {
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            delegate.deleteAllPlayerEntries();
        } finally {
            sample.stop(deleteAllPlayerEntriesTimer);
        }
    }
}
