package com.billchau.authdemo.model

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import javax.persistence.*

@Entity
@Table(name = "USER")
class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Int = 0

    @Column
    var name: String = ""

    @Column(unique = true)
    var email: String = ""

    @Column
    var password: String = ""
        @JsonIgnore
        get() = field
        set(value) {
            field = BCryptPasswordEncoder().encode(value)
        }

    fun comparePassword(password: String): Boolean = BCryptPasswordEncoder().matches(password, this.password)
}