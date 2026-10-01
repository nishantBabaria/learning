package com.learning.app

import android.app.Application
import com.learning.app.core.di.AppContainer
import com.learning.app.core.di.DefaultAppContainer

class LearningApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        container = DefaultAppContainer(this)
    }

    companion object {
        lateinit var instance: LearningApp
            private set
    }
}
