package com.learning.app.domain.dashboard

import com.learning.app.core.network.NetworkResult
import kotlinx.coroutines.flow.Flow

class GetCoursesUseCase(
    private val courseRepository: CourseRepository
) {
    fun getCoursesFlow(): Flow<List<Course>> {
        return courseRepository.getCourses()
    }

    suspend fun refresh(): NetworkResult<Unit> {
        return courseRepository.refreshCourses()
    }
}
