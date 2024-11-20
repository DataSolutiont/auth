package com.mreblan.auth.redis;

import org.springframework.data.redis.core.RedisHash;

@RedisHash(timeToLive = 600L)
public class RevokedToken {
    private String token;
}
