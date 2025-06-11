package com.twoweekhee.coupon.repository

import com.twoweekhee.coupon.common.CustomException
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

  fun getCoupon(): String {
    return stringRedisTemplate.opsForList().leftPop(COUPON_POOL)?: throw CustomException("쿠폰이 더 이상 존재하지 않습니다.")
  }
}