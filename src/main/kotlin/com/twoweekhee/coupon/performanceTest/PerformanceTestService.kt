package com.twoweekhee.coupon.performanceTest

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.twoweekhee.coupon.domain.Coupon
import com.twoweekhee.coupon.logger
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.*

@Service
class PerformanceTestService(
    private val stringRedisTemplate: RedisTemplate<String, String>,
    private val redisTemplate: RedisTemplate<String, Any>,
    private val couponRedisTemplate: RedisTemplate<String, Coupon>
){

    private final val ANY_KEY = "any_coupon:"
    private final val STRING_KEY = "string_coupon:"
    private final val COUPON_KEY = "typed_coupon:"

    private val objectMapper = ObjectMapper().apply {
        registerModule(KotlinModule.Builder().build())
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    fun stringRedisTemplateTest(testCount: Int = 1000): PerformanceResult {
        val testCoupons = (1..testCount).map { createTestCoupon(it.toString()) }

        // Write 성능 측정
        val writeStartTime = System.currentTimeMillis()
        testCoupons.forEach { coupon ->
            val jsonString = objectMapper.writeValueAsString(coupon)
            stringRedisTemplate.opsForValue().set("$STRING_KEY${coupon.id}", jsonString)
        }
        val writeEndTime = System.currentTimeMillis()
        val writeTime = writeEndTime - writeStartTime

        // Read 성능 측정
        val readStartTime = System.currentTimeMillis()
        val readCoupons = testCoupons.map { coupon ->
            val jsonString = stringRedisTemplate.opsForValue().get("$STRING_KEY${coupon.id}")
            jsonString?.let { objectMapper.readValue(it, Coupon::class.java) }
        }
        val readEndTime = System.currentTimeMillis()
        val readTime = readEndTime - readStartTime

        return PerformanceResult(
            method = "StringRedisTemplate + ObjectMapper",
            writeTime = writeTime,
            readTime = readTime,
            totalTime = writeTime + readTime
        ).also {
            logger.info("StringRedisTemplate 테스트 완료: ${readCoupons.size}개 처리, readTime : ${readTime}, writeTime : ${writeTime}")
        }
    }

    fun redisTemplateTest(testCount: Int = 1000): PerformanceResult {
        val testCoupons = (1..testCount).map { createTestCoupon(it.toString()) }

        // Write 성능 측정
        val writeStartTime = System.currentTimeMillis()
        testCoupons.forEach { coupon ->
            redisTemplate.opsForValue().set("$ANY_KEY${coupon.id}", coupon)
        }
        val writeEndTime = System.currentTimeMillis()
        val writeTime = writeEndTime - writeStartTime

        // Read 성능 측정
        val readStartTime = System.currentTimeMillis()
        val readCoupons = testCoupons.map { coupon ->
            redisTemplate.opsForValue().get("$ANY_KEY${coupon.id}") as? Coupon
        }
        val readEndTime = System.currentTimeMillis()
        val readTime = readEndTime - readStartTime

        return PerformanceResult(
            method = "RedisTemplate<String, Any>",
            writeTime = writeTime,
            readTime = readTime,
            totalTime = writeTime + readTime
        ).also {
            logger.info("RedisTemplate<Any> 테스트 완료: ${readCoupons.size}개 처리, readTime : ${readTime}, writeTime : ${writeTime}")
        }
    }

    fun couponRedisTemplateTest(testCount: Int = 1000): PerformanceResult {
        val testCoupons = (1..testCount).map { createTestCoupon(it.toString()) }

        // Write 성능 측정
        val writeStartTime = System.currentTimeMillis()
        testCoupons.forEach { coupon ->
            couponRedisTemplate.opsForValue().set("$COUPON_KEY${coupon.id}", coupon)
        }
        val writeEndTime = System.currentTimeMillis()
        val writeTime = writeEndTime - writeStartTime

        // Read 성능 측정
        val readStartTime = System.currentTimeMillis()
        val readCoupons = testCoupons.map { coupon ->
            couponRedisTemplate.opsForValue().get("$COUPON_KEY${coupon.id}")
        }
        val readEndTime = System.currentTimeMillis()
        val readTime = readEndTime - readStartTime

        return PerformanceResult(
            method = "RedisTemplate<String, Coupon>",
            writeTime = writeTime,
            readTime = readTime,
            totalTime = writeTime + readTime
        ).also {
            logger.info("RedisTemplate<Coupon> 테스트 완료: ${readCoupons.size}개 처리, readTime : ${readTime}, writeTime : ${writeTime}")
        }
    }

    fun cleanupAllTestData() {
        try {
            val patterns = listOf("$STRING_KEY*", "$ANY_KEY*", "$COUPON_KEY*")
            var totalDeletedKeys = 0

            patterns.forEach { pattern ->
                val keys = when {
                    pattern.startsWith(STRING_KEY) -> stringRedisTemplate.keys(pattern)
                    pattern.startsWith(ANY_KEY) -> redisTemplate.keys(pattern)
                    pattern.startsWith(COUPON_KEY) -> couponRedisTemplate.keys(pattern)
                    else -> emptySet()
                }

                if (keys?.isNotEmpty() == true) {
                    val deletedCount = when {
                        pattern.startsWith(STRING_KEY) -> stringRedisTemplate.delete(keys)
                        pattern.startsWith(ANY_KEY) -> redisTemplate.delete(keys)
                        pattern.startsWith(COUPON_KEY) -> couponRedisTemplate.delete(keys)
                        else -> 0L
                    }
                    totalDeletedKeys += deletedCount.toInt()
                    logger.info("삭제된 키 개수 ($pattern): $deletedCount")
                }
            }
            logger.info("모든 테스트 데이터 정리 완료. 총 삭제된 키: $totalDeletedKeys")
        } catch (e: Exception) {
            logger.error("테스트 데이터 정리 중 오류 발생: ${e.message}")
            throw e
        }
    }

    private fun createTestCoupon(id: String): Coupon {
        return Coupon(
            id = id,
            code = generateCouponCode()
        )
    }

    private fun generateCouponCode(): String {
        return "COUPON_${UUID.randomUUID().toString().replace("-", "").uppercase()}"
    }
}