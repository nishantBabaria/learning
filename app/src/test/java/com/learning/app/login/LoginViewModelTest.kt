package com.learning.app.login

import com.learning.app.domain.login.LoginUseCase
import com.learning.app.fakes.FakeAuthRepository
import com.learning.app.presentation.login.viewmodel.LoginViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var fakeAuthRepository: FakeAuthRepository
    private lateinit var loginUseCase: LoginUseCase
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeAuthRepository = FakeAuthRepository()
        loginUseCase = LoginUseCase(authRepository = fakeAuthRepository)
        viewModel = LoginViewModel(loginUseCase = loginUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun login_invalidEmail_setsEmailError() {
        viewModel.onEmailChanged("invalid-email")
        viewModel.onPasswordChanged("password123")
        viewModel.login()

        assertNotNull(viewModel.uiState.value.emailError)
    }

    @Test
    fun login_shortPassword_setsPasswordError() {
        viewModel.onEmailChanged("user@example.com")
        viewModel.onPasswordChanged("123")
        viewModel.login()

        assertNotNull(viewModel.uiState.value.passwordError)
    }

    @Test
    fun login_validCredentials_authenticatesSuccessfully() {
        viewModel.onEmailChanged("user@example.com")
        viewModel.onPasswordChanged("password123")
        viewModel.login()

        assertTrue(viewModel.uiState.value.isSuccess)
    }
}
