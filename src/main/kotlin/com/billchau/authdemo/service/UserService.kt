package com.billchau.authdemo.service

import com.billchau.authdemo.model.User
import com.billchau.authdemo.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepository) {

    fun save(user: User): User = userRepository.save(user)

    fun findByEmail(email: String): User? = userRepository.findByEmail(email)

    fun getById(id: Int): User = userRepository.getById(id)
}