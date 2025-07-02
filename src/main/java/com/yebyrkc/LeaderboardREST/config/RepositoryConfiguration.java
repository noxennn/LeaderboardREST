package com.yebyrkc.LeaderboardREST.config;

import com.yebyrkc.LeaderboardREST.repository.*;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * Provides decorated repository beans with instrumentation applied.
 */
@Configuration
public class RepositoryConfiguration {

    @Bean("javaRepo")
    public LeaderboardRepository javaRepository(MeterRegistry registry) {
        return new InstrumentedLeaderboardRepository(new InMemoryLeaderboardRepository(), registry, "java");
    }

    @Bean("caffeineRepo")
    public LeaderboardRepository caffeineRepository(MeterRegistry registry) {
        return new InstrumentedLeaderboardRepository(new CaffeineLeaderboardRepository(), registry, "caffeine");
    }

    @Bean("redisRepo")
    public LeaderboardRepository redisRepository(RedisTemplate<String, String> redisTemplate, MeterRegistry registry) {
        return new InstrumentedLeaderboardRepository(new RedisLeaderboardRepository(redisTemplate, registry), registry, "redis");
    }
}
