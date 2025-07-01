package com.yebyrkc.LeaderboardREST.service.Leaderboard;

import com.yebyrkc.LeaderboardREST.model.LeaderboardEntry;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

import java.util.List;
import java.util.Objects;

/**
 * LeaderboardService decorator that instruments method calls with Micrometer metrics.
 */
public class InstrumentedLeaderboardService implements LeaderboardService {

    private final LeaderboardService delegate;
    private final MeterRegistry meterRegistry;
    private final String cacheType;

    private final Counter playerHitCounter;
    private final Counter playerMissCounter;

    public InstrumentedLeaderboardService(LeaderboardService delegate, MeterRegistry meterRegistry, String cacheType) {
        this.delegate = Objects.requireNonNull(delegate, "delegate");
        this.meterRegistry = Objects.requireNonNull(meterRegistry, "meterRegistry");
        this.cacheType = Objects.requireNonNull(cacheType, "cacheType");

        this.playerHitCounter = Counter.builder("leaderboard.get.player.hit")
                .tag("type", cacheType)
                .register(meterRegistry);
        this.playerMissCounter = Counter.builder("leaderboard.get.player.miss")
                .tag("type", cacheType)
                .register(meterRegistry);
    }

    private Timer.Builder timerBuilder(String name) {
        return Timer.builder(name)
                .tag("type", cacheType)
                .publishPercentileHistogram()
                .publishPercentiles(0.5, 0.95, 0.99);
    }

    @Override
    public double incrementScore(String playerId, double increment) {
        return timerBuilder("leaderboard.increment.score")
                .register(meterRegistry)
                .record(() -> delegate.incrementScore(playerId, increment));
    }

    @Override
    public List<LeaderboardEntry> getTopPlayers(int n) {
        return timerBuilder("leaderboard.get.top.players")
                .register(meterRegistry)
                .record(() -> delegate.getTopPlayers(n));
    }

    @Override
    public LeaderboardEntry getPlayer(String playerId) {
        LeaderboardEntry entry = timerBuilder("leaderboard.get.player")
                .register(meterRegistry)
                .record(() -> delegate.getPlayer(playerId));
        if (entry != null) {
            playerHitCounter.increment();
        } else {
            playerMissCounter.increment();
        }
        return entry;
    }

    @Override
    public long getPlayerRank(String playerId) {
        return timerBuilder("leaderboard.get.rank")
                .register(meterRegistry)
                .record(() -> delegate.getPlayerRank(playerId));
    }

    @Override
    public void addPlayerEntry(String playerId, String username, int level, double initialScore) {
        timerBuilder("leaderboard.add.player")
                .register(meterRegistry)
                .record(() -> delegate.addPlayerEntry(playerId, username, level, initialScore));
    }

    @Override
    public void addPlayerEntries(List<LeaderboardEntry> entries) {
        timerBuilder("leaderboard.add.players.bulk")
                .register(meterRegistry)
                .record(() -> delegate.addPlayerEntries(entries));
    }

    @Override
    public void deletePlayerEntry(String playerId) {
        timerBuilder("leaderboard.delete.player")
                .register(meterRegistry)
                .record(() -> delegate.deletePlayerEntry(playerId));
    }

    @Override
    public void deleteAllPlayerEntries() {
        timerBuilder("leaderboard.delete.all.players")
                .register(meterRegistry)
                .record(delegate::deleteAllPlayerEntries);
    }
}

