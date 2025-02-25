package com.university.healthcenter.models;

public class HealthStaffModel extends UserModel {

    public HealthStaffModel() {}

    public HealthStaffModel(
            int userId,
            String username,
            String email,
            String phoneNumber,
            String address,
            String password) {
        super(userId, username, email, phoneNumber, address, password);
    }
}
