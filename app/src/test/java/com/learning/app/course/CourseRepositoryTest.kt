package com.learning.app.course

import com.learning.app.core.database.CourseEntity
import com.learning.app.core.database.LessonEntity
import com.learning.app.core.network.NetworkResult
import com.learning.app.data.dashboard.repository.CourseRepositoryImpl
import com.learning.app.fakes.FakeApiService
import com.learning.app.fakes.FakeCourseDao
import com.learning.app.fakes.FakeSecurePrefs
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CourseRepositoryTest {

    private lateinit var fakeDao: FakeCourseDao
    private lateinit var fakeApiService: FakeApiService
    private lateinit var fakeSecurePrefs: FakeSecurePrefs
    private lateinit var repository: CourseRepositoryImpl

    @Before
    fun setUp() {
        fakeDao = FakeCourseDao()
        fakeApiService = FakeApiService()
        fakeSecurePrefs = FakeSecurePrefs()
        repository = CourseRepositoryImpl(fakeDao, fakeApiService, fakeSecurePrefs)
    }

    @Test
    fun refreshCourses_fetchesFromRemoteAndSyncsToLocalDatabase() = runBlocking {
        val result = repository.refreshCourses()

        assertTrue(result is NetworkResult.Success)
        assertEquals(1, fakeDao.courses.size)
        assertEquals("Python Programming", fakeDao.courses[0].title)
        assertEquals(2, fakeDao.lessons.size)
    }

    @Test
    fun toggleLessonCompletion_recalculatesCourseProgressAndReflectsOnDashboard() = runBlocking {
        fakeDao.courses.add(CourseEntity(1, "test@learning.com", "usr_1001", "Python Programming", "John", 0, 2))
        fakeDao.lessons.add(LessonEntity(101, 1, "test@learning.com", "Intro", false))
        fakeDao.lessons.add(LessonEntity(102, 1, "test@learning.com", "Vars", false))

        repository.toggleLessonCompletion(courseId = 1, lessonId = 101, isCompleted = true)

        assertEquals(50, fakeDao.courses[0].progress)
        assertTrue(fakeDao.lessons.first { it.id == 101 }.isCompleted)
    }
}
