package com.billchau.authdemo.model

import java.time.Instant
import javax.persistence.*

@Entity(name = "REFRESH_TOKEN")
class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    var user: User? = null

    @Column(nullable = false, unique = true)
    var token: String = ""

    @Column(nullable = false)
    var expiryDate: Instant = Instant.now()
}