package com.billchau.authdemo.security.service

import com.billchau.authdemo.exception.TokenRefreshException
import com.billchau.authdemo.model.RefreshToken
import com.billchau.authdemo.repository.RefreshTokenRepository
import com.billchau.authdemo.repository.UserRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.*
import kotlin.jvm.Throws

@Service
class RefreshTokenService(
    private val refreshTokenRepository: RefreshTokenRepository,
    private val userRepository: UserRepository
) {
    @Value("\${jwtproperties.jwtRefreshExpirationMs}")
    private var refreshTokenDurationMs: Long = 0

    fun findByToken(token: String): RefreshToken? = refreshTokenRepository.findByToken(token)

    fun createRefreshToken(userId: Long): RefreshToken {
        val refreshToken = RefreshToken()
        refreshToken.user = userRepository.findById(userId) ?: throw NoSuchElementException("No value present");
        refreshToken.expiryDate = Instant.now().plusMillis(refreshTokenDurationMs)
        refreshToken.token = UUID.randomUUID().toString()
        refreshTokenRepository.save(refreshToken)
        return refreshToken
    }

    fun verifyExpiration(token: RefreshToken): RefreshToken? {
        if (token.expiryDate < Instant.now()) {
            refreshTokenRepository.delete(token)
            throw TokenRefreshException(token.token, "Refresh token was expired. Please make a new signin request")
        }
        return token
    }

    @Transactional
    fun deleteByUserId(userId: Int): Int = refreshTokenRepository.deleteByUser(userRepository.findById(userId).get())
}