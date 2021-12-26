package com.billchau.authdemo.repository

import com.billchau.authdemo.model.EnumRole
import com.billchau.authdemo.model.Role
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RoleRepository: JpaRepository<Role, Long> {
    fun findByName(name: EnumRole): Role?
}