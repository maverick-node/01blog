package com.config;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.Exceptions.BannedUserExceptions;
import com.Exceptions.RateLimitExceededException;
import com.Exceptions.UnauthorizedActionException;
import com.Model.UserStruct;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.services.UserServiceMiddle;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final UserServiceMiddle uStruct;

    public RateLimitFilter(UserServiceMiddle uStruct) {
        this.uStruct = uStruct;
    }

    // Endpoints that are excluded from rate limiting
    private final Set<String> excludedPaths = Set.of("/login", "/logout");

    // Rate limiting settings
    private final int MAX_REQUESTS = 20; // max requests per window
    private final long WINDOW_MILLIS = 60_000; // 1 minute

    // Map: userKey -> requestKey -> request info
    private final Map<String, Map<String, UserRequestInfo>> requestCounts = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();
        System.out.println("RateLimitFilter:========================== ");
        // Skip excluded paths
        if (excludedPaths.contains(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract JWT (from cookie or Authorization header)
        String jwt = getJwt(request);
        UserStruct user = null;

        if (jwt != null && !jwt.isEmpty()) {
            try {
                user = uStruct.getUserFromJwt(jwt);
            } catch (Exception e) {
                // remove cookies
                Cookie cookie = new Cookie("jwt", null);
                cookie.setHttpOnly(true);
                cookie.setSecure(true);
                cookie.setPath("/");
                cookie.setMaxAge(0);
                response.addCookie(cookie);

                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid token");
                return;
            }
        }

        // If user exists and is banned, block request
        if ("POST".equalsIgnoreCase(method) && user != null && user.isBanned()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "You are banned from making requests.");
            return;

        }

        // Rate limiting only for POST requests
        if (!"POST".equalsIgnoreCase(method)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Create a unique key per request type (method + path)
        String requestKey = method + ":" + path;

        // Determine the key to use for rate limiting (JWT if available, else IP)
        String userKey = (jwt != null && !jwt.isEmpty()) ? jwt : request.getRemoteAddr();

        Map<String, UserRequestInfo> userInfoMap = requestCounts.computeIfAbsent(userKey,
                k -> new ConcurrentHashMap<>());
        UserRequestInfo info = userInfoMap.computeIfAbsent(requestKey,
                k -> new UserRequestInfo(0, System.currentTimeMillis()));

        long now = System.currentTimeMillis();

        // Reset counter if window expired
        if (now - info.startTime > WINDOW_MILLIS) {
            info.count = 0;
            info.startTime = now;
        }

        info.count++;
        if (info.count > MAX_REQUESTS) {
            response.sendError(401, "Too many requests. Please try again later.");
            return;
        }

        // Continue the filter chain
        filterChain.doFilter(request, response);
    }

    // Extract JWT from cookie or Authorization header
    private String getJwt(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (var cookie : request.getCookies()) {
                if ("jwt".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    // Inner class to track requests per user and path
    private static class UserRequestInfo {
        int count;
        long startTime;

        UserRequestInfo(int count, long startTime) {
            this.count = count;
            this.startTime = startTime;
        }
    }

  

}
