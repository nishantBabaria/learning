package com.learning.app.domain.dashboard

import com.learning.app.core.network.NetworkResult
import kotlinx.coroutines.flow.Flow

interface CourseRepository {
    fun getCourses(): Flow<List<Course>>
    fun getCourseDetails(courseId: Int): Flow<Course?>
    suspend fun refreshCourses(): NetworkResult<Unit>
    suspend fun toggleLessonCompletion(courseId: Int, lessonId: Int, isCompleted: Boolean)
}
