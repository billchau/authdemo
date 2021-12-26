package com.billchau.authdemo.controller

import com.billchau.authdemo.model.EnumRole
import com.billchau.authdemo.model.Role
import com.billchau.authdemo.model.User
import com.billchau.authdemo.repository.RoleRepository
import com.billchau.authdemo.repository.UserRepository
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/api/test")
class TestController(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository
) {
    @GetMapping("health")
    fun health() = ResponseEntity.ok("OK")

    @GetMapping("init")
    fun initData() {
        val adminRole = Role()
        adminRole.name = EnumRole.ROLE_ADMIN


        val userRole = Role()
        userRole.name = EnumRole.ROLE_USER

        val modRole = Role()
        modRole.name = EnumRole.ROLE_MODERATOR
        roleRepository.saveAll(mutableListOf(adminRole, userRole, modRole))

        ResponseEntity.ok("OK")

    }

    @GetMapping("/all")
    fun allAccess(): String = "Public Content."


    @GetMapping("/user")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    fun userAccess(): String = "User Content."


    @GetMapping("/mod")
    @PreAuthorize("hasRole('MODERATOR')")
    fun moderatorAccess(): String = "Moderator Board."

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    fun adminAccess(): String = "Admin Board."
}