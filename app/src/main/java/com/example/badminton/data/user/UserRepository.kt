package com.example.badminton.data.user

import com.example.badminton.data.user.User
import com.example.badminton.data.user.UserDao
import org.mindrot.jbcrypt.BCrypt

class UserRepository(private val userDao: UserDao) {

    suspend fun insert(user: User): Result<Unit> {
        return if (userDao.getUserByEmail(user.email) == null) {
            val hashedPassword = BCrypt.hashpw(user.password, BCrypt.gensalt())
            val userWithHashedPassword = user.copy(password = hashedPassword)
            userDao.insert(userWithHashedPassword)
            Result.success(Unit)
        } else {
            Result.failure(Exception("Email already registered"))
        }
    }

    suspend fun authenticate(email: String, password: String): User? {
        val user = userDao.getUserByEmail(email)
        return if (user != null && BCrypt.checkpw(password, user.password)) {
            user
        } else {
            null
        }
    }

    suspend fun getUserDetails(userId: Int): User? {
        return userDao.findUserById(userId)
    }

}