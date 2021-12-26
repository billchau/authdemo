package com.billchau.authdemo.controller

import com.billchau.authdemo.model.EnumRole
import com.billchau.authdemo.model.Role
import com.billchau.authdemo.model.User
import com.billchau.authdemo.payload.request.LoginRequest
import com.billchau.authdemo.payload.request.SignupRequest
import com.billchau.authdemo.payload.response.JwtResponse
import com.billchau.authdemo.payload.response.MessageResponse
import com.billchau.authdemo.repository.RoleRepository
import com.billchau.authdemo.repository.UserRepository
import com.billchau.authdemo.security.jtw.JwtUtil
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
    private val encoder: PasswordEncoder,
    private val jwtUtil: JwtUtil
) {
    @PostMapping("/signin")
    fun authenticateUser(@Valid @RequestBody loginRequest: LoginRequest): ResponseEntity<Any> {
        val token = UsernamePasswordAuthenticationToken(loginRequest.username, loginRequest.password)
        val authentication = authenticationManager.authenticate(token)
        print(authentication)
        SecurityContextHolder.getContext().authentication = authentication
        val jwt = jwtUtil.generateJwtToken(authentication)
        print(jwt)
        val userDetails = authentication.principal as UserDetailsImpl
        val roles = userDetails.authorities.stream()
            .map { item -> item.authority }
            .collect(Collectors.toList())
        return ResponseEntity.ok(
            JwtResponse(
                accessToken = jwt,
                id = userDetails.id,
                username = userDetails.username,
                email = userDetails.email,
                roles = roles
            )
        )
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
//
//    @PostMapping("register")
//    fun register(@RequestBody body: RegisterDTO): ResponseEntity<User> {
//        val user = User()
//        user.name = body.name
//        user.password = body.password
//        user.email = body.email
//        return ResponseEntity.ok(userService.save(user))
//    }
//
//    @PostMapping("login")
//    fun  login(@RequestBody body: LoginDTO, response: HttpServletResponse): ResponseEntity<Any> {
//        val user = userService.findByEmail(body.email)
//            ?: return ResponseEntity.badRequest().body(GeneralException("User not found"))
//        if (!user.comparePassword(body.password)) {
//            return ResponseEntity.badRequest().body(GeneralException("Invalid username / password"))
//        }
//
//        val issuer = user.id.toString()
//        val secret = "secret"
//        val jwt = Jwts.builder()
//            .setIssuer(issuer)
//            .setExpiration(Date(System.currentTimeMillis() + 60 * 60 * 1000)) // 1 hour
//            .signWith(SignatureAlgorithm.HS256, secret).compact()
//
//        // save the jwt nto cookie
//        val cookie = Cookie("jwt", jwt)
//        cookie.isHttpOnly = true
//        response.addCookie(cookie)
//
//        return ResponseEntity.ok("success")
//    }
//
//    @GetMapping("user")
//    fun user(@CookieValue("jwt") jwt: String?): ResponseEntity<Any> {
//        try {
//            if (jwt == null) {
//                return ResponseEntity.status(401). body(GeneralException("unauthenticated"))
//            }
//
//            val secret = "secret"
//            val body = Jwts.parser().setSigningKey(secret).parseClaimsJws(jwt).body
//            return ResponseEntity.ok(userService.getById(body.issuer.toInt()))
//        } catch (e: Exception) {
//            return ResponseEntity.status(401). body(GeneralException("unauthenticated"))
//        }
//
//    }
//
//    @PostMapping("logout")
//    fun logout(response: HttpServletResponse): ResponseEntity<Any> {
//        val cookie = Cookie("jwt", "")
//        cookie.maxAge = 0
//        response.addCookie(cookie)
//        return ResponseEntity.ok("OK")
//    }
}