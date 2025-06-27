package com.yebyrkc.LeaderboardREST.repository;

import com.yebyrkc.LeaderboardREST.model.LeaderboardEntry;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
//@Primary
public class InMemoryLeaderboardRepository implements LeaderboardRepository {

    private final Map<String, LeaderboardEntry> store = new HashMap<>();

    @Override
    public void save(LeaderboardEntry entry) {
        store.put(entry.getPlayerId(), entry);
    }

    @Override
    public void saveAll(List<LeaderboardEntry> entries) {
        Map<String, LeaderboardEntry> map = entries.stream()
                .collect(Collectors.toMap(
                        LeaderboardEntry::getPlayerId,
                        Function.identity(),
                        (existing, replacement) -> replacement // keep the new one
                ));
        store.putAll(map);
    }

    @Override
    public LeaderboardEntry findById(String playerId) {
        return store.get(playerId);
    }

    @Override
    public List<LeaderboardEntry> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void delete(String playerId) {
        store.remove(playerId);
    }

    @Override
    public void deleteAll() {
        store.clear();
    }

}
