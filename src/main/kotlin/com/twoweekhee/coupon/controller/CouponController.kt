package com.twoweekhee.coupon.controller

import com.twoweekhee.coupon.domain.Coupon
import com.twoweekhee.coupon.domain.IssuedCoupon
import com.twoweekhee.coupon.service.CouponService
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody

@Controller
class CouponController(
  private val couponService: CouponService
) {

  @GetMapping("/api/coupon")
  fun getCoupon(@RequestBody id: String): ResponseEntity<IssuedCoupon> {
    return ResponseEntity.ok(couponService.getCoupon(id))
  }

  @GetMapping("/api/coupons")
  fun generateCoupons(@RequestBody poolNumber: Int): ResponseEntity<List<Coupon>> {
    return ResponseEntity.ok(couponService.generateCoupons(poolNumber))
  }

  @DeleteMapping("/api/coupons")
  fun deleteCoupon(): ResponseEntity<Unit> {
    return ResponseEntity.ok(couponService.deleteAllCoupons())
  }
}