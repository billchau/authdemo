package com.billchau.authdemo.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.FORBIDDEN)
class TokenRefreshException(
    private val token: String,
    private val errorMessage: String)
    : RuntimeException(String.format("Failed for [%s]: %s", token, errorMessage)) {
    private val serialVersionUID = 1L
}