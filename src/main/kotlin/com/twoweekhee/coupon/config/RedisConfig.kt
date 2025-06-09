package com.twoweekhee.coupon.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfig {

    @Bean
    fun RedisTemplate(): RedisTemplate<String, Any> {
        val redisTemplate = RedisTemplate<String, Any>().apply {
            keySerializer = StringRedisSerializer()
            hashKeySerializer = StringRedisSerializer()

            valueSerializer = GenericJackson2JsonRedisSerializer()
            hashValueSerializer = GenericJackson2JsonRedisSerializer()

            setDefaultSerializer(GenericJackson2JsonRedisSerializer())
            connectionFactory = LettuceConnectionFactory()

            afterPropertiesSet()
        }

        return redisTemplate
    }

    @Bean
    fun StringRedisTemplate(): StringRedisTemplate {

        return StringRedisTemplate().apply {
            connectionFactory = LettuceConnectionFactory()
        }
    }

    @Bean
    fun lettuceConnectionFactory(): LettuceConnectionFactory {
        val redisStandaloneConfiguration = RedisStandaloneConfiguration().apply {
            hostName = "localhost"
            port = 6379
        }

        return LettuceConnectionFactory(redisStandaloneConfiguration)
    }
}