package com.example.badminton.ui.auth

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.badminton.data.user.User
import com.example.badminton.data.user.UserRepository
import com.example.badminton.ui.SessionManager
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class UserViewModel(private val userRepository: UserRepository,  context: Context) : ViewModel() {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    fun register(username: String, password: String, confirmPassword: String, email: String, onResult: (Result<Unit>) -> Unit) {
        if (password != confirmPassword) {
            onResult(Result.failure(Exception("Passwords do not match")))
            return
        }
        viewModelScope.launch {
            val user = User(userName = username, password = password, email = email)
            val result = userRepository.insert(user)
            onResult(result)
        }
    }



    fun login(email: String, password: String, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val user = userRepository.authenticate(email, password)
            if (user != null) {
                storeUserInfo(user)
                // Log shared preferences after storing user info
                val allEntries = sharedPreferences.all
                Log.d("UserViewModel", "Shared preferences after login: $allEntries")
                SessionManager.login() // Notify login
                onResult(Result.success(Unit))
            } else {
                onResult(Result.failure(Exception("Invalid email or password")))
            }
        }
    }

    private fun storeUserInfo(user: User) {
        with(sharedPreferences.edit()) {
            putString("user_id", user.userID.toString())
            putString("user_email", user.email)
            putString("user_name", user.userName)
            apply()
        }
    }

    fun logout() {
        // Log shared preferences before clearing them
        SessionManager.logout() // Notify logout
        var allEntries = sharedPreferences.all
        Log.d("UserViewModel", "Shared preferences before logout: $allEntries")

        with(sharedPreferences.edit()) {
            clear()
            apply()
        }

        // Log shared preferences after clearing them
        allEntries = sharedPreferences.all
        Log.d("UserViewModel", "Shared preferences after logout: $allEntries")
    }

    fun getUserId(): String? {
        return sharedPreferences.getString("user_id", null)
    }

    fun getUserEmail(): String? {
        return sharedPreferences.getString("user_email", null)
    }

    fun getUserName(): String? {
        return sharedPreferences.getString("user_name", null)
    }

    fun getUserDetails(userId: Int): User? {
        return runBlocking {
            userRepository.getUserDetails(userId)
        }
    }
}
