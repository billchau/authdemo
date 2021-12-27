package com.billchau.authdemo.repository

import com.billchau.authdemo.model.RefreshToken
import com.billchau.authdemo.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.stereotype.Repository


@Repository
interface RefreshTokenRepository: JpaRepository<RefreshToken, Int> {
    fun findByToken(token: String): RefreshToken?

    @Modifying
    fun deleteByUser(user: User?): Int
}