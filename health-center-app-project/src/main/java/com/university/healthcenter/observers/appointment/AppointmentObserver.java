package com.university.healthcenter.observers.appointment;

import com.university.healthcenter.entities.Appointment;

public interface AppointmentObserver {
    void onAppointmentCreated(Appointment appointment);
    void onAppointmentUpdated(Appointment appointment);
    void onAppointmentDeleted(Appointment appointment);
}
