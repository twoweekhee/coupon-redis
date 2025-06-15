package com.twoweekhee.coupon.common

import org.springframework.http.HttpStatus

class CustomException(
    val httpStatus: HttpStatus,
    override val message: String
): RuntimeException(message)