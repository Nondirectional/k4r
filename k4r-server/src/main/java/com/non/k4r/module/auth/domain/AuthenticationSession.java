package com.non.k4r.module.auth.domain;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AuthenticationSession {
    private Long id;

    private String username;

    private String nickname;

    private String token;
}
