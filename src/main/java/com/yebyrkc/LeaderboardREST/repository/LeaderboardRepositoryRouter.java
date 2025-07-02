package com.yebyrkc.LeaderboardREST.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Simple router to resolve repositories by type.
 */
@Component
public class LeaderboardRepositoryRouter {

    private final LeaderboardRepository javaRepo;
    private final LeaderboardRepository caffeineRepo;
    private final LeaderboardRepository redisRepo;

    public LeaderboardRepositoryRouter(@Qualifier("javaRepo") LeaderboardRepository javaRepo,
                                        @Qualifier("caffeineRepo") LeaderboardRepository caffeineRepo,
                                        @Qualifier("redisRepo") LeaderboardRepository redisRepo) {
        this.javaRepo = javaRepo;
        this.caffeineRepo = caffeineRepo;
        this.redisRepo = redisRepo;
    }

    public LeaderboardRepository resolve(String type) {
        return switch (type.toLowerCase()) {
            case "java" -> javaRepo;
            case "caffeine" -> caffeineRepo;
            case "redis" -> redisRepo;
            default -> throw new IllegalArgumentException("Unknown repository type: " + type);
        };
    }

    public List<LeaderboardRepository> getAll() {
        return List.of(javaRepo, caffeineRepo, redisRepo);
    }
}
