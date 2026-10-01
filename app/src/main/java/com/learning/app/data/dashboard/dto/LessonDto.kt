package com.learning.app.data.dashboard.dto

import com.google.gson.annotations.SerializedName

data class LessonDto(
    @SerializedName("id") val rawId: Any? = null,
    @SerializedName("courseId") val rawCourseId: Any? = null,
    @SerializedName("title") val rawTitle: String? = null,
    @SerializedName("isCompleted") val rawIsCompleted: Boolean? = null
) {
    val id: Int
        get() = try {
            when (rawId) {
                is Number -> rawId.toInt()
                else -> rawId?.toString()?.filter { it.isDigit() }?.toIntOrNull() ?: 101
            }
        } catch (_: Exception) { 101 }

    val courseId: Int
        get() = try {
            when (rawCourseId) {
                is Number -> rawCourseId.toInt()
                else -> rawCourseId?.toString()?.filter { it.isDigit() }?.toIntOrNull() ?: 1
            }
        } catch (_: Exception) { 1 }

    val title: String
        get() = rawTitle ?: "Lesson $id"

    val isCompleted: Boolean
        get() = rawIsCompleted ?: false
}
