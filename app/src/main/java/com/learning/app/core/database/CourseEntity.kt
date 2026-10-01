package com.learning.app.core.database

import androidx.room.Entity

@Entity(tableName = "courses", primaryKeys = ["id", "userEmail"])
data class CourseEntity(
    val id: Int,
    val userEmail: String,
    val userId: String,
    val title: String,
    val instructor: String,
    val progress: Int,
    val lessonsCount: Int,
    val isPendingSync: Boolean = false
)
