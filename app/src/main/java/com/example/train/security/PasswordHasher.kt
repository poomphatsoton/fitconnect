package com.example.train.security

import org.mindrot.jbcrypt.BCrypt

object PasswordHasher {
    fun hash(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }

    fun verify(password: String, storedPassword: String): Boolean {
        return BCrypt.checkpw(password, storedPassword)
    }
}
