package com.learning.app.presentation.details.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learning.app.domain.details.GetCourseDetailUseCase
import com.learning.app.domain.details.ToggleLessonCompletionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CourseDetailViewModel(
    private val courseId: Int,
    private val getCourseDetailUseCase: GetCourseDetailUseCase,
    private val toggleLessonCompletionUseCase: ToggleLessonCompletionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<CourseDetailUiState>(CourseDetailUiState.Loading)
    val uiState: StateFlow<CourseDetailUiState> = _uiState.asStateFlow()

    init {
        observeCourse()
    }

    private fun observeCourse() {
        viewModelScope.launch {
            getCourseDetailUseCase(courseId = courseId).collectLatest { course ->
                if (course != null) {
                    _uiState.value = CourseDetailUiState.Success(course = course)
                } else {
                    _uiState.value = CourseDetailUiState.Error(message = "Course details not found")
                }
            }
        }
    }

    fun toggleLessonCompletion(lessonId: Int, currentStatus: Boolean) {
        viewModelScope.launch {
            toggleLessonCompletionUseCase(
                courseId = courseId,
                lessonId = lessonId,
                isCompleted = !currentStatus
            )
        }
    }
}
