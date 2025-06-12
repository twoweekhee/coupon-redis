package com.twoweekhee.coupon.controller

import com.twoweekhee.coupon.domain.Coupon
import com.twoweekhee.coupon.service.CouponService
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody

@Controller
class CouponController(
  val couponService: CouponService
) {

  @GetMapping
  fun getCoupon(@RequestBody id: String): ResponseEntity<Coupon> {
    return ResponseEntity.ok(couponService.getCoupon(id))
  }

  @GetMapping
  fun generateCoupons(poolNumber: Int): ResponseEntity<List<Coupon>> {
    return ResponseEntity.ok(couponService.generateCoupons(poolNumber))
  }
}