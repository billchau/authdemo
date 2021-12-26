package com.billchau.authdemo.security.service

import com.billchau.authdemo.model.User
import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.stream.Collectors





class UserDetailsImpl: UserDetails {
    val serialVersionUID = 1L
    var  id: Long = 0
    private var username = ""
    var email = ""
    @JsonIgnore
    private var password = ""
    private var authorities: MutableCollection<GrantedAuthority> = mutableSetOf()

    constructor(id: Long, username: String, email: String,  password: String,
                authorities: MutableCollection<GrantedAuthority>) {
        this.id = id
        this.username = username
        this.email = email
        this.password = password
        this.authorities = authorities
    }

    class Builder() {
        fun build(user: User): UserDetailsImpl {

            val authorities: MutableCollection<GrantedAuthority> = user.roles.stream()
                .map { role -> SimpleGrantedAuthority(role.name.name) }
                .collect(Collectors.toList())

            return UserDetailsImpl(
                id = user.id,
                username = user.username,
                email = user.email,
                password = user.password,
                authorities = authorities
            )
        }
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return authorities
    }


    override fun getPassword(): String {
        return password
    }

    override fun getUsername(): String {
        return username
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }
}