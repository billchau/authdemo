package com.billchau.authdemo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.runApplication

@SpringBootApplication
class AuthdemoApplication

fun main(args: Array<String>) {
	runApplication<AuthdemoApplication>(*args)
}

