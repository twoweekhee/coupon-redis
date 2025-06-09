package com.twoweekhee.coupon

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import mu.KotlinLogging

val logger = KotlinLogging.logger {}

@SpringBootApplication
class CouponApplication

fun main(args: Array<String>) {
    runApplication<CouponApplication>(*args)
}
