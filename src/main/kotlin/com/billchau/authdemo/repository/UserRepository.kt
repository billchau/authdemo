package com.billchau.authdemo.repository

import com.billchau.authdemo.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository: JpaRepository<User, Int> {
    fun findByEmail(email: String): User?

    fun findById(id: Long): User?
    fun findByUsername(username: String): User?
    fun existsByUsername(username: String): Boolean
    fun existsByEmail(email: String): Boolean
}