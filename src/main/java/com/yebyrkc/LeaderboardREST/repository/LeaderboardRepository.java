package com.yebyrkc.LeaderboardREST.repository;

import com.yebyrkc.LeaderboardREST.model.LeaderboardEntry;

import java.util.List;

public interface LeaderboardRepository {
    void save(LeaderboardEntry entry);
    void saveAll(List<LeaderboardEntry> entries);
    LeaderboardEntry findById(String playerId);
    List<LeaderboardEntry> findAll();
    void delete(String playerId);
    void deleteAll();
}
