package com.learning.app.domain.details

import com.learning.app.domain.dashboard.Course
import com.learning.app.domain.dashboard.CourseRepository
import kotlinx.coroutines.flow.Flow

class GetCourseDetailUseCase(
    private val courseRepository: CourseRepository
) {
    operator fun invoke(courseId: Int): Flow<Course?> {
        return courseRepository.getCourseDetails(courseId = courseId)
    }
}
