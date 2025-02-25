package com.university.healthcenter.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.university.healthcenter.models.AppointmentModel;

public interface AppointmentService {

    List<AppointmentModel> getAllAppointments();

    AppointmentModel getAppointmentById(int appointmentId);

    AppointmentModel createAppointment(AppointmentModel appointmentModel);

    List<AppointmentModel> getAppointmentsByStudentId(int studentId);

    List<AppointmentModel> getAppointmentsByDoctorId(int doctorId);

    List<AppointmentModel> getAppointmentsByDoctorIdAndDate(int doctorId, LocalDate date);

    void deleteAppointment(int appointmentId);
      /**
     * Saves an appointment to the database.
     * 
     * @param appointment The appointment to be saved.
     */
    void saveAppointment(AppointmentModel appointment);

    /**
     * Reschedules an appointment by updating its date and time.
     * 
     * @param studentId The ID of the student requesting the reschedule.
     * @param appointmentId The ID of the appointment to be rescheduled.
     * @param newDate The new date for the appointment.
     * @param newTime The new time for the appointment.
     * @throws IllegalArgumentException if the new date/time is invalid or conflicts with another appointment.
     */
    void rescheduleAppointment(int studentId, int appointmentId, LocalDate newDate, LocalTime newTime);
}
