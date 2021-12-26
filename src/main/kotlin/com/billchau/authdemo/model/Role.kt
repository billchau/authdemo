package com.billchau.authdemo.model

import javax.persistence.*

@Entity
@Table(name = "ROLE")
class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int = 0

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    var name: EnumRole = EnumRole.ROLE_NULL
}
/*
INSERT INTO ROLE VALUES(1, 'ROLE_ADMIN');
INSERT INTO ROLE  VALUES(2, 'ROLE_USER');
INSERT INTO ROLE  VALUES(3, 'ROLE_MODERATOR');
 */