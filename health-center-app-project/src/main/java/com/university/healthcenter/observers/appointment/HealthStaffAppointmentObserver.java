package com.university.healthcenter.observers.appointment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.university.healthcenter.entities.Appointment;
import com.university.healthcenter.service.EmailSenderService;
import com.university.healthcenter.strategies.emails.AppointmentCancelingEmailStrategy;
import com.university.healthcenter.strategies.emails.AppointmentConfirmationEmailStrategy;
import com.university.healthcenter.strategies.emails.AppointmentUpdateEmailStrategy;
import com.university.healthcenter.strategies.emails.EmailStrategy;

@Component
public class HealthStaffAppointmentObserver implements AppointmentObserver {

    @Autowired
    private EmailSenderService emailSenderService;
    
    @Override
    public void onAppointmentCreated(Appointment appointment) {
        EmailStrategy strategy = new AppointmentConfirmationEmailStrategy();
        emailSenderService.setEmailStrategy(strategy);
        emailSenderService.sendEmail(appointment.getDoctor().getEmail(),
                                    appointment.getAppointmentDate().toString(),
                                    "New appointment with student " + appointment.getStudent().getUsername() + 
                                    " scheduled for " + appointment.getAppointmentDate() +
                                    " at " + appointment.getAppointmentTime());
    }

    @Override
    public void onAppointmentUpdated(Appointment appointment) {
        EmailStrategy strategy = new AppointmentUpdateEmailStrategy();
        emailSenderService.setEmailStrategy(strategy);
        emailSenderService.sendEmail(appointment.getDoctor().getEmail(),
                                    appointment.getAppointmentDate().toString(),
                                    "Appointment with student " + appointment.getStudent().getUsername() + 
                                    " has been rescheduled to " + appointment.getAppointmentDate() +
                                    " at " + appointment.getAppointmentTime());
    }

    @Override
    public void onAppointmentDeleted(Appointment appointment) {
        EmailStrategy strategy = new AppointmentCancelingEmailStrategy();
        emailSenderService.setEmailStrategy(strategy);
        emailSenderService.sendEmail(appointment.getDoctor().getEmail(),
                                    appointment.getAppointmentDate().toString(),
                                    "Appointment with student " + appointment.getStudent().getUsername() + 
                                    " scheduled for " + appointment.getAppointmentDate() +
                                    " at " + appointment.getAppointmentTime() +
                                    " has been cancelled");
    }
}
