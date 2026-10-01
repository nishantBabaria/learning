package com.learning.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.learning.app.LearningApp
import com.learning.app.core.di.AppContainer
import com.learning.app.presentation.dashboard.CourseDashboardScreen
import com.learning.app.presentation.dashboard.viewmodel.CourseDashboardViewModel
import com.learning.app.presentation.details.CourseDetailScreen
import com.learning.app.presentation.details.viewmodel.CourseDetailViewModel
import com.learning.app.presentation.login.LoginScreen
import com.learning.app.presentation.login.viewmodel.LoginViewModel

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Dashboard : Screen("dashboard")
    data object CourseDetail : Screen("details/{courseId}") {
        fun createRoute(courseId: Int) = "details/$courseId"
    }
}

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val container = LearningApp.instance.container

    val startDestination = if (container.isLoggedInUseCase()) {
        Screen.Dashboard.route
    } else {
        Screen.Login.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        loginGraph(navController = navController, container = container)
        dashboardGraph(navController = navController, container = container)
        detailsGraph(navController = navController, container = container)
    }
}

private fun NavGraphBuilder.loginGraph(
    navController: NavHostController,
    container: AppContainer
) {
    composable(route = Screen.Login.route) {
        val loginViewModel = remember {
            LoginViewModel(
                loginUseCase = container.loginUseCase
            )
        }
        LoginScreen(
            viewModel = loginViewModel,
            onLoginSuccess = {
                navController.navigate(route = Screen.Dashboard.route) {
                    popUpTo(route = Screen.Login.route) { inclusive = true }
                }
            }
        )
    }
}

private fun NavGraphBuilder.dashboardGraph(
    navController: NavHostController,
    container: AppContainer
) {
    composable(route = Screen.Dashboard.route) {
        val dashboardViewModel = remember {
            CourseDashboardViewModel(
                getCoursesUseCase = container.getCoursesUseCase,
                logoutUseCase = container.logoutUseCase,
                securePrefs = container.securePrefs
            )
        }
        CourseDashboardScreen(
            viewModel = dashboardViewModel,
            onLogoutClick = {
                navController.navigate(route = Screen.Login.route) {
                    popUpTo(route = Screen.Dashboard.route) { inclusive = true }
                }
            },
            onCourseClick = { courseId ->
                navController.navigate(route = Screen.CourseDetail.createRoute(courseId = courseId))
            }
        )
    }
}

private fun NavGraphBuilder.detailsGraph(
    navController: NavHostController,
    container: AppContainer
) {
    composable(
        route = Screen.CourseDetail.route,
        arguments = listOf(navArgument(name = "courseId") { type = NavType.IntType })
    ) { backStackEntry ->
        val courseId = backStackEntry.arguments?.getInt("courseId") ?: 0
        val detailViewModel = remember(courseId) {
            CourseDetailViewModel(
                courseId = courseId,
                getCourseDetailUseCase = container.getCourseDetailUseCase,
                toggleLessonCompletionUseCase = container.toggleLessonCompletionUseCase
            )
        }

        CourseDetailScreen(
            viewModel = detailViewModel,
            onBackClick = { navController.popBackStack() }
        )
    }
}
