package com.yebyrkc.LeaderboardREST.repository;

import com.yebyrkc.LeaderboardREST.model.LeaderboardEntry;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;

import java.time.Duration;
import java.util.List;

public class InstrumentedLeaderboardRepository implements LeaderboardRepository {

    private final LeaderboardRepository repository;
    private final MeterRegistry meterRegistry;
    private final String typeTag;

    public InstrumentedLeaderboardRepository(LeaderboardRepository repository, MeterRegistry meterRegistry, String typeTag) {
        this.repository = repository;
        this.meterRegistry = meterRegistry;
        this.typeTag = typeTag;
    }

    private Timer timer(String name) {
        return Timer.builder(name)
                .tag("type", typeTag)
                .publishPercentiles(0.5, 0.95, 0.99) // still export these percentiles

                .serviceLevelObjectives(
                        Duration.ofNanos(1_000),
                        Duration.ofNanos(10_000),
                        Duration.ofNanos(25_000),
                        Duration.ofNanos(50_000),
                        Duration.ofNanos(100_000),
                        Duration.ofNanos(200_000),  // 0.0002 s
                        Duration.ofNanos(500_000),  // 0.0005 s
                        Duration.ofMillis(1),       // 0.001 s
                        Duration.ofMillis(2),       // 0.002 s
                        Duration.ofMillis(5),       // 0.005 s
                        Duration.ofMillis(10),      // 0.010 s
                        Duration.ofMillis(20),       // 0.020 s
                        Duration.ofMillis(50),       // 0.020 s
                        Duration.ofMillis(100),       // 0.020 s
                        Duration.ofMillis(200)       // 0.020 s
                )
                // optional: if you want to set expected ranges
                .register(meterRegistry);
    }

    @Override
    public double incrementScore(String playerId, double increment) {
        return timer("leaderboard.increment.score")
                .record(() -> repository.incrementScore(playerId, increment));
    }

    @Override
    public List<LeaderboardEntry> getTopPlayers(int n) {
        return timer("leaderboard.get.top.players")
                .record(() -> repository.getTopPlayers(n));
    }

    @Override
    public LeaderboardEntry getPlayer(String playerId) {
        return timer("leaderboard.get.player")
                .record(() -> repository.getPlayer(playerId));
    }

    @Override
    public long getPlayerRank(String playerId) {
        return timer("leaderboard.get.rank")
                .record(() -> repository.getPlayerRank(playerId));
    }

    @Override
    public void addPlayerEntry(String playerId, String username, int level, double initialScore) {
        timer("leaderboard.add.player")
                .record(() -> repository.addPlayerEntry(playerId, username, level, initialScore));
    }

    @Override
    public void addPlayerEntries(List<LeaderboardEntry> entries) {
        timer("leaderboard.add.players.bulk")
                .record(() -> repository.addPlayerEntries(entries));
    }

    @Override
    public void deletePlayerEntry(String playerId) {
        timer("leaderboard.delete.player")
                .record(() -> repository.deletePlayerEntry(playerId));
    }

    @Override
    public void deleteAllPlayerEntries() {
        timer("leaderboard.delete.all.players")
                .record(repository::deleteAllPlayerEntries);
    }
}
