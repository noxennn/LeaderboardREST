package com.yebyrkc.LeaderboardREST.repository;

import com.yebyrkc.LeaderboardREST.exception.PlayerNotFoundException;
import com.yebyrkc.LeaderboardREST.model.LeaderboardEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.nio.charset.StandardCharsets;
import java.util.*;



@Profile("redis")
public class RedisSortedSetLeaderboardRepository implements ILeaderboardRepository {

        private final RedisTemplate<String, String> redisTemplate;


        private static final Logger logger = LoggerFactory.getLogger(RedisSortedSetLeaderboardRepository.class);

        private static final String LEADERBOARD_KEY = "leaderboard";
        private static final String PLAYER_HASH_PREFIX = "player:";



        public RedisSortedSetLeaderboardRepository(RedisTemplate<String, String> redisTemplate  ) {
            this.redisTemplate = redisTemplate;
        }



    @Override
        public double incrementScore(String playerId, double increment) {
            logger.debug("Incrementing score for {} by {} in Redis", playerId, increment);
            Double newScore = redisTemplate.opsForZSet().incrementScore(LEADERBOARD_KEY, playerId, increment);

            if (newScore == null) {
                throw new PlayerNotFoundException("Player not found: " + playerId);
            }

            // Update time
            return newScore;
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
            if (n <= 0) {
                throw new IllegalArgumentException("n must be greater than 0");
            }
            logger.debug("Retrieving top {} players from Redis", n);
            Set<ZSetOperations.TypedTuple<String>> top = redisTemplate.opsForZSet()
                    .reverseRangeWithScores(LEADERBOARD_KEY, 0, n - 1);

            //seperate them into two lists for filtering playerId and use it for getting player attributes
            //from player Hashes (we are not storing player attributes in leaderboard set actually,
            // we need to get them from hashes
            List<LeaderboardEntry> result = new ArrayList<>();
            if (top == null) {
                return  result ;
            }
            List<String> playerIds = top.stream().map(ZSetOperations.TypedTuple::getValue).toList();
            List<Double> scores = top.stream().map(ZSetOperations.TypedTuple::getScore).toList();

            //get hashes of players (in byte format)
            // Pipelined HMGET for each player
            List<Object> hashResults = redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
                for (String id : playerIds) {
                    //convert playerId's into bytes
                    byte[] key = id.getBytes(StandardCharsets.UTF_8);
                    //HMGET with byte key and byte field names
                    connection.hashCommands().hMGet(
                            key,
                            "username".getBytes(StandardCharsets.UTF_8),
                            "level".getBytes(StandardCharsets.UTF_8)
                    );

                }
                return null;
            });

            //convert byte hashes  and create the LeaderboardEntry
            for (int i = 0; i < playerIds.size(); i++) {
                //get playerId from list
                String playerId = playerIds.get(i);

                double score = scores.get(i) != null ? scores.get(i) : 0.0;

//            @SuppressWarnings("unchecked")
//            List<byte[]> values = (List<byte[]>) hashResults.get(i);

                @SuppressWarnings("unchecked")
                List<String> values = (List<String>) hashResults.get(i);
                //decode bytes to Strings
                String username = values.get(0) ;
                String levelStr = values.get(1) ;


                int level = levelStr != null ? Integer.parseInt(levelStr) : 0;


                //create entry
                result.add(new LeaderboardEntry(playerId, username, score, level));
            }
            //return list of entries
            return result;
        }
