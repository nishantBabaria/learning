package com.learning.app.data.dashboard.remote

import com.learning.app.data.dashboard.dto.CourseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface CourseApiService {
    @GET("courses")
    suspend fun getCourses(@Query("userId") userId: String? = null): List<CourseDto>

    @GET("courses/{id}")
    suspend fun getCourseDetail(@Path("id") courseId: Int): CourseDto

    @PUT("courses/{id}")
    suspend fun updateCourseDetail(
        @Path("id") courseId: Int,
        @Body course: CourseDto
    ): CourseDto
}
