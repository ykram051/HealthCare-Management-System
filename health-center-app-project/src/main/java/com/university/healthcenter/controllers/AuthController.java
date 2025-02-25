package com.university.healthcenter.controllers;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.university.healthcenter.entities.HealthStaff;
import com.university.healthcenter.entities.Student;
import com.university.healthcenter.exceptions.InvalidRequestException;
import com.university.healthcenter.exceptions.InvalidTokenException;
import com.university.healthcenter.models.HealthStaffModel;
import com.university.healthcenter.models.LoginModel;
import com.university.healthcenter.models.StudentModel;
import com.university.healthcenter.repository.HealthStaffRepository;
import com.university.healthcenter.repository.StudentRepository;
import com.university.healthcenter.repository.UserRepository;
import com.university.healthcenter.security.TokenStore;
import com.university.healthcenter.utils.PermissionChecker;

@RestController
@RequestMapping("security/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final TokenStore tokenStore;
    private final PermissionChecker permissionChecker;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final HealthStaffRepository healthStaffRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(TokenStore tokenStore, PermissionChecker permissionChecker,
                          UserRepository userRepository, StudentRepository studentRepository,
                          HealthStaffRepository healthStaffRepository, PasswordEncoder passwordEncoder) {
        this.tokenStore = tokenStore;
        this.permissionChecker = permissionChecker;
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.healthStaffRepository = healthStaffRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginModel user) {
        try {
            // Validate the user credentials and generate a token
            String token = permissionChecker.validateCredentials(user);
    
            // Check if the user exists in the Student table
            Optional<Student> studentOpt = studentRepository.findByEmail(user.getEmail());
            if (studentOpt.isPresent()) {
                Student student = studentOpt.get();
                return ResponseEntity.ok(Map.of(
                    "token", token,
                    "user", student,
                    "role", "student"
                ));
            }
    
            // Check if the user exists in the HealthStaff table
            Optional<HealthStaff> staffOpt = healthStaffRepository.findByEmail(user.getEmail());
            if (staffOpt.isPresent()) {
                HealthStaff staff = staffOpt.get();
                return ResponseEntity.ok(Map.of(
                    "token", token,
                    "user", staff,
                    "role", "health-staff"
                ));
            }
    
            // If the user is not found in either table
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        } catch (InvalidRequestException e) {
            logger.error("Invalid login attempt for email: {}", user.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }
    }
    
    @PostMapping("/register/student")
    public ResponseEntity<String> registerStudent(@RequestBody StudentModel studentModel) {
        if (userRepository.findByEmail(studentModel.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email already exists");
        }
    
        Student student = new Student();
        student.setUsername(studentModel.getUsername());
        student.setEmail(studentModel.getEmail());
        student.setPhoneNumber(studentModel.getPhoneNumber());
        student.setAddress(studentModel.getAddress());
        student.setPassword(passwordEncoder.encode(studentModel.getPassword())); // Use password from model
        student.setDateOfBirth(studentModel.getDateOfBirth());
        student.setAllergies(studentModel.getAllergies());
        studentRepository.save(student);
        return ResponseEntity.status(HttpStatus.CREATED).body("Student registered successfully");
    }
    
    @PostMapping("/register/health-staff")
    public ResponseEntity<String> registerHealthStaff(@RequestBody HealthStaffModel healthStaffModel) {
        if (userRepository.findByEmail(healthStaffModel.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email already exists");
        }
    
        HealthStaff healthStaff = new HealthStaff();
        healthStaff.setUsername(healthStaffModel.getUsername());
        healthStaff.setEmail(healthStaffModel.getEmail());
        healthStaff.setPhoneNumber(healthStaffModel.getPhoneNumber());
        healthStaff.setAddress(healthStaffModel.getAddress());
        healthStaff.setPassword(passwordEncoder.encode(healthStaffModel.getPassword())); // Use password from model
    
        healthStaffRepository.save(healthStaff);
        return ResponseEntity.status(HttpStatus.CREATED).body("Health staff registered successfully");
    }
    


    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        try {
            tokenStore.removeToken(token);
            return ResponseEntity.ok("Logged out");
        } catch (InvalidTokenException e) {
            logger.error("Invalid token", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }
    
}