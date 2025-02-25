package com.university.healthcenter.models;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.university.healthcenter.entities.Appointment;
import com.university.healthcenter.repository.AppointmentRepository;
@Component
public class AppointmentStatusUpdater {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Scheduled(fixedRate = 60000) // Runs every 60 seconds
    public void updateAppointmentStatuses() {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        List<Appointment> outdatedAppointments = appointmentRepository.findByStatusAndAppointmentBefore("UPCOMING", today, now);

        outdatedAppointments.forEach(appointment -> appointment.setStatus("PAST"));
        appointmentRepository.saveAll(outdatedAppointments);

        System.out.println("Updated statuses for past appointments.");
    }
}
