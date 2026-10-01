package com.learning.app.core.di

import android.content.Context
import com.learning.app.core.database.AppDatabase
import com.learning.app.core.network.NetworkModule
import com.learning.app.core.security.SecurePreferencesManager
import com.learning.app.data.dashboard.remote.CourseApiService
import com.learning.app.data.dashboard.repository.CourseRepositoryImpl
import com.learning.app.data.login.remote.AuthApiService
import com.learning.app.data.login.repository.AuthRepositoryImpl
import com.learning.app.domain.dashboard.CourseRepository
import com.learning.app.domain.dashboard.GetCoursesUseCase
import com.learning.app.domain.details.GetCourseDetailUseCase
import com.learning.app.domain.details.ToggleLessonCompletionUseCase
import com.learning.app.domain.login.AuthRepository
import com.learning.app.domain.login.IsLoggedInUseCase
import com.learning.app.domain.login.LoginUseCase
import com.learning.app.domain.login.LogoutUseCase

interface AppContainer : AuthContainer, CourseContainer {
    val database: AppDatabase
    val securePrefs: SecurePreferencesManager
}

class DefaultAppContainer(private val context: Context) : AppContainer {

    override val database: AppDatabase by lazy {
        AppDatabase.getInstance(context = context)
    }

    override val securePrefs: SecurePreferencesManager by lazy {
        SecurePreferencesManager(context = context)
    }

    private val okHttpClient by lazy {
        NetworkModule.provideOkHttpClient(securePrefs = securePrefs)
    }

    private val retrofit by lazy {
        NetworkModule.provideRetrofit(okHttpClient = okHttpClient)
    }

    private val authApiService: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }

    private val courseApiService: CourseApiService by lazy {
        retrofit.create(CourseApiService::class.java)
    }

    override val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(
            authApiService = authApiService,
            securePrefs = securePrefs
        )
    }

    override val courseRepository: CourseRepository by lazy {
        CourseRepositoryImpl(
            courseDao = database.courseDao(),
            apiService = courseApiService,
            securePrefs = securePrefs
        )
    }

    override val loginUseCase: LoginUseCase by lazy {
        LoginUseCase(authRepository = authRepository)
    }

    override val isLoggedInUseCase: IsLoggedInUseCase by lazy {
        IsLoggedInUseCase(authRepository = authRepository)
    }

    override val logoutUseCase: LogoutUseCase by lazy {
        LogoutUseCase(authRepository = authRepository)
    }

    override val getCoursesUseCase: GetCoursesUseCase by lazy {
        GetCoursesUseCase(courseRepository = courseRepository)
    }

    override val getCourseDetailUseCase: GetCourseDetailUseCase by lazy {
        GetCourseDetailUseCase(courseRepository = courseRepository)
    }

    override val toggleLessonCompletionUseCase: ToggleLessonCompletionUseCase by lazy {
        ToggleLessonCompletionUseCase(courseRepository = courseRepository)
    }
}

interface AuthContainer {
    val authRepository: AuthRepository
    val loginUseCase: LoginUseCase
    val isLoggedInUseCase: IsLoggedInUseCase
    val logoutUseCase: LogoutUseCase
}

interface CourseContainer {
    val courseRepository: CourseRepository
    val getCoursesUseCase: GetCoursesUseCase
    val getCourseDetailUseCase: GetCourseDetailUseCase
    val toggleLessonCompletionUseCase: ToggleLessonCompletionUseCase
}
