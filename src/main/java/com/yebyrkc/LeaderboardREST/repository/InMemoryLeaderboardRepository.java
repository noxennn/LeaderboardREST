package com.yebyrkc.LeaderboardREST.repository;

import com.yebyrkc.LeaderboardREST.model.LeaderboardEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
//@Primary
public class InMemoryLeaderboardRepository implements LeaderboardRepository {

    private static final Logger logger = LoggerFactory.getLogger(InMemoryLeaderboardRepository.class);

    private final Map<String, LeaderboardEntry> store = new HashMap<>();

    @Override
    public void save(LeaderboardEntry entry) {
        logger.debug("InMemoryRepository.save - playerId={}", entry.getPlayerId());
        store.put(entry.getPlayerId(), entry);
    }

    @Override
    public void saveAll(List<LeaderboardEntry> entries) {
        logger.debug("InMemoryRepository.saveAll - count={}", entries.size());
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
        logger.debug("InMemoryRepository.findById - playerId={}", playerId);
        return store.get(playerId);
    }

    @Override
    public List<LeaderboardEntry> findAll() {
        logger.debug("InMemoryRepository.findAll");
        return new ArrayList<>(store.values());
    }

    @Override
    public void delete(String playerId) {
        logger.debug("InMemoryRepository.delete - playerId={}", playerId);
        store.remove(playerId);
    }

    @Override
    public void deleteAll() {
        logger.debug("InMemoryRepository.deleteAll");
        store.clear();
    }

}
