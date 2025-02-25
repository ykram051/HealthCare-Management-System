package com.university.healthcenter.utils;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.university.healthcenter.entities.User;
import com.university.healthcenter.exceptions.InvalidRequestException;
import com.university.healthcenter.exceptions.InvalidTokenException;
import com.university.healthcenter.exceptions.UnauthorizedAccessException;
import com.university.healthcenter.models.LoginModel;
import com.university.healthcenter.repository.HealthStaffRepository;
import com.university.healthcenter.repository.StudentRepository;
import com.university.healthcenter.repository.UserRepository;
import com.university.healthcenter.security.TokenStore;

@Component
public class PermissionChecker {

    private final UserRepository userRepository;
    private final TokenStore tokenStore;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private HealthStaffRepository healthStaffRepository;

    public PermissionChecker(UserRepository userRepository, TokenStore tokenStore, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tokenStore = tokenStore;
        this.passwordEncoder = passwordEncoder;
    }

    public String validateCredentials(LoginModel loginModel) throws InvalidRequestException {
        User userFromDb = userRepository.findByEmail(loginModel.getEmail())
                .orElseThrow(() -> new InvalidRequestException("Invalid email or password"));

        if (!passwordEncoder.matches(loginModel.getPassword(), userFromDb.getPassword())) {
            throw new InvalidRequestException("Invalid email or password");
        }

        String token = UUID.randomUUID().toString();
        tokenStore.storeToken(userFromDb.getUserId(), token);

        return token;
    }

    public int getUserIdByToken(String token) throws InvalidTokenException {
        return tokenStore.getUseridByToken(token);
    }

    public boolean isAuthorized(String token) {
        try {
            int userId = getUserIdByToken(token);
            return userId != -1;
        } catch (InvalidTokenException e) {
            return false;
        }
    }

    public boolean isStudent(String token) {
        try {
            int userId = getUserIdByToken(token);
            return studentRepository.findById(userId).isPresent();
        } catch (InvalidTokenException e) {
            return false;
        }
    }

    public boolean isHealthStaff(String token) {
        try {
            int userId = getUserIdByToken(token);
            return healthStaffRepository.findById(userId).isPresent();
        } catch (InvalidTokenException e) {
            return false;
        }
    }

    // Validate that the user is authorized to access a student's data
    public void validateStudentAccess(String token, int studentId) {
        try {
            int userId = getUserIdByToken(token);
            if (userId != studentId || !isStudent(token)) {
                throw new UnauthorizedAccessException("Unauthorized access to student data");
            }
        } catch (InvalidTokenException e) {
            throw new UnauthorizedAccessException("Invalid token");
        }
    }

    // Validate that the user is authorized to access a health staff's data
    public void validateHealthStaffAccess(String token, int healthStaffId) {
        try {
            int userId = getUserIdByToken(token);
            if (userId != healthStaffId || !isHealthStaff(token)) {
                throw new UnauthorizedAccessException("Unauthorized access to health staff data");
            }
        } catch (InvalidTokenException e) {
            throw new UnauthorizedAccessException("Invalid token");
        }
    }
}
