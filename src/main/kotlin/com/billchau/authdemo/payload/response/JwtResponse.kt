package com.billchau.authdemo.payload.response

class JwtResponse {
    var token: String =""
    var refreshToken: String = ""
    val type = "Bearer"
    var id: Long = 0
    var username: String = ""
    var email: String = ""
    var roles: MutableList<String> = mutableListOf()


    constructor(accessToken: String, refreshToken: String, id: Long, username: String, email: String, roles: MutableList<String>) {
        this.token = accessToken
        this.refreshToken = refreshToken
        this.id = id
        this.username = username
        this.email = email
        this.roles = roles
    }

}