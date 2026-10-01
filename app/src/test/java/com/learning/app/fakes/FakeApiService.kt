package com.learning.app.fakes

import com.learning.app.data.dashboard.dto.CourseDto
import com.learning.app.data.dashboard.dto.LessonDto
import com.learning.app.data.dashboard.remote.CourseApiService

class FakeApiService : CourseApiService {
    override suspend fun getCourses(userId: String?): List<CourseDto> {
        return listOf(
            CourseDto(
                rawId = 1,
                rawUserId = "usr_1001",
                rawTitle = "Python Programming",
                rawInstructor = "John",
                rawProgress = 0,
                rawLessonsCount = 2,
                lessonList = listOf(
                    LessonDto(rawId = 101, rawCourseId = 1, rawTitle = "Intro", rawIsCompleted = false),
                    LessonDto(rawId = 102, rawCourseId = 1, rawTitle = "Vars", rawIsCompleted = false)
                )
            )
        )
    }

    override suspend fun getCourseDetail(courseId: Int): CourseDto {
        return getCourses(null).first()
    }

    override suspend fun updateCourseDetail(courseId: Int, course: CourseDto): CourseDto {
        return course
    }
}
