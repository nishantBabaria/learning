package com.learning.app.presentation.dashboard.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learning.app.core.network.NetworkResult
import com.learning.app.core.security.SecurePreferencesManager
import com.learning.app.domain.dashboard.GetCoursesUseCase
import com.learning.app.domain.login.LogoutUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CourseDashboardViewModel(
    private val getCoursesUseCase: GetCoursesUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val securePrefs: SecurePreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<CourseDashboardUiState>(CourseDashboardUiState.Loading)
    val uiState: StateFlow<CourseDashboardUiState> = _uiState.asStateFlow()

    val userEmail: String
        get() = securePrefs.getUserEmail() ?: ""

    val userName: String
        get() = userEmail.substringBefore("@").replaceFirstChar { it.uppercase() }

    init {
        loadCourses()
    }

    fun loadCourses() {
        viewModelScope.launch {
            _uiState.value = CourseDashboardUiState.Loading

            val refreshResult = getCoursesUseCase.refresh()

            getCoursesUseCase.getCoursesFlow().collectLatest { courses ->
                if (courses.isNotEmpty()) {
                    val isOffline = refreshResult is NetworkResult.Error
                    val errorMessage = (refreshResult as? NetworkResult.Error)?.message
                    _uiState.value = CourseDashboardUiState.Success(
                        courses = courses,
                        isOffline = isOffline,
                        errorMessage = errorMessage
                    )
                } else {
                    if (refreshResult is NetworkResult.Error) {
                        _uiState.value = CourseDashboardUiState.Error(
                            message = refreshResult.message
                        )
                    } else {
                        _uiState.value = CourseDashboardUiState.Success(courses = emptyList())
                    }
                }
            }
        }
    }

    fun logout(onLogoutSuccess: () -> Unit) {
        logoutUseCase()
        onLogoutSuccess()
    }
}
