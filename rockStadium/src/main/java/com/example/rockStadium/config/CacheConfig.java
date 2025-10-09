package com.example.rockStadium.config;

import java.util.Arrays;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(Arrays.asList(
            new ConcurrentMapCache("hotels"),
            new ConcurrentMapCache("transport"),
            new ConcurrentMapCache("parking"),
            new ConcurrentMapCache("venues"),
            new ConcurrentMapCache("placeDetails")
        ));
        return cacheManager;
    }
}