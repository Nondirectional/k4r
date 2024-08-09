package com.non.k4r.module.auth.controller;

import com.non.k4r.context.RequestContext;
import com.non.k4r.core.entity.Users;
import com.non.k4r.domain.AccessTokenBody;
import com.non.k4r.framework.commons.Result;
import com.non.k4r.framework.constant.CustomException;
import com.non.k4r.framework.constant.ErrorCodes;
import com.non.k4r.module.auth.domain.AuthenticationSession;
import com.non.k4r.module.auth.domain.SignUpParameters;
import com.non.k4r.module.auth.domain.dto.SignInParameters;
import com.non.k4r.module.auth.service.AuthenticationService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 身份验证
 */
@RestController
public class AuthenticationController {
    @Resource
    private AuthenticationService authenticationService;

    /**
     * 登录
     *
     * @param dto 请求参数
     */
    @PostMapping("/auth/authentication/no-auth/sign-in")
    public Result<String> login(@RequestBody @Validated SignInParameters dto) {
        String accessToken = authenticationService.login(dto.getIdentifier(), dto.getPassword());
        return Result.success(accessToken);
    }

    /**
     * 注册
     *
     * @param dto 请求参数
     */
    @PostMapping("/auth/authentication/no-auth/sign-up")
    public Result<String> signUp(@RequestBody @Validated SignUpParameters dto) {
        String accessToken = authenticationService.signUp(dto);
        return Result.success(accessToken);
    }

    @GetMapping("/auth/authentication/session")
    public Result<AuthenticationSession> getSession() {
        AccessTokenBody tokenBody = RequestContext.getAccessTokenBody();
        Users currentUser = Users
                .create()
                .setId(tokenBody.getUserId())
                .oneOpt()
                .orElseThrow(() -> new CustomException(ErrorCodes.UNKNOWN_ERROR, "Current user not found"));
        AuthenticationSession session = new AuthenticationSession();
        session
                .setId(tokenBody.getUserId())
                .setToken(tokenBody.getToken())
                .setUsername(currentUser.getUsername())
                .setNickname(currentUser.getNickname());
        return Result.success(session);
    }
}

