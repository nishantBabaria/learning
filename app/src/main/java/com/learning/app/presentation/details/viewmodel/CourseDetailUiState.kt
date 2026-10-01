package com.learning.app.presentation.details.viewmodel

import com.learning.app.domain.dashboard.Course

sealed interface CourseDetailUiState {
    data object Loading : CourseDetailUiState
    data class Success(val course: Course) : CourseDetailUiState
    data class Error(val message: String) : CourseDetailUiState
}
