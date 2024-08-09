package com.non.k4r.manager;

import com.non.k4r.domain.AccessTokenBody;
import jakarta.annotation.Resource;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Component
public class AccessTokenManager {
    @Resource
    private RedissonClient redissonClient;

    private RMapCache<String, AccessTokenBody> MAP;

    private RMapCache<String, AccessTokenBody> map() {
        if (MAP == null) {
            MAP = redissonClient.getMapCache("auth:access_token");
        }
        return MAP;
    }


    public String generateAndStoreAccessToken(Long userId) {
        String token = UUID.randomUUID().toString().replace("-", "");
        RMapCache<String, AccessTokenBody> map = map();

        AccessTokenBody body = new AccessTokenBody();
        body
                .setToken(token)
                .setUserId(userId);
        map.put(token, body, 72, TimeUnit.HOURS);
        return token;
    }


    public AccessTokenBody getTokenBody(String accessToken) {
        RMapCache<String, AccessTokenBody> map = map();
        return map.get(accessToken);
    }

    // TODO 监听所有导致AccessTokenBody失效的事件

    // TODO 监听所有导致AccessToken失效的事件
}
