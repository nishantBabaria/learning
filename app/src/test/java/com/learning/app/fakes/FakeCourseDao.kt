package com.learning.app.fakes

import com.learning.app.core.database.CourseDao
import com.learning.app.core.database.CourseEntity
import com.learning.app.core.database.CourseWithLessons
import com.learning.app.core.database.LessonEntity

class FakeCourseDao : CourseDao() {
    val courses = mutableListOf<CourseEntity>()
    val lessons = mutableListOf<LessonEntity>()

    override fun getCoursesWithLessonsFlow(userEmail: String) = error("Not implemented")
    override fun getCourseWithLessonsFlow(courseId: Int, userEmail: String) = error("Not implemented")

    override suspend fun getCourseWithLessonsOnce(courseId: Int, userEmail: String): CourseWithLessons? {
        val course = courses.find { it.id == courseId && it.userEmail == userEmail } ?: return null
        val courseLessons = lessons.filter { it.courseId == courseId && it.userEmail == userEmail }
        return CourseWithLessons(course = course, lessons = courseLessons)
    }

    override suspend fun getPendingSyncCourses(userEmail: String): List<CourseEntity> {
        return courses.filter { it.isPendingSync && it.userEmail == userEmail }
    }

    override suspend fun updatePendingSyncStatus(courseId: Int, userEmail: String, isPendingSync: Boolean) {
        val index = courses.indexOfFirst { it.id == courseId && it.userEmail == userEmail }
        if (index != -1) {
            courses[index] = courses[index].copy(isPendingSync = isPendingSync)
        }
    }

    override suspend fun insertCourses(courses: List<CourseEntity>) {
        this.courses.clear()
        this.courses.addAll(courses)
    }

    override suspend fun insertLessons(lessons: List<LessonEntity>) {
        this.lessons.clear()
        this.lessons.addAll(lessons)
    }

    override suspend fun updateLessonCompletion(lessonId: Int, userEmail: String, isCompleted: Boolean) {
        val index = lessons.indexOfFirst { it.id == lessonId && it.userEmail == userEmail }
        if (index != -1) {
            lessons[index] = lessons[index].copy(isCompleted = isCompleted)
        }
    }

    override suspend fun getLessonsForCourse(courseId: Int, userEmail: String): List<LessonEntity> {
        return lessons.filter { it.courseId == courseId && it.userEmail == userEmail }
    }

    override suspend fun updateCourseProgress(courseId: Int, userEmail: String, progress: Int) {
        val index = courses.indexOfFirst { it.id == courseId && it.userEmail == userEmail }
        if (index != -1) {
            courses[index] = courses[index].copy(progress = progress)
        }
    }
}
