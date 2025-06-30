package com.yebyrkc.LeaderboardREST.repository;

import com.yebyrkc.LeaderboardREST.model.LeaderboardEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
//@Primary
@Profile("java")
public class InMemoryLeaderboardRepository implements LeaderboardRepository {

    private static final Logger logger = LoggerFactory.getLogger(InMemoryLeaderboardRepository.class);

    private final Map<String, LeaderboardEntry> store = new HashMap<>();

    @Override
    public void save(LeaderboardEntry entry) {
        logger.debug("Saving player {} in memory store", entry.getPlayerId());
        store.put(entry.getPlayerId(), entry);
    }

    @Override
    public void saveAll(List<LeaderboardEntry> entries) {
        logger.debug("Saving {} players in memory store", entries.size());
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
        logger.debug("Finding player {} in memory store", playerId);
        return store.get(playerId);
    }

    @Override
    public List<LeaderboardEntry> findAll() {
        logger.debug("Fetching all players from memory store");
        return new ArrayList<>(store.values());
    }

    @Override
    public void delete(String playerId) {
        logger.debug("Deleting player {} from memory store", playerId);
        store.remove(playerId);
    }

    @Override
    public void deleteAll() {
        logger.debug("Clearing all players from memory store");
        store.clear();
    }

}
