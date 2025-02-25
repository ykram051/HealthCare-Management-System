package com.university.healthcenter.models;

import java.time.LocalDate;
import java.time.LocalTime;

public class AppointmentModel {

    private int id;
    private int studentId;
    private int doctorId;
    private LocalDate appointmentDate; // Date of the appointment
    private LocalTime appointmentTime; 
    private String status;

    // Constructors
    public AppointmentModel() {}

    public AppointmentModel(int id, int studentId, int doctorId, LocalDate appointmentDate, LocalTime appointmentTime, String status) {
        this.id = id;
        this.studentId = studentId;
        this.doctorId = doctorId;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.status = status;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public Integer getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public LocalTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
