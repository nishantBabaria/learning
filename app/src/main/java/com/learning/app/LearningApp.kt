package com.learning.app

import android.app.Application
import com.learning.app.core.di.AppContainer
import com.learning.app.core.di.DefaultAppContainer

class LearningApp : Application() {

    val container: AppContainer by lazy {
        DefaultAppContainer(this)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: LearningApp
            private set
    }
}
