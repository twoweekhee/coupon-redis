package com.twoweekhee.coupon.performanceTest

data class PerformanceResult(
    val method: String,
    val writeTime: Long,
    val readTime: Long,
    val totalTime: Long
)