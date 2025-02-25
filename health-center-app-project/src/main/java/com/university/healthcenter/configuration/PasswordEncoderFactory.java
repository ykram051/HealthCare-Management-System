package com.university.healthcenter.configuration;

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncoderFactory {
    
    public PasswordEncoder createPasswordEncoder(String type) {
        return switch (type.toLowerCase()) {
            case "bcrypt" -> new BCryptPasswordEncoder();
            case "argon2" -> Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
            default -> throw new IllegalArgumentException("Unsupported encoder type: " + type);
        };
    }
}
