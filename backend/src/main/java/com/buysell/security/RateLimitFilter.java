package com.buysell.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final StringRedisTemplate redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
            
        String clientIp = request.getRemoteAddr();
        String path = request.getRequestURI();
        
        long limit = 100;
        if (path.contains("/api/v1/auth/login")) limit = 5;
        else if (path.contains("/api/v1/auth/refresh")) limit = 10;
        else if (path.contains("/api/v1/media")) limit = 30;
        
        String key = "ratelimit:" + clientIp + ":" + (limit == 100 ? "global" : path);
        
        Long count = redisTemplate.opsForValue().increment(key);
        if (count == null) count = 1L;
        
        if (count == 1) {
            redisTemplate.expire(key, Duration.ofMinutes(1));
        }
        
        if (count > limit) {
            response.setStatus(429); // Too Many Requests
            response.getWriter().write("Rate limit exceeded.");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
