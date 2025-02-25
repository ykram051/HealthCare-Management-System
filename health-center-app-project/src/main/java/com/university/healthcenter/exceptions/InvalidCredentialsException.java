package com.university.healthcenter.exceptions;


public class InvalidCredentialsException extends RuntimeException {
    // Default constructor
    public InvalidCredentialsException() {
        super("Invalid credentials provided.");
    }

    // Constructor with custom message
    public InvalidCredentialsException(String message) {
        super(message);
    }

    // Constructor with custom message and cause
    public InvalidCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }
}


