package com.non.k4r.core.network.api

import com.non.k4r.core.network.dto.LoginRequest
import com.non.k4r.core.network.dto.LoginResponse
import com.non.k4r.core.network.dto.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApi {
    
    @POST("api/v1/auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>
    
    @GET("api/v1/users/me")
    suspend fun getCurrentUser(@Header("Authorization") token: String): Response<UserResponse>
    
    @POST("api/v1/auth/test-token")
    suspend fun testToken(@Header("Authorization") token: String): Response<UserResponse>
} 