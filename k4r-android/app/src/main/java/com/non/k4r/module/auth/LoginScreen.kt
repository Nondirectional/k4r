package com.non.k4r.module.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.non.k4r.ui.theme.AppTheme

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current

    // 登录成功后的处理
    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) {
            onLoginSuccess()
        }
    }

    // 显示错误信息
    uiState.errorMessage?.let { errorMessage ->
        LaunchedEffect(errorMessage) {
            // 可以在这里显示Snackbar或Toast
        }
    }

    if (uiState.isLoggedIn && uiState.user != null) {
        // 已登录状态显示用户信息
        UserInfoScreen(
            user = uiState.user!!,
            onLogout = viewModel::logout,
            onTestToken = viewModel::testToken,
            isLoading = uiState.isLoading,
            modifier = modifier
        )
    } else {
        // 未登录状态显示登录表单
        LoginForm(
            username = uiState.username,
            password = uiState.password,
            isLoading = uiState.isLoading,
            errorMessage = uiState.errorMessage,
            onUsernameChange = viewModel::updateUsername,
            onPasswordChange = viewModel::updatePassword,
            onLogin = viewModel::login,
            onClearError = viewModel::clearError,
            modifier = modifier
        )
    }
}

@Composable
private fun LoginForm(
    username: String,
    password: String,
    isLoading: Boolean,
    errorMessage: String?,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLogin: () -> Unit,
    onClearError: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    // 使用Surface来确保背景颜色正确适配主题
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 标题
            Text(
                text = "K4R 登录",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 32.dp)
            )

        // 用户名输入框
        OutlinedTextField(
            value = username,
            onValueChange = {
                onUsernameChange(it)
                if (errorMessage != null) onClearError()
            },
            label = { Text("用户名") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            singleLine = true,
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 密码输入框
        OutlinedTextField(
            value = password,
            onValueChange = {
                onPasswordChange(it)
                if (errorMessage != null) onClearError()
            },
            label = { Text("密码") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { 
                    focusManager.clearFocus()
                    onLogin()
                }
            ),
            singleLine = true,
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

            // 登录按钮
            Button(
                onClick = onLogin,
                enabled = !isLoading && username.isNotBlank() && password.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text("登录")
            }

        // 错误信息
        errorMessage?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
        }
    }
}

@Composable
private fun UserInfoScreen(
    user: com.non.k4r.core.network.dto.UserResponse,
    onLogout: () -> Unit,
    onTestToken: () -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    // 使用Surface来确保背景颜色正确适配主题
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 用户信息卡片
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "用户信息",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "用户名: ${user.username}",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "邮箱: ${user.email}",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    user.full_name?.let { fullName ->
                        Text(
                            text = "全名: $fullName",
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    
                    Text(
                        text = "状态: ${if (user.is_active) "活跃" else "已禁用"}",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "用户ID: ${user.id}",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 测试Token按钮
            Button(
                onClick = onTestToken,
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text("测试Token")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 登出按钮
            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("登出")
            }
        }
    }
}

// 浅色模式预览
@Preview(name = "浅色模式", showBackground = true)
@Composable
fun LoginFormLightPreview() {
    AppTheme(darkTheme = false) {
        LoginForm(
            username = "testuser",
            password = "password",
            isLoading = false,
            errorMessage = null,
            onUsernameChange = {},
            onPasswordChange = {},
            onLogin = {},
            onClearError = {}
        )
    }
}

// 深色模式预览
@Preview(name = "深色模式", showBackground = true)
@Composable
fun LoginFormDarkPreview() {
    AppTheme(darkTheme = true) {
        LoginForm(
            username = "testuser",
            password = "password",
            isLoading = false,
            errorMessage = null,
            onUsernameChange = {},
            onPasswordChange = {},
            onLogin = {},
            onClearError = {}
        )
    }
}

// 错误状态预览
@Preview(name = "错误状态", showBackground = true)
@Composable
fun LoginFormErrorPreview() {
    AppTheme(darkTheme = true) {
        LoginForm(
            username = "testuser",
            password = "password",
            isLoading = false,
            errorMessage = "用户名或密码错误，请重试",
            onUsernameChange = {},
            onPasswordChange = {},
            onLogin = {},
            onClearError = {}
        )
    }
}

// 加载状态预览
@Preview(name = "加载状态", showBackground = true)
@Composable
fun LoginFormLoadingPreview() {
    AppTheme(darkTheme = false) {
        LoginForm(
            username = "testuser",
            password = "password",
            isLoading = true,
            errorMessage = null,
            onUsernameChange = {},
            onPasswordChange = {},
            onLogin = {},
            onClearError = {}
        )
    }
} 