package com.learning.app.data.dashboard.repository

import com.learning.app.core.database.CourseDao
import com.learning.app.core.logging.AppLogger
import com.learning.app.core.network.ApiErrorMapper
import com.learning.app.core.network.NetworkResult
import com.learning.app.core.security.SecurePreferencesManager
import com.learning.app.data.dashboard.mapper.CourseDataMapper.toCourseDto
import com.learning.app.data.dashboard.mapper.CourseDataMapper.toCourseEntity
import com.learning.app.data.dashboard.mapper.CourseDataMapper.toDomainCourse
import com.learning.app.data.dashboard.mapper.CourseDataMapper.toLessonEntities
import com.learning.app.data.dashboard.remote.CourseApiService
import com.learning.app.domain.dashboard.Course
import com.learning.app.domain.dashboard.CourseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CourseRepositoryImpl(
    private val courseDao: CourseDao,
    private val apiService: CourseApiService,
    private val securePrefs: SecurePreferencesManager
) : CourseRepository {

    private val currentUserEmail: String
        get() = securePrefs.getUserEmail() ?: ""

    override fun getCourses(): Flow<List<Course>> {
        return courseDao.getCoursesWithLessonsFlow(userEmail = currentUserEmail).map { entities ->
            entities.map { item -> item.toDomainCourse() }
        }
    }

    override fun getCourseDetails(courseId: Int): Flow<Course?> {
        return courseDao.getCourseWithLessonsFlow(courseId = courseId, userEmail = currentUserEmail).map { item ->
            item?.toDomainCourse()
        }
    }

    override suspend fun refreshCourses(): NetworkResult<Unit> {
        val email = currentUserEmail
        syncPendingOfflineChanges(email = email)

        return try {
            val remoteCourses = apiService.getCourses()

            val courseEntities = remoteCourses.mapIndexed { index, dto ->
                dto.toCourseEntity(userEmail = email, fallbackIndex = index + 1)
            }
            val lessonEntities = remoteCourses.flatMapIndexed { index, dto ->
                dto.toLessonEntities(userEmail = email, fallbackIndex = index + 1)
            }

            courseDao.insertCourses(courses = courseEntities)
            courseDao.insertLessons(lessons = lessonEntities)

            NetworkResult.Success(data = Unit)
        } catch (e: Exception) {
            AppLogger.e(TAG, "Failed to refresh courses from remote API", e)
            val errorMessage = ApiErrorMapper.mapThrowableToErrorMessage(throwable = e)
            NetworkResult.Error(message = errorMessage, cause = e)
        }
    }

    private suspend fun syncPendingOfflineChanges(email: String) {
        try {
            val pendingCourses = courseDao.getPendingSyncCourses(userEmail = email)
            for (pendingCourse in pendingCourses) {
                val courseWithLessons = courseDao.getCourseWithLessonsOnce(courseId = pendingCourse.id, userEmail = email) ?: continue
                val updatedDto = courseWithLessons.toCourseDto()
                apiService.updateCourseDetail(courseId = pendingCourse.id, course = updatedDto)
                courseDao.updatePendingSyncStatus(courseId = pendingCourse.id, userEmail = email, isPendingSync = false)
            }
        } catch (e: Exception) {
            AppLogger.e(TAG, "Offline sync failed gracefully, will retry on next reconnect", e)
        }
    }

    override suspend fun toggleLessonCompletion(courseId: Int, lessonId: Int, isCompleted: Boolean) {
        val email = currentUserEmail

        courseDao.updateLessonAndRecalculateProgress(
            lessonId = lessonId,
            courseId = courseId,
            userEmail = email,
            isCompleted = isCompleted,
            markPendingSync = true
        )

        try {
            val courseWithLessons = courseDao.getCourseWithLessonsOnce(courseId = courseId, userEmail = email)
            if (courseWithLessons != null) {
                val updatedDto = courseWithLessons.toCourseDto()
                apiService.updateCourseDetail(courseId = courseId, course = updatedDto)
                courseDao.updatePendingSyncStatus(courseId = courseId, userEmail = email, isPendingSync = false)
            }
        } catch (e: Exception) {
            AppLogger.e(TAG, "Direct network sync failed, marked as pending in Room DB", e)
        }
    }

    private companion object {
        private const val TAG = "CourseRepository"
    }
}
