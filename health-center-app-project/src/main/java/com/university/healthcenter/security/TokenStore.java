package com.university.healthcenter.security;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.university.healthcenter.exceptions.InvalidTokenException;
@Component
public class TokenStore {
    private final Map<String, Integer> tokenMap = new ConcurrentHashMap<>();

    public void storeToken(Integer userId, String token) {
        tokenMap.put(token, userId);
        System.out.println("Token stored: " + token + " for UserID: " + userId);
    }

    public Integer getUseridByToken(String token) throws InvalidTokenException {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        if (!tokenMap.containsKey(token)) {
            throw new InvalidTokenException("Invalid token: " + token);
        }

        return tokenMap.get(token);
    }

    public void removeToken(String token) throws InvalidTokenException {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        if (!tokenMap.containsKey(token)) {
            throw new InvalidTokenException("Token not found: " + token);
        }

        tokenMap.remove(token);
        System.out.println("Token removed: " + token);
    }
}
