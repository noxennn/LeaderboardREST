package com.yebyrkc.LeaderboardREST.service.Leaderboard;


import com.yebyrkc.LeaderboardREST.model.LeaderboardEntry;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.RedisCallback;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

@Service
//@Primary
public class LeaderboardServiceRedis implements LeaderboardService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String LEADERBOARD_KEY = "leaderboard";
    private static final String PLAYER_HASH_PREFIX = "player:";

    public LeaderboardServiceRedis(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public double incrementScore(String playerId, double increment) {
        // Increment in sorted set
        Double newScore = redisTemplate.opsForZSet().incrementScore(LEADERBOARD_KEY, playerId, increment);
        // Update timestamp in hash
        redisTemplate.opsForHash().put(PLAYER_HASH_PREFIX + playerId, "lastUpdated", String.valueOf(Instant.now().getEpochSecond()));
        return newScore != null ? newScore : 0;
    }

//    @Override
//    public List<LeaderboardEntry> getTopPlayers(int n) {
//        Set<ZSetOperations.TypedTuple<String>> top = redisTemplate.opsForZSet()
//                .reverseRangeWithScores(LEADERBOARD_KEY, 0, n - 1);
//
//        List<LeaderboardEntry> result = new ArrayList<>();
//        if (top != null) {
//            for (ZSetOperations.TypedTuple<String> entry : top) {
//                String playerId = entry.getValue();
//                double score = entry.getScore() != null ? entry.getScore() : 0;
//                result.add(getPlayerWithScore(playerId, score));
//            }
//        }
//        return result;
//    }
@Override
public List<LeaderboardEntry> getTopPlayers(int n) {
    Set<ZSetOperations.TypedTuple<String>> top = redisTemplate.opsForZSet()
            .reverseRangeWithScores(LEADERBOARD_KEY, 0, n - 1);

    List<LeaderboardEntry> result = new ArrayList<>();
    if (top != null) {
        // Using Redis Pipelining to fetch player hash data
        List<String> playerIds = top.stream().map(ZSetOperations.TypedTuple::getValue).toList();
        List<Double> scores = top.stream().map(ZSetOperations.TypedTuple::getScore).toList();

        List<Object> hashResults = redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            playerIds.forEach(id -> {
                byte[] key = ( id).getBytes(StandardCharsets.UTF_8);
                connection.hashCommands().hGet(key, "username".getBytes());
                connection.hashCommands().hGet(key, "level".getBytes());
                connection.hashCommands().hGet(key, "lastUpdated".getBytes());
            });
            return null;
        });

        // hashResults will contain all hash data sequentially in order
        for (int i = 0; i < playerIds.size(); i++) {
            String playerId = playerIds.get(i);
            double score = scores.get(i) != null ? scores.get(i) : 0.0;

            // Every player has 3 hash lookups in sequence
            int base = i * 3;
            String username = (String) hashResults.get(base);
            String levelStr = (String) hashResults.get(base + 1);
            String lastUpdatedStr = (String) hashResults.get(base + 2);

            int level = levelStr != null ? Integer.parseInt(levelStr) : 0;
            Instant lastUpdated = lastUpdatedStr != null ? Instant.ofEpochSecond(Long.parseLong(lastUpdatedStr)) : Instant.now();

            result.add(new LeaderboardEntry(playerId, username, score, level, lastUpdated));
        }
    }
    return result;
}

    @Override
    public LeaderboardEntry getPlayer(String playerId) {
        Double score = redisTemplate.opsForZSet().score(LEADERBOARD_KEY, playerId);
        return getPlayerWithScore(playerId, score != null ? score : 0);
    }

    @Override
    public long getPlayerRank(String playerId) {
        Long rank = redisTemplate.opsForZSet().reverseRank(LEADERBOARD_KEY, playerId);
        return (rank != null ? rank + 1 : -1); // return 1-based rank
    }

    /**
     * Helper method to get all player data from the hash and build a LeaderboardEntry object.
     */
    private LeaderboardEntry getPlayerWithScore(String playerId, double score) {
        var hashOps = redisTemplate.opsForHash();
        var username = (String) hashOps.get(PLAYER_HASH_PREFIX + playerId, "username");
        var levelStr = (String) hashOps.get(PLAYER_HASH_PREFIX + playerId, "level");
        var lastUpdatedStr = (String) hashOps.get(PLAYER_HASH_PREFIX + playerId, "lastUpdated");

        int level = levelStr != null ? Integer.parseInt(levelStr) : 0;
        Instant lastUpdated = lastUpdatedStr != null ? Instant.ofEpochSecond(Long.parseLong(lastUpdatedStr)) : Instant.now();

        return new LeaderboardEntry(playerId, username, score, level, lastUpdated);
    }
