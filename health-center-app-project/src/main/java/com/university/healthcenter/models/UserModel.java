package com.university.healthcenter.models;
public class UserModel {

    private int userId;
    private String username;
    private String email;
    private String phoneNumber;
    private String address;
    private String password;

    // Constructors
    public UserModel() {}

    public UserModel(int userId, String username, String email, String phoneNumber, String address,String password) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.password=password;
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
