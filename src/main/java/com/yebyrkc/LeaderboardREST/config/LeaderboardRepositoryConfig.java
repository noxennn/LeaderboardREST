package com.yebyrkc.LeaderboardREST.config;

import com.yebyrkc.LeaderboardREST.repository.*;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.*;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class LeaderboardRepositoryConfig {

    @Bean
    @Profile("caffeine")
    public LeaderboardRepository caffeineRepository(MeterRegistry meterRegistry) {
        LeaderboardRepository impl = new CaffeineLeaderboardRepository();
        return new InstrumentedLeaderboardRepository(impl, meterRegistry, "caffeine");
    }

    @Bean
    @Profile("java")
    public LeaderboardRepository javaRepository(MeterRegistry meterRegistry) {
        LeaderboardRepository impl = new InMemoryLeaderboardRepository();
        return new InstrumentedLeaderboardRepository(impl, meterRegistry, "java");
    }

    @Bean
    @Profile("redis")
    public LeaderboardRepository redisRepository(MeterRegistry meterRegistry, RedisTemplate<String, String> redisTemplate) {
        LeaderboardRepository impl = new RedisLeaderboardRepository(redisTemplate);
        return new InstrumentedLeaderboardRepository(impl, meterRegistry, "redis");
    }
}
