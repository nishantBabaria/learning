package com.learning.app.domain.details

import com.learning.app.domain.dashboard.CourseRepository

class ToggleLessonCompletionUseCase(
    private val courseRepository: CourseRepository
) {
    suspend operator fun invoke(courseId: Int, lessonId: Int, isCompleted: Boolean) {
        courseRepository.toggleLessonCompletion(
            courseId = courseId,
            lessonId = lessonId,
            isCompleted = isCompleted
        )
    }
}
