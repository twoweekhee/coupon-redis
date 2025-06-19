package com.twoweekhee.coupon.performanceTest

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/api/performance")
class PerformanceTestController(
    private val performanceTestService: PerformanceTestService
) {

    @GetMapping("/quick")
    fun quickTest(@RequestParam(defaultValue = "100") testCount: Int): Map<String, Any> {
        val stringResult = performanceTestService.stringRedisTemplateTest(testCount)
        val anyResult = performanceTestService.redisTemplateTest(testCount)
        val typedResult = performanceTestService.couponRedisTemplateTest(testCount)

        return mapOf(
            "testCount" to testCount,
            "results" to listOf(stringResult, anyResult, typedResult),
        )
    }

    @DeleteMapping("/cleanup")
    fun cleanupTestData(): Map<String, String> {
        return try {
            performanceTestService.cleanupAllTestData()
            mapOf("message" to "모든 테스트 데이터가 정리되었습니다.")
        } catch (e: Exception) {
            mapOf("error" to "테스트 데이터 정리 중 오류 발생: ${e.message}")
        }
    }
}