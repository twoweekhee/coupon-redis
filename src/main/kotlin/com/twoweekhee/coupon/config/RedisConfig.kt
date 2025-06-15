package com.twoweekhee.coupon.config

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
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
    fun redisTemplate(lettuceConnectionFactory: LettuceConnectionFactory): RedisTemplate<String, Any> {
        val redisTemplate = RedisTemplate<String, Any>().apply {
            keySerializer = StringRedisSerializer()
            hashKeySerializer = StringRedisSerializer()

            valueSerializer = redisSerializer()
            hashValueSerializer = redisSerializer()

            setDefaultSerializer(redisSerializer())
            connectionFactory = lettuceConnectionFactory

            afterPropertiesSet()
        }

        return redisTemplate
    }

    @Bean
    fun stringRedisTemplate(lettuceConnectionFactory: LettuceConnectionFactory): StringRedisTemplate {

        return StringRedisTemplate().apply {
            connectionFactory = lettuceConnectionFactory
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

    @Bean
    fun redisSerializer(): GenericJackson2JsonRedisSerializer {

        val objectMapper = ObjectMapper().apply {
            registerModule(JavaTimeModule())
            registerModule(KotlinModule.Builder().build()) // Kotlin 모듈 추가
            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            activateDefaultTyping(
                //어떤 타입을 역직렬화 허용할 지
                BasicPolymorphicTypeValidator.builder()
                    .allowIfSubType(Any::class.java)
                    .build(),
                //타입정보 포함하는 경우
                ObjectMapper.DefaultTyping.NON_FINAL,
                //타입 정보 포함 방식
                JsonTypeInfo.As.PROPERTY
            )
        }

        return GenericJackson2JsonRedisSerializer(objectMapper)
    }
}