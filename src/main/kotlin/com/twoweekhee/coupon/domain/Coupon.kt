package com.twoweekhee.coupon.domain

import org.springframework.cache.annotation.Cacheable

@Cacheable(cacheNames = ["coupon"])
data class Coupon(
    val id: String,
    val code: String
) {
    companion object {
        fun of(id: String, code: String): Coupon {
            return Coupon(id = id, code = code)
        }
    }
}