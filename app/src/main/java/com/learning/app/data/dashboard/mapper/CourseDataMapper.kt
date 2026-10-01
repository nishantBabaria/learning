package com.learning.app.data.dashboard.mapper

import com.learning.app.core.database.CourseEntity
import com.learning.app.core.database.CourseWithLessons
import com.learning.app.core.database.LessonEntity
import com.learning.app.data.dashboard.dto.CourseDto
import com.learning.app.data.dashboard.dto.LessonDto
import com.learning.app.domain.dashboard.Course
import com.learning.app.domain.dashboard.Lesson

object CourseDataMapper {

    fun CourseWithLessons.toDomainCourse(): Course {
        val userLessons = lessons.filter { it.userEmail == course.userEmail }
        return Course(
            id = course.id,
            title = course.title,
            instructor = course.instructor,
            progress = course.progress,
            lessonsCount = course.lessonsCount,
            lessons = userLessons.map { l ->
                Lesson(
                    id = l.id,
                    courseId = l.courseId,
                    title = l.title,
                    isCompleted = l.isCompleted
                )
            }
        )
    }

    fun CourseDto.toCourseEntity(userEmail: String, fallbackIndex: Int = 1): CourseEntity {
        val courseId = parseId(fallbackIndex)
        return CourseEntity(
            id = courseId,
            userEmail = userEmail,
            userId = userId,
            title = title,
            instructor = instructor,
            progress = progress,
            lessonsCount = lessonsCount,
            isPendingSync = false
        )
    }

    fun CourseDto.toLessonEntities(userEmail: String, fallbackIndex: Int = 1): List<LessonEntity> {
        val parentCourseId = parseId(fallbackIndex)
        return lessonList.mapIndexed { index, dto ->
            val lessonId = if (dto.id <= 0) (parentCourseId * 100 + index + 1) else dto.id
            LessonEntity(
                id = lessonId,
                courseId = parentCourseId,
                userEmail = userEmail,
                title = dto.title,
                isCompleted = dto.isCompleted
            )
        }
    }

    fun CourseWithLessons.toCourseDto(): CourseDto {
        val userLessons = lessons.filter { it.userEmail == course.userEmail }
        return CourseDto(
            rawId = course.id,
            rawUserId = course.userId,
            rawTitle = course.title,
            rawInstructor = course.instructor,
            rawProgress = course.progress,
            rawLessonsCount = course.lessonsCount,
            lessonList = userLessons.map { l ->
                LessonDto(
                    rawId = l.id,
                    rawCourseId = l.courseId,
                    rawTitle = l.title,
                    rawIsCompleted = l.isCompleted
                )
            }
        )
    }
}