//    @Override
//    public List<LeaderboardEntry> getTopPlayers(int n) {
//        if (n <= 0) {
//            throw new IllegalArgumentException("n must be greater than 0");
//        }
//        logger.debug("Retrieving top {} players from Redis", n);
//
//        // Step 1: Get the top N players from the Sorted Set
//        Set<ZSetOperations.TypedTuple<String>> top = redisTemplate.opsForZSet()
//                .reverseRangeWithScores(LEADERBOARD_KEY, 0, n - 1);
//
//        // Step 2: Early return if no players
//        if (top == null || top.isEmpty()) {
//            return Collections.emptyList();
//        }
//
//        // Step 3: Separate out player IDs and scores using Java Streams (streaming approach)
//        List<String> playerIds = top.stream()
//                .map(ZSetOperations.TypedTuple::getValue)
//                .collect(Collectors.toList());
//
//        List<Double> scores = top.stream()
//                .map(ZSetOperations.TypedTuple::getScore)
//                .collect(Collectors.toList());
//
//        // Step 4: Fetch player details in a pipelined approach
//        List<Object> hashResults = redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
//            for (String playerId : playerIds) {
//                byte[] key = playerId.getBytes(StandardCharsets.UTF_8);
//                connection.hashCommands().hMGet(
//                        key,
//                        "username".getBytes(StandardCharsets.UTF_8),
//                        "level".getBytes(StandardCharsets.UTF_8)
//                );
//            }
//            return null;
//        });
//
//        // Step 5: Convert the results and stream processing
//        List<LeaderboardEntry> result = new ArrayList<>();
//        Iterator<String> playerIdIterator = playerIds.iterator();
//        Iterator<Double> scoreIterator = scores.iterator();
//        Iterator<Object> hashResultIterator = hashResults.iterator();
//
//        while (playerIdIterator.hasNext() && hashResultIterator.hasNext() && scoreIterator.hasNext()) {
//            String playerId = playerIdIterator.next();
//            Double score = scoreIterator.next();
//
//            @SuppressWarnings("unchecked")
//            List<String> values = (List<String>) hashResultIterator.next();
//            String username = values.get(0);
//            String levelStr = values.get(1);
//
//            int level = levelStr != null ? Integer.parseInt(levelStr) : 0;
//
//            // Create leaderboard entry and add to the result list
//            result.add(new LeaderboardEntry(playerId, username, score != null ? score : 0.0, level);
//        }
//
//        // Return the combined list of leaderboard entries
//        return result;
//    }



    @Override
        public LeaderboardEntry getPlayer(String playerId) {
            logger.debug("Retrieving player {} from Redis", playerId);
            Double score = redisTemplate.opsForZSet().score(LEADERBOARD_KEY, playerId);
            if (score == null) {
                throw new PlayerNotFoundException("Player not found: " + playerId);
            }
            return getPlayerWithScore(playerId, score);
        }

        @Override
        public long getPlayerRank(String playerId) {
            logger.debug("Retrieving rank for {} from Redis", playerId);
            Long rank = redisTemplate.opsForZSet().reverseRank(LEADERBOARD_KEY, playerId);
            if (rank == null) {
                throw new PlayerNotFoundException("Player not found: " + playerId);
            }
            return rank + 1; // return 1-based rank
        }

        /**
         * Helper method to get all player data from the hash and build a LeaderboardEntry object.
         */
        private LeaderboardEntry getPlayerWithScore(String playerId, double score) {
            var hashOps = redisTemplate.opsForHash();
            var username = (String) hashOps.get(PLAYER_HASH_PREFIX + playerId, "username");
            var levelStr = (String) hashOps.get(PLAYER_HASH_PREFIX + playerId, "level");
            int level = levelStr != null ? Integer.parseInt(levelStr) : 0;

            return new LeaderboardEntry(playerId, username, score, level);
        }
//    @Override
//    public void addPlayerEntry(String playerId, String username, int level, double initialScore) {
//        // add to sorted set
//        redisTemplate.opsForZSet().add(LEADERBOARD_KEY, playerId, initialScore);
//        // add hash data
        ////        var hashOps = redisTemplate.opsForHash();
//        redisTemplate.opsForHash().put(playerId, "username", username);
//        redisTemplate.opsForHash().put( playerId, "level", String.valueOf(level));
//    }
        @Override
        public void addPlayerEntry(String playerId, String username, int level, double initialScore) {
            logger.debug("Adding player to Redis with playerId={}", playerId);
            redisTemplate.opsForZSet().add(LEADERBOARD_KEY, playerId, initialScore);

            // Create a map with multiple fields to set in the hash
            Map<String, String> playerData = new HashMap<>();
            playerData.put("username", username);
            playerData.put("level", String.valueOf(level));

            // Add all fields to the hash in one go
            redisTemplate.opsForHash().putAll(playerId, playerData);
        }
        //    @Override
//    public void addPlayerEntry(String playerId, String username, int level, double initialScore) {
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
//    public void addPlayerEntries(List<LeaderboardEntry> entries) {
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
//
//                redisTemplate.opsForHash().putAll(playerKey, fields);
//            }
//            return null;
//        });
//    }
        @Override
        public void addPlayerEntries(List<LeaderboardEntry> entries) {
            logger.debug("Adding {} players to Redis", entries.size());
            // Execute pipelined operations
            redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
                // Pipelining multiple commands for all players
                for (LeaderboardEntry e : entries) {
                    // Add to sorted set with converting them into bytes
                    connection.zAdd(LEADERBOARD_KEY.getBytes(StandardCharsets.UTF_8), e.getScore(),
                            e.getPlayerId().getBytes(StandardCharsets.UTF_8));

                    // Set fields in hash by converting them into Bytes
                    Map<byte[], byte[]> fields = new HashMap<>();
                    fields.put("username".getBytes(StandardCharsets.UTF_8), e.getUsername().getBytes(StandardCharsets.UTF_8));
                    fields.put("level".getBytes(StandardCharsets.UTF_8), String.valueOf(e.getLevel()).getBytes(StandardCharsets.UTF_8));
                    connection.hMSet(e.getPlayerId().getBytes(StandardCharsets.UTF_8), fields);
                }
                return null;
            });
        }

        @Override
        public void deletePlayerEntry(String playerId) {
            logger.debug("Deleting player {} from Redis", playerId);
            // Remove from the sorted set
            redisTemplate.opsForZSet().remove(LEADERBOARD_KEY, playerId);
            // Delete the player's hash data
            redisTemplate.delete(playerId);
        }

        @Override
        public void deleteAllPlayerEntries() {
            logger.debug("Deleting all players from Redis");
            // Get all player IDs in the leaderboard sorted set
            Set<String> playerIds = redisTemplate.opsForZSet().range(LEADERBOARD_KEY, 0, -1);
            if (playerIds != null && !playerIds.isEmpty()) {
                // Delete all player hash keys
                redisTemplate.delete(playerIds);
            }
            // Clear the sorted set
            redisTemplate.delete(LEADERBOARD_KEY);
        }


    }



