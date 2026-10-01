package com.learning.app.domain.dashboard

data class Course(
    val id: Int,
    val title: String,
    val instructor: String,
    val progress: Int,
    val lessonsCount: Int,
    val lessons: List<Lesson> = emptyList()
) {
    val isCompleted: Boolean
        get() = progress >= 100
}
