package com.billchau.authdemo.security.jtw

import com.billchau.authdemo.security.service.UserDetailsImpl
import io.jsonwebtoken.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.security.SignatureException
import java.util.*

@Component
class JwtUtil {
    private val logger: Logger = LoggerFactory.getLogger(JwtUtil::class.java)

    @Value("\${jwtproperties.jwtSecret}")
    private var jwtSecret: String = ""

    @Value("\${jwtproperties.jwtExpirationMs}")
    private var jwtExpirationMs = 0

    //version 1 generateJwtToken
//    fun generateJwtToken(authentication: Authentication): String {
//        val userPrincipal: UserDetailsImpl = authentication.principal as UserDetailsImpl
//        return Jwts.builder()
//            .setSubject(userPrincipal.username)
//            .setIssuedAt(Date())
//            .setExpiration(Date(Date().time + jwtExpirationMs))
//            .signWith(SignatureAlgorithm.HS512, jwtSecret)
//            .compact();
//    }

    // version 2 generateJwtToken
    fun generateJwtToken(userDetailsImpl: UserDetailsImpl): String = generateTokenFromUsername(userDetailsImpl.username)

    fun generateTokenFromUsername(username: String): String =
        Jwts.builder()
            .setSubject(username)
            .setIssuedAt(Date())
            .setExpiration(Date(Date().time + jwtExpirationMs))
            .signWith(SignatureAlgorithm.HS512, jwtSecret)
            .compact()

    fun getUserNameFromJwtToken(token: String): String =
        Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).body.subject

    fun validateToken(token: String): Boolean {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token)
            return true
        } catch (e: SignatureException) {
            logger.error("Invalid JWT signature: {}", e.message);
        } catch (e: MalformedJwtException) {
            logger.error("Invalid JWT token: {}", e.message);
        } catch (e: ExpiredJwtException) {
            logger.error("JWT token is expired: {}", e.message);
        } catch (e: UnsupportedJwtException) {
            logger.error("JWT token is unsupported: {}", e.message);
        } catch (e: IllegalArgumentException) {
            logger.error("JWT claims string is empty: {}", e.message);
        }

        return false;
    }
}