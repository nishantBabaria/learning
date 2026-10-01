package com.learning.app.domain.dashboard

data class Lesson(
    val id: Int,
    val courseId: Int,
    val title: String,
    val isCompleted: Boolean
)
