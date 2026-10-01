package com.learning.app.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
abstract class CourseDao {

    @Transaction
    @Query("SELECT * FROM courses WHERE userEmail = :userEmail")
    abstract fun getCoursesWithLessonsFlow(userEmail: String): Flow<List<CourseWithLessons>>

    @Transaction
    @Query("SELECT * FROM courses WHERE id = :courseId AND userEmail = :userEmail")
    abstract fun getCourseWithLessonsFlow(courseId: Int, userEmail: String): Flow<CourseWithLessons?>

    @Transaction
    @Query("SELECT * FROM courses WHERE id = :courseId AND userEmail = :userEmail")
    abstract suspend fun getCourseWithLessonsOnce(courseId: Int, userEmail: String): CourseWithLessons?

    @Query("SELECT * FROM courses WHERE isPendingSync = 1 AND userEmail = :userEmail")
    abstract suspend fun getPendingSyncCourses(userEmail: String): List<CourseEntity>

    @Query("UPDATE courses SET isPendingSync = :isPendingSync WHERE id = :courseId AND userEmail = :userEmail")
    abstract suspend fun updatePendingSyncStatus(courseId: Int, userEmail: String, isPendingSync: Boolean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertCourses(courses: List<CourseEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertLessons(lessons: List<LessonEntity>)

    @Query("UPDATE lessons SET isCompleted = :isCompleted WHERE id = :lessonId AND userEmail = :userEmail")
    abstract suspend fun updateLessonCompletion(lessonId: Int, userEmail: String, isCompleted: Boolean)

    @Query("SELECT * FROM lessons WHERE courseId = :courseId AND userEmail = :userEmail")
    abstract suspend fun getLessonsForCourse(courseId: Int, userEmail: String): List<LessonEntity>

    @Query("UPDATE courses SET progress = :progress WHERE id = :courseId AND userEmail = :userEmail")
    abstract suspend fun updateCourseProgress(courseId: Int, userEmail: String, progress: Int)

    @Transaction
    open suspend fun updateLessonAndRecalculateProgress(
        lessonId: Int,
        courseId: Int,
        userEmail: String,
        isCompleted: Boolean,
        markPendingSync: Boolean = true
    ): Boolean {
        updateLessonCompletion(lessonId = lessonId, userEmail = userEmail, isCompleted = isCompleted)
        val lessons = getLessonsForCourse(courseId = courseId, userEmail = userEmail)
        if (lessons.isNotEmpty()) {
            val completedCount = lessons.count { it.isCompleted }
            val newProgress = (completedCount * 100) / lessons.size
            updateCourseProgress(courseId = courseId, userEmail = userEmail, progress = newProgress)
            if (markPendingSync) {
                updatePendingSyncStatus(courseId = courseId, userEmail = userEmail, isPendingSync = true)
            }
        }
        return true
    }
}
