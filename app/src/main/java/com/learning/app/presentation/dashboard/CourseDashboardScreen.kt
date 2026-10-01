package com.learning.app.presentation.dashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.learning.app.presentation.dashboard.components.CourseDashboardErrorContent
import com.learning.app.presentation.dashboard.components.CourseDashboardLoadingContent
import com.learning.app.presentation.dashboard.components.CourseDashboardSuccessContent
import com.learning.app.presentation.dashboard.components.CourseDashboardTopBar
import com.learning.app.presentation.dashboard.components.ErrorPlaceHolder
import com.learning.app.presentation.dashboard.components.ProfileBottomSheet
import com.learning.app.presentation.dashboard.viewmodel.CourseDashboardUiState
import com.learning.app.presentation.dashboard.viewmodel.CourseDashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDashboardScreen(
    viewModel: CourseDashboardViewModel,
    onLogoutClick: () -> Unit,
    onCourseClick: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showProfileSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    if (showProfileSheet) {
        ProfileBottomSheet(
            name = viewModel.userName,
            email = viewModel.userEmail,
            sheetState = sheetState,
            onDismiss = { showProfileSheet = false },
            onLogoutClick = {
                showProfileSheet = false
                viewModel.logout(onLogoutSuccess = onLogoutClick)
            }
        )
    }

    Scaffold(
        topBar = {
            CourseDashboardTopBar(
                userName = viewModel.userName,
                onProfileClick = { showProfileSheet = true }
            )
        },
        bottomBar = {
            val state = uiState
            if (state is CourseDashboardUiState.Success && state.isOffline) {
                state.errorMessage?.let { bannerText ->
                    ErrorPlaceHolder(message = bannerText)
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val state = uiState) {
                is CourseDashboardUiState.Loading -> CourseDashboardLoadingContent()
                is CourseDashboardUiState.Error -> CourseDashboardErrorContent(
                    message = state.message,
                    onRetry = viewModel::loadCourses
                )
                is CourseDashboardUiState.Success -> CourseDashboardSuccessContent(
                    courses = state.courses,
                    isLoading = false,
                    onRefresh = viewModel::loadCourses,
                    onCourseClick = onCourseClick
                )
            }
        }
    }
}