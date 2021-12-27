package com.billchau.authdemo.controller

import com.billchau.authdemo.exception.TokenRefreshException
import com.billchau.authdemo.model.EnumRole
import com.billchau.authdemo.model.RefreshToken
import com.billchau.authdemo.model.Role
import com.billchau.authdemo.model.User
import com.billchau.authdemo.payload.request.LoginRequest
import com.billchau.authdemo.payload.request.SignupRequest
import com.billchau.authdemo.payload.request.TokenRefreshRequest
import com.billchau.authdemo.payload.response.JwtResponse
import com.billchau.authdemo.payload.response.MessageResponse
import com.billchau.authdemo.payload.response.TokenRefreshResponse
import com.billchau.authdemo.repository.RoleRepository
import com.billchau.authdemo.repository.UserRepository
import com.billchau.authdemo.security.jtw.JwtUtil
import com.billchau.authdemo.security.service.RefreshTokenService
import com.billchau.authdemo.security.service.UserDetailsImpl
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import java.util.*
import java.util.stream.Collectors
import javax.validation.Valid

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authenticationManager: AuthenticationManager,
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val refreshTokenService: RefreshTokenService,
    private val encoder: PasswordEncoder,
    private val jwtUtil: JwtUtil
) {
    @PostMapping("/signin")
    fun authenticateUser(@Valid @RequestBody loginRequest: LoginRequest): ResponseEntity<Any> {
        val token = UsernamePasswordAuthenticationToken(loginRequest.username, loginRequest.password)
        val authentication = authenticationManager.authenticate(token)
        SecurityContextHolder.getContext().authentication = authentication
        val userDetails = authentication.principal as UserDetailsImpl
        val jwt = jwtUtil.generateJwtToken(userDetails)
        val refreshToken = refreshTokenService.createRefreshToken(userDetails.id)

        val roles = userDetails.authorities.stream()
            .map { item -> item.authority }
            .collect(Collectors.toList())
        return ResponseEntity.ok(
            JwtResponse(
                accessToken = jwt,
                refreshToken = refreshToken.token,
                id = userDetails.id,
                username = userDetails.username,
                email = userDetails.email,
                roles = roles
            )
        )
    }

    @PostMapping("/refreshtoken")
    fun refreshToken(@Valid @RequestBody request: TokenRefreshRequest): ResponseEntity<Any> {
        val requestRefreshToken = request.refreshToken
        return refreshTokenService.findByToken(requestRefreshToken)
            ?.let(refreshTokenService::verifyExpiration)
            ?.let(RefreshToken::user)
            ?.let {
            val token = jwtUtil.generateTokenFromUsername(it.username)
            return ResponseEntity.ok(TokenRefreshResponse(token, requestRefreshToken))
        } ?: throw TokenRefreshException(requestRefreshToken, "Refresh token is not in database!")
    }

    @PostMapping("/signup")
    fun registerUser(@Valid @RequestBody signupRequest: SignupRequest): ResponseEntity<Any> {
        if (userRepository.existsByUsername(signupRequest.username)) {
            return ResponseEntity.badRequest().body(MessageResponse("Error: Username is already taken!"))
        }
        if (userRepository.existsByEmail(signupRequest.email)) {
            return ResponseEntity.badRequest().body(MessageResponse("Error: Email is already in use!"))
        }

        val user = User(
            username = signupRequest.username,
            email = signupRequest.email,
            password = encoder.encode(signupRequest.password)
        )
        val strRole = signupRequest.role
        val roles = mutableSetOf<Role>()
        if (strRole == null) {
            val userRole: Role = roleRepository.findByName(EnumRole.ROLE_USER)
                ?: throw RuntimeException("Error: Role is not found.")
            roles.add(userRole)
        } else {
            strRole.forEach { role ->
                when (role) {
                    "admin" -> {
                        val adminRole: Role = roleRepository.findByName(EnumRole.ROLE_ADMIN)
                            ?: throw RuntimeException("Error: Role is not found.")
                        roles.add(adminRole)
                    }
                    "mod" -> {
                        val modRole: Role = roleRepository.findByName(EnumRole.ROLE_MODERATOR)
                            ?: throw RuntimeException("Error: Role is not found.")
                        roles.add(modRole)
                    }
                    else -> {
                        val userRole: Role = roleRepository.findByName(EnumRole.ROLE_USER)
                            ?: throw RuntimeException("Error: Role is not found.")
                        roles.add(userRole)
                    }
                }
            }
        }
        user.roles = roles
        userRepository.save(user);

        return ResponseEntity.ok( MessageResponse("User registered successfully!"));
    }
}