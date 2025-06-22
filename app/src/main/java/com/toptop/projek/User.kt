package com.toptop.projek

data class User(
    val username: String? = null,
    val email: String? = null,
    val password: String? = null // Ingat, ini tidak aman!
)