package com.billchau.authdemo.advice

import com.billchau.authdemo.exception.TokenRefreshException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import java.util.*

@RestControllerAdvice
class TokenControllerAdvice {
    @ExceptionHandler(value = [TokenRefreshException::class])
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun handleTokenRefreshException(exception: TokenRefreshException, request: WebRequest): ErrorMessage =
        ErrorMessage(
            statusCode = HttpStatus.FORBIDDEN.value(),
            timestamp = Date(),
            message = exception.message ?: "",
            description = request.getDescription(false)
        )
}