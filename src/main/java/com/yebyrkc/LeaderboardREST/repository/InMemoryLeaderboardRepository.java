package com.yebyrkc.LeaderboardREST.repository;

import com.yebyrkc.LeaderboardREST.model.LeaderboardEntry;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@Primary
public class InMemoryLeaderboardRepository implements LeaderboardRepository {

    private final Map<String, LeaderboardEntry> store = new HashMap<>();

    @Override
    public void save(LeaderboardEntry entry) {
        store.put(entry.getPlayerId(), entry);
    }

    @Override
    public LeaderboardEntry findById(String playerId) {
        return store.get(playerId);
    }

    @Override
    public List<LeaderboardEntry> findAll() {
        return new ArrayList<>(store.values());
    }
}
