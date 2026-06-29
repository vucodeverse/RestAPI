package com.phongvu.restapi.configuration;

import com.phongvu.restapi.dto.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Collections;

@Component
@RequiredArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    // LUA script for fixed window rate limiting (5 requests per 60 seconds)
    private static final String LUA_SCRIPT = 
        "local current = redis.call('get', KEYS[1]) " +
        "if current and tonumber(current) >= tonumber(ARGV[1]) then " +
        "    return tonumber(current) " +
        "end " +
        "current = redis.call('incr', KEYS[1]) " +
        "if tonumber(current) == 1 then " +
        "    redis.call('expire', KEYS[1], ARGV[2]) " +
        "end " +
        "return tonumber(current)";

    private final DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(LUA_SCRIPT, Long.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!request.getRequestURI().contains("/api/v1/identity/authenticate")) {
            return true;
        }

        String clientIp = request.getRemoteAddr();
        String key = "ratelimit:auth:" + clientIp;
        
        // Allowed: 5, Window: 60 seconds
        Long currentCount = redisTemplate.execute(redisScript, Collections.singletonList(key), "5", "60");
        
        if (currentCount != null && currentCount >= 5) {
            response.setStatus(429);
            response.setContentType("application/json;charset=UTF-8");
            ApiResponse<Void> apiResponse = ApiResponse.error(429, "Too Many Requests. Vui lòng thử lại sau.");
            response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
            return false;
        }
        return true;
    }
}
