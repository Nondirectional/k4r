package com.non.k4r.core.auth

import com.non.k4r.core.network.api.AuthApi
import com.non.k4r.core.network.dto.LoginRequest
import com.non.k4r.core.network.dto.UserResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

sealed class AuthResult<out T> {
    data class Success<T>(val data: T) : AuthResult<T>()
    data class Error(val message: String) : AuthResult<Nothing>()
    object Loading : AuthResult<Nothing>()
}

@Singleton
class AuthRepository @Inject constructor(
    private val authApi: AuthApi,
    private val tokenManager: TokenManager
) {

    suspend fun login(username: String, password: String): AuthResult<UserResponse> {
        return try {
            val loginRequest = LoginRequest(username, password)
            val response = authApi.login(loginRequest)
            
            if (response.isSuccessful) {
                val loginResponse = response.body()
                if (loginResponse != null) {
                    // 保存Token
                    tokenManager.saveTokens(
                        loginResponse.access_token,
                        loginResponse.token_type
                    )
                    
                    // 获取用户信息
                    val userResult = getCurrentUser()
                    if (userResult is AuthResult.Success) {
                        AuthResult.Success(userResult.data)
                    } else {
                        AuthResult.Error("登录成功但获取用户信息失败")
                    }
                } else {
                    AuthResult.Error("登录响应为空")
                }
            } else {
                val errorMessage = when (response.code()) {
                    401 -> "用户名或密码错误"
                    400 -> "用户账号已被禁用"
                    else -> "登录失败: ${response.message()}"
                }
                AuthResult.Error(errorMessage)
            }
        } catch (e: Exception) {
            AuthResult.Error("网络错误: ${e.message}")
        }
    }

    suspend fun getCurrentUser(): AuthResult<UserResponse> {
        return try {
            val authHeader = tokenManager.getAuthHeader().first()
            if (authHeader == null) {
                return AuthResult.Error("未登录")
            }

            val response = authApi.getCurrentUser(authHeader)
            if (response.isSuccessful) {
                val user = response.body()
                if (user != null) {
                    AuthResult.Success(user)
                } else {
                    AuthResult.Error("用户信息为空")
                }
            } else {
                when (response.code()) {
                    401 -> {
                        // Token过期，清除本地Token
                        tokenManager.clearTokens()
                        AuthResult.Error("登录已过期，请重新登录")
                    }
                    else -> AuthResult.Error("获取用户信息失败: ${response.message()}")
                }
            }
        } catch (e: Exception) {
            AuthResult.Error("网络错误: ${e.message}")
        }
    }

    suspend fun logout() {
        tokenManager.clearTokens()
    }

    fun isLoggedIn(): Flow<Boolean> {
        return tokenManager.isLoggedIn()
    }

    suspend fun testToken(): AuthResult<UserResponse> {
        return try {
            val authHeader = tokenManager.getAuthHeader().first()
            if (authHeader == null) {
                return AuthResult.Error("未登录")
            }

            val response = authApi.testToken(authHeader)
            if (response.isSuccessful) {
                val user = response.body()
                if (user != null) {
                    AuthResult.Success(user)
                } else {
                    AuthResult.Error("Token验证失败")
                }
            } else {
                when (response.code()) {
                    401 -> {
                        tokenManager.clearTokens()
                        AuthResult.Error("Token已过期")
                    }
                    else -> AuthResult.Error("Token验证失败: ${response.message()}")
                }
            }
        } catch (e: Exception) {
            AuthResult.Error("网络错误: ${e.message}")
        }
    }
} 