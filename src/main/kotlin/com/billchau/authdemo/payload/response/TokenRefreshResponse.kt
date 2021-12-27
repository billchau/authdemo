package com.billchau.authdemo.payload.response

class TokenRefreshResponse {
    var accessToken: String =""
    var refreshToken: String = ""
    val type = "Bearer"

    constructor(accessToken: String, refreshToken: String){
        this.accessToken = accessToken
        this.refreshToken = refreshToken
    }
}