//package com.yebyrkc.LeaderboardREST.config;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.module.SimpleModule;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
//
//@Configuration
//public class JacksonConfig {
//
//    @Bean
//    public ObjectMapper objectMapper() {
//        // Create a simple module
//        SimpleModule module = new SimpleModule();
//
//        // Add the custom serializer
//        module.addSerializer(Double.class, new ToIntegerFormatSerializer());
//
//        // Build ObjectMapper and register the module
//        return Jackson2ObjectMapperBuilder.json()
//                .modulesToInstall(module)  // Register the custom module
//                .build();
//    }
//}