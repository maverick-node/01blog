package com.config;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.Exceptions.RateLimitExceededException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    // Excluded endpoints
    private final Set<String> excludedPaths = Set.of("/login", "/logout");

    // Default rate limit per request
    private final int MAX_REQUESTS = 20;        // max requests per window
    private final long WINDOW_MILLIS = 60_000; // 1 minute

    // Map: userKey -> requestKey -> request info
    private final Map<String, Map<String, UserRequestInfo>> requestCounts = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        // Create a unique key per request type (method + path)
        String requestKey = method + ":" + path;

        // Skip excluded paths
        if (excludedPaths.contains(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Only rate limit POST requests (optional)
        if (!"POST".equalsIgnoreCase(method)) {
            filterChain.doFilter(request, response);
            return;
        }

        String userKey = extractKey(request); // JWT or IP
        Map<String, UserRequestInfo> userInfoMap = requestCounts.computeIfAbsent(userKey, k -> new ConcurrentHashMap<>());
        UserRequestInfo info = userInfoMap.computeIfAbsent(requestKey, k -> new UserRequestInfo(0, System.currentTimeMillis()));

        long now = System.currentTimeMillis();

        // Reset counter if window expired
        if (now - info.startTime > WINDOW_MILLIS) {
            info.count = 0;
            info.startTime = now;
        }

        info.count++;
        if (info.count > MAX_REQUESTS) {
            throw new RateLimitExceededException(
                    "Rate limit exceeded. Max " + MAX_REQUESTS + " requests per minute allowed.");

        }

        filterChain.doFilter(request, response);
    }

    private String extractKey(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (var cookie : request.getCookies()) {
                if ("jwt".equals(cookie.getName())) {
                    return cookie.getValue(); // Use JWT as key
                }
            }
        }
        return request.getRemoteAddr(); // fallback: IP
    }

    private static class UserRequestInfo {
        int count;
        long startTime;

        UserRequestInfo(int count, long startTime) {
            this.count = count;
            this.startTime = startTime;
        }
    }
}
