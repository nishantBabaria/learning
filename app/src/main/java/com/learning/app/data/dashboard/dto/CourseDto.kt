package com.learning.app.data.dashboard.dto

import com.google.gson.annotations.SerializedName

data class CourseDto(
    @SerializedName("id") val rawId: Any? = null,
    @SerializedName("userId") val rawUserId: Any? = null,
    @SerializedName("title") val rawTitle: String? = null,
    @SerializedName("instructor") val rawInstructor: String? = null,
    @SerializedName("progress") val rawProgress: Any? = null,
    @SerializedName("lessonsCount") val rawLessonsCount: Any? = null,
    @SerializedName("lessonList") val lessonList: List<LessonDto> = emptyList()
) {
    fun parseId(fallbackIndex: Int = 1): Int {
        return try {
            when (rawId) {
                is Number -> rawId.toInt()
                is String -> rawId.filter { it.isDigit() }.toIntOrNull() ?: fallbackIndex
                else -> fallbackIndex
            }
        } catch (_: Exception) { fallbackIndex }
    }

    val id: Int
        get() = parseId(1)

    val userId: String
        get() = rawUserId?.toString() ?: ""

    val title: String
        get() = rawTitle ?: "Course ${parseId(1)}"

    val instructor: String
        get() = rawInstructor ?: "Instructor"

    val progress: Int
        get() = try {
            when (rawProgress) {
                is Number -> rawProgress.toInt()
                else -> rawProgress?.toString()?.filter { it.isDigit() }?.toIntOrNull() ?: 50
            }
        } catch (_: Exception) { 50 }

    val lessonsCount: Int
        get() = try {
            when (rawLessonsCount) {
                is Number -> rawLessonsCount.toInt()
                else -> rawLessonsCount?.toString()?.filter { it.isDigit() }?.toIntOrNull() ?: lessonList.size.coerceAtLeast(1)
            }
        } catch (_: Exception) { lessonList.size.coerceAtLeast(1) }
}
