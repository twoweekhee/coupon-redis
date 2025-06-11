package com.twoweekhee.coupon.repository

import com.twoweekhee.coupon.domain.Coupon
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository

@Repository
class RedisRepository(
  private val stringRedisTemplate: RedisTemplate<String, String>
) {
  companion object {
    const val COUPON_POOL = "coupon:pool"
    const val COUPON_DETAILS = "coupon:details"
    const val USER_COUPON_MAP = "user:coupon"
    const val ISSUED_COUPONS = "coupon:issued"
    const val COUPON_COUNTER = "coupon:counter"
  }

  fun saveCouponsIdList(counponIds: List<String>) {
    stringRedisTemplate.opsForList().rightPushAll(COUPON_POOL, counponIds)
  }
}