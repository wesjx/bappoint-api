package com.wesleysilva.bappoint.ratelimit;

public interface RateLimiterService {
    boolean tryConsume(String key);
}
