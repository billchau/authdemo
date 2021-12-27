package com.billchau.authdemo.payload.request

import javax.validation.constraints.NotBlank

class TokenRefreshRequest {
    @NotBlank
    var refreshToken = ""
}