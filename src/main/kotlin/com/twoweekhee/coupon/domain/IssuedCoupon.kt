package com.twoweekhee.coupon.domain

data class IssuedCoupon(
    val id: String,
    val code: String,
    val userId: String
) {
    companion object {
        fun from(coupon: Coupon, userId: String): IssuedCoupon {
            return IssuedCoupon(coupon.id, coupon.code, userId)
        }
    }
}