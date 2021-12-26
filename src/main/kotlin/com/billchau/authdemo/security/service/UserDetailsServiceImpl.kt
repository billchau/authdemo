package com.billchau.authdemo.security.service

import com.billchau.authdemo.model.User
import com.billchau.authdemo.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class UserDetailsServiceImpl(
    private val  userRepository: UserRepository
): UserDetailsService {

    @Transactional
    @Throws( UsernameNotFoundException::class )
    override fun loadUserByUsername(username: String): UserDetails {
        val user: User? = userRepository.findByUsername(username)
        user?.let {
            val userDetails = UserDetailsImpl.Builder().build(it)
            return userDetails
        } ?: run {
           throw UsernameNotFoundException("User Not Found with username: $username")
        }
    }
}