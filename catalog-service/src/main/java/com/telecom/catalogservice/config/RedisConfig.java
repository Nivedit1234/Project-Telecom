package com.telecom.catalogservice.config;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.telecom.catalogservice.dto.PlanResponse;

import tools.jackson.databind.JavaType;
import tools.jackson.databind.json.JsonMapper;

@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
    public RedisCacheManager cacheManager(
            RedisConnectionFactory connectionFactory) {

        JsonMapper jsonMapper = JsonMapper.builder()
                .build();

        StringRedisSerializer keySerializer =
                new StringRedisSerializer();

        /*
         * Typed serializer for a single PlanResponse.
         */
        JacksonJsonRedisSerializer<PlanResponse> planSerializer =
                new JacksonJsonRedisSerializer<>(
                        jsonMapper,
                        PlanResponse.class
                );

        /*
         * Typed serializer for List<PlanResponse>.
         */
        JavaType planListType =
                jsonMapper.getTypeFactory()
                        .constructCollectionType(
                                List.class,
                                PlanResponse.class
                        );

        JacksonJsonRedisSerializer<List<PlanResponse>> planListSerializer =
                new JacksonJsonRedisSerializer<>(
                        jsonMapper,
                        planListType
                );

        RedisCacheConfiguration defaultConfiguration =
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofMinutes(10))
                        .disableCachingNullValues()
                        .serializeKeysWith(
                                RedisSerializationContext.SerializationPair
                                        .fromSerializer(keySerializer)
                        );

        /*
         * Cache for:
         * @Cacheable(value = "plan", key = "#id")
         */
        RedisCacheConfiguration planConfiguration =
                defaultConfiguration
                        .serializeValuesWith(
                                RedisSerializationContext.SerializationPair
                                        .fromSerializer(planSerializer)
                        );

        /*
         * Cache for:
         * @Cacheable(value = "planCode", key = "#code")
         */
        RedisCacheConfiguration planCodeConfiguration =
                defaultConfiguration
                        .serializeValuesWith(
                                RedisSerializationContext.SerializationPair
                                        .fromSerializer(planSerializer)
                        );

        /*
         * Cache for:
         * @Cacheable(value = "plans", key = "'active'")
         */
        RedisCacheConfiguration plansConfiguration =
                defaultConfiguration
                        .serializeValuesWith(
                                RedisSerializationContext.SerializationPair
                                        .fromSerializer(planListSerializer)
                        );

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfiguration)
                .withInitialCacheConfigurations(
                        java.util.Map.of(
                                "plan", planConfiguration,
                                "planCode", planCodeConfiguration,
                                "plans", plansConfiguration
                        )
                )
                .build();
    }
}