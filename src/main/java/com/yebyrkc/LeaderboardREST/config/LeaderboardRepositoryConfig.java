package com.yebyrkc.LeaderboardREST.config;

import com.yebyrkc.LeaderboardREST.repository.*;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.*;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class LeaderboardRepositoryConfig {

    @Bean
    @Profile("caffeine")
    public ILeaderboardRepository caffeineRepository(MeterRegistry meterRegistry) {
        ILeaderboardRepository impl = new CaffeineLeaderboardRepository();
        return new InstrumentedLeaderboardRepository(impl, meterRegistry, "caffeine");
    }

    @Bean
    @Profile("java")
    public ILeaderboardRepository javaRepository(MeterRegistry meterRegistry) {
        ILeaderboardRepository impl = new InMemoryLeaderboardRepository();
        return new InstrumentedLeaderboardRepository(impl, meterRegistry, "java");
    }

    @Bean
    @Profile("redis")
    public ILeaderboardRepository redisRepository(MeterRegistry meterRegistry, RedisTemplate<String, String> redisTemplate) {
        ILeaderboardRepository impl = new RedisSortedSetLeaderboardRepository(redisTemplate);
        return new InstrumentedLeaderboardRepository(impl, meterRegistry, "redis");
    }

}