//    @Override
//    public void addPlayer(String playerId, String username, int level, double initialScore) {
//        // add to sorted set
//        redisTemplate.opsForZSet().add(LEADERBOARD_KEY, playerId, initialScore);
//        // add hash data
////        var hashOps = redisTemplate.opsForHash();
//        redisTemplate.opsForHash().put(playerId, "username", username);
//        redisTemplate.opsForHash().put( playerId, "level", String.valueOf(level));
//        redisTemplate.opsForHash().put( playerId, "lastUpdated", String.valueOf(Instant.now().getEpochSecond()));
//    }
@Override
public void addPlayer(String playerId, String username, int level, double initialScore) {
    // Add to sorted set
    redisTemplate.opsForZSet().add(LEADERBOARD_KEY, playerId, initialScore);

    // Create a map with multiple fields to set in the hash
    Map<String, String> playerData = new HashMap<>();
    playerData.put("username", username);
    playerData.put("level", String.valueOf(level));
    playerData.put("lastUpdated", String.valueOf(Instant.now().getEpochSecond()));

    // Add all fields to the hash in one go
    redisTemplate.opsForHash().putAll(playerId, playerData);
}
//    @Override
//    public void addPlayer(String playerId, String username, int level, double initialScore) {
//        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
//            byte[] leaderboardKey = LEADERBOARD_KEY.getBytes(StandardCharsets.UTF_8);
//            byte[] playerHashKey = ("player:" + playerId).getBytes(StandardCharsets.UTF_8);
//
//            // ZADD player to the sorted set
//            connection.zAdd(leaderboardKey, initialScore, playerId.getBytes(StandardCharsets.UTF_8));
//
//            // HSET hash fields
//            connection.hashCommands().hSet(playerHashKey, "username".getBytes(StandardCharsets.UTF_8), username.getBytes(StandardCharsets.UTF_8));
//            connection.hashCommands().hSet(playerHashKey, "level".getBytes(StandardCharsets.UTF_8), String.valueOf(level).getBytes(StandardCharsets.UTF_8));
//            connection.hashCommands().hSet(playerHashKey, "lastUpdated".getBytes(StandardCharsets.UTF_8), String.valueOf(Instant.now().getEpochSecond()).getBytes(StandardCharsets.UTF_8));
//            return null;
//        });
//    }
//    @Override
//
//    public void addPlayers(List<LeaderboardEntry> entries) {
//        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
//            for (LeaderboardEntry e : entries) {
//                String playerKey = e.getPlayerId();
//
//                // Add score to sorted set
//                redisTemplate.opsForZSet().add(LEADERBOARD_KEY, playerKey, e.getScore());
//
//                // Write all hash fields
//                Map<String, String> fields = new HashMap<>();
//                fields.put("username", e.getUsername());
//                fields.put("level", String.valueOf(e.getLevel()));
//                fields.put("lastUpdated", String.valueOf(Instant.now().getEpochSecond()));
//
//                redisTemplate.opsForHash().putAll(playerKey, fields);
//            }
//            return null;
//        });
//    }
@Override
public void addPlayers(List<LeaderboardEntry> entries) {
    // Execute pipelined operations
    redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
        // Pipelining multiple commands for all players
        for (LeaderboardEntry e : entries) {
            // Add to sorted set
            connection.zAdd(LEADERBOARD_KEY.getBytes(StandardCharsets.UTF_8), e.getScore(),
                    e.getPlayerId().getBytes(StandardCharsets.UTF_8));

            // Set fields in hash
            Map<byte[], byte[]> fields = new HashMap<>();
            fields.put("username".getBytes(StandardCharsets.UTF_8), e.getUsername().getBytes(StandardCharsets.UTF_8));
            fields.put("level".getBytes(StandardCharsets.UTF_8), String.valueOf(e.getLevel()).getBytes(StandardCharsets.UTF_8));
            fields.put("lastUpdated".getBytes(StandardCharsets.UTF_8), String.valueOf(Instant.now().getEpochSecond()).getBytes(StandardCharsets.UTF_8));

            connection.hMSet(e.getPlayerId().getBytes(StandardCharsets.UTF_8), fields);
        }
        return null;
    });
}






}

