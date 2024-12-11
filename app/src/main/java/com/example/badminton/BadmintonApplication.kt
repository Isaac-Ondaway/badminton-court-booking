package com.example.badminton

import android.app.Application
import com.example.badminton.data.AppContainer
import com.example.badminton.data.AppDataContainer

class BadmintonApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}
