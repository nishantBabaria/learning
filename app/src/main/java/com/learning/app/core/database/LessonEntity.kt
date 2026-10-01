package com.learning.app.core.database

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "lessons",
    primaryKeys = ["id", "userEmail"],
    foreignKeys = [
        ForeignKey(
            entity = CourseEntity::class,
            parentColumns = ["id", "userEmail"],
            childColumns = ["courseId", "userEmail"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class LessonEntity(
    val id: Int,
    val courseId: Int,
    val userEmail: String,
    val title: String,
    val isCompleted: Boolean
)
