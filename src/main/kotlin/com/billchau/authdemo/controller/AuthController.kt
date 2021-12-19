package com.billchau.authdemo.controller

import com.billchau.authdemo.dto.LoginDTO
import com.billchau.authdemo.dto.RegisterDTO
import com.billchau.authdemo.exception.GeneralException
import com.billchau.authdemo.model.User
import com.billchau.authdemo.service.UserService
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("api")
class AuthController(private val userService: UserService) {
    @GetMapping("health")
    fun health() = ResponseEntity.ok("OK")

    @PostMapping("register")
    fun register(@RequestBody body: RegisterDTO): ResponseEntity<User> {
        val user = User()
        user.name = body.name
        user.password = body.password
        user.email = body.email
        return ResponseEntity.ok(userService.save(user))
    }

    @PostMapping("login")
    fun  login(@RequestBody body: LoginDTO, response: HttpServletResponse): ResponseEntity<Any> {
        val user = userService.findByEmail(body.email)
            ?: return ResponseEntity.badRequest().body(GeneralException("User not found"))
        if (!user.comparePassword(body.password)) {
            return ResponseEntity.badRequest().body(GeneralException("Invalid username / password"))
        }

        val issuer = user.id.toString()
        val secret = "secret"
        val jwt = Jwts.builder()
            .setIssuer(issuer)
            .setExpiration(Date(System.currentTimeMillis() + 60 * 60 * 1000)) // 1 hour
            .signWith(SignatureAlgorithm.HS256, secret).compact()

        // save the jwt nto cookie
        val cookie = Cookie("jwt", jwt)
        cookie.isHttpOnly = true
        response.addCookie(cookie)

        return ResponseEntity.ok("success")
    }

    @GetMapping("user")
    fun user(@CookieValue("jwt") jwt: String?): ResponseEntity<Any> {
        try {
            if (jwt == null) {
                return ResponseEntity.status(401). body(GeneralException("unauthenticated"))
            }

            val secret = "secret"
            val body = Jwts.parser().setSigningKey(secret).parseClaimsJws(jwt).body
            return ResponseEntity.ok(userService.getById(body.issuer.toInt()))
        } catch (e: Exception) {
            return ResponseEntity.status(401). body(GeneralException("unauthenticated"))
        }

    }

    @PostMapping("logout")
    fun logout(response: HttpServletResponse): ResponseEntity<Any> {
        val cookie = Cookie("jwt", "")
        cookie.maxAge = 0
        response.addCookie(cookie)
        return ResponseEntity.ok("OK")
    }
}