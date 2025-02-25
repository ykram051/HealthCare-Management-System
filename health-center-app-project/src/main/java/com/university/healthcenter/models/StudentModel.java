package com.university.healthcenter.models;

import java.util.List;


public class StudentModel extends UserModel {

    private String dateOfBirth;
    private List<String> allergies;

    // Constructors
    public StudentModel() {}

    public StudentModel(
            int userId,
            String username,
            String email,
            String phoneNumber,
            String address,
            String dateOfBirth,
            List<String> allergies,
            String password) {
        super(userId, username, email, phoneNumber, address, password);
        this.dateOfBirth = dateOfBirth;
        this.allergies = allergies;
    }

    // Getters and Setters
    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public List<String> getAllergies() {
        return allergies;
    }

    public void setAllergies(List<String> allergies) {
        this.allergies = allergies;
    }

}
