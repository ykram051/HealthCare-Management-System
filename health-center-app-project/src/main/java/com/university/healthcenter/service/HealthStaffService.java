package com.university.healthcenter.service;

import java.util.List;
import java.util.Optional;

import com.university.healthcenter.models.AppointmentModel;
import com.university.healthcenter.models.HealthStaffModel;

public interface HealthStaffService {
    List<HealthStaffModel> getAllHealthStaff();
    Optional<HealthStaffModel> getHealthStaffById(int healthStaffId);
    HealthStaffModel saveHealthStaff(HealthStaffModel healthStaffModel);
    void deleteHealthStaff(int healthStaffId);
    HealthStaffModel updateHealthStaffProfile(int healthStaffId, HealthStaffModel healthStaffModel);
    List<AppointmentModel> getAppointmentsByHealthStaffId(int healthStaffId);
    HealthStaffModel createHealthStaff(HealthStaffModel healthStaffModel);

}
