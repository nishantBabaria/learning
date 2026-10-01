package com.learning.app.presentation.dashboard.viewmodel

import com.learning.app.domain.dashboard.Course

sealed interface CourseDashboardUiState {
    data object Loading : CourseDashboardUiState
    data class Success(
        val courses: List<Course>,
        val isOffline: Boolean = false,
        val errorMessage: String? = null
    ) : CourseDashboardUiState
    data class Error(val message: String) : CourseDashboardUiState
}
