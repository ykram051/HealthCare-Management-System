package com.university.healthcenter.security;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.university.healthcenter.utils.PermissionChecker;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
@Component
public class TokenInterceptor implements HandlerInterceptor {

    private final PermissionChecker permissionChecker;

    public TokenInterceptor(PermissionChecker permissionChecker) {
        this.permissionChecker = permissionChecker;
    }@Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("Intercepting request: " + request.getRequestURI());
        System.out.println("HTTP Method: " + request.getMethod());
    
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            System.out.println("Preflight request, allowing.");
            return true; // Allow preflight requests for CORS
        }
    
        String uri = request.getRequestURI();
        System.out.println("Request URI: " + uri);
    
        // Allow public access to login and register endpoints
        if (uri.startsWith("/security/auth/login") || uri.startsWith("/security/auth/register")) {
            System.out.println("Public endpoint, no token required.");
            return true; // No token required for login or register endpoints
        }
    
        // For all other endpoints, validate the token
        String token = request.getHeader("Authorization");
        System.out.println("Authorization header: " + token);
    
        if (token == null || !permissionChecker.isAuthorized(token)) {
            System.out.println("Token is invalid or missing.");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: Invalid or missing token");
            return false;
        }
    
        System.out.println("Token is valid, proceeding.");
        return true; // Token is valid
    }
    
    
}