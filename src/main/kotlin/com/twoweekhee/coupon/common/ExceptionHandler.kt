package com.twoweekhee.coupon.common

import com.twoweekhee.coupon.logger
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionHandler {

    @ExceptionHandler(CustomException::class)
    fun handleCustomException(customException: CustomException) {
        logger.error("Custom exception: {}", customException.message)

        ResponseEntity.status(customException.httpStatus).body(customException.message)
    }
}