package com.billchau.authdemo.model

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import javax.persistence.*
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Entity
@Table(name = "USER",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["email", "username"])
    ])
class User {

    constructor(username: String, email: String, password: String) {
        this.username = username
        this.email = email
        this.password = password
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @Column
    @NotBlank
    @Size(max = 20)
    var username: String = ""

    @Column
    @NotBlank
    @Size(max = 50)
    @Email
    var email: String = ""

    @Column
    @NotBlank
    @Size(max = 120)
    var password: String = ""
//        @JsonIgnore
//        get() = field
//        set(value) {
//            field = BCryptPasswordEncoder().encode(value)
//        }

//    fun comparePassword(password: String): Boolean = BCryptPasswordEncoder().matches(password, this.password)

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "USER_ROLE",
            joinColumns = [JoinColumn(name = "user_id")],
            inverseJoinColumns = [JoinColumn(name = "role_id")])
    var roles: MutableSet<Role> = mutableSetOf()
}