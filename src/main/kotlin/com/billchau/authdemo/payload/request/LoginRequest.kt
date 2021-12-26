package com.billchau.authdemo.payload.request

import javax.validation.constraints.NotBlank

class LoginRequest {
    @NotBlank
    var username = ""
    @NotBlank
    var password = ""
}