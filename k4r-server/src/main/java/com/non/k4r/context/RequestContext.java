package com.non.k4r.context;

import com.non.k4r.domain.AccessTokenBody;

import java.util.Optional;

public class RequestContext {
    private static final ThreadLocal<AccessTokenBody> ACCESS_TOKEN_BODY_HOLDER = new ThreadLocal<>();

    public static void setAccessTokenBody(AccessTokenBody body) {
        ACCESS_TOKEN_BODY_HOLDER.set(body);
    }

    public static AccessTokenBody getAccessTokenBody() {
        return ACCESS_TOKEN_BODY_HOLDER.get();
    }

    public static void clearAccessTokenBody() {
        ACCESS_TOKEN_BODY_HOLDER.remove();
    }

    public static Long getRequestUserId() {
        return Optional
                .ofNullable(getAccessTokenBody())
                .map(AccessTokenBody::getUserId)
                .orElse(null);
    }
}
