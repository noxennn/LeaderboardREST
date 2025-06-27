package com.yebyrkc.LeaderboardREST.config;

import com.yebyrkc.LeaderboardREST.model.LeaderboardEntry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

//@Configuration
//public class RedisConfig {
//    @Bean
//    public RedisTemplate<String , LeaderboardEntry> redisTemplate(RedisConnectionFactory factory){
//        RedisTemplate<String ,LeaderboardEntry> template = new RedisTemplate<>();
//        template.setConnectionFactory(factory);
//
//        //use string keys
//        template.setKeySerializer(new StringRedisSerializer()); //Key
//        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(LeaderboardEntry.class)); //value (for key and string value pairs)
//
//        template.setHashKeySerializer(new StringRedisSerializer()); //field (key of Hash)
//
//        template.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(LeaderboardEntry.class)); //value of hash (not for key and string value pair
//
//        return template;
//
//    }
//}
