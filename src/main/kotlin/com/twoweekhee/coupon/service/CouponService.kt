package com.twoweekhee.coupon.service

import com.twoweekhee.coupon.domain.Coupon
import com.twoweekhee.coupon.logger
import com.twoweekhee.coupon.repository.RedisRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class CouponService(
    private val redisRepository: RedisRepository
) {

    fun generateCoupons(poolNumber: Int): List<Coupon> {
        val coupons = mutableListOf<Coupon>()
        val couponIds = mutableListOf<String>()
        val generatedCouponsCode = mutableSetOf<String>()

        logger.info("쿠폰 생성 시작: $poolNumber 개")

        repeat(poolNumber) { index ->
            var couponCode: String

            do {
                couponCode = generateCouponCode()
            } while (generatedCouponsCode.contains(couponCode))

            generatedCouponsCode.add(couponCode)

            val coupon = Coupon(
                id = "COUPON_${index + 1}개",
                code = couponCode
            )
            couponIds.add(coupon.id)
            coupons.add(coupon)
        }
        logger.info("쿠폰 생성 완료: ${coupons.size} 개")

        redisRepository.saveCouponsIdList(couponIds)
        return coupons
    }

    fun generateCouponCode(): String {

        return "COUPON_${UUID.randomUUID().toString().replace("-", "").uppercase()}"
    }
}