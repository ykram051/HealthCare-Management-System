package com.university.healthcenter.serviceImpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.university.healthcenter.entities.HealthStaff;
import com.university.healthcenter.models.AppointmentModel;
import com.university.healthcenter.models.HealthStaffModel;
import com.university.healthcenter.repository.AppointmentRepository;
import com.university.healthcenter.repository.HealthStaffRepository;
import com.university.healthcenter.service.HealthStaffService;

@Service
public class HealthStaffServiceImpl implements HealthStaffService {

    @Autowired
    private HealthStaffRepository healthStaffRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<HealthStaffModel> getAllHealthStaff() {
        return healthStaffRepository.findAll()
                .stream()
                .map(healthStaff -> modelMapper.map(healthStaff, HealthStaffModel.class))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<HealthStaffModel> getHealthStaffById(int healthStaffId) {
        return healthStaffRepository.findById(healthStaffId)
                .map(healthStaff -> modelMapper.map(healthStaff, HealthStaffModel.class));
    }

    @Override
    public HealthStaffModel saveHealthStaff(HealthStaffModel healthStaffModel) {
        HealthStaff healthStaff = modelMapper.map(healthStaffModel, HealthStaff.class);
        HealthStaff savedHealthStaff = healthStaffRepository.save(healthStaff);
        return modelMapper.map(savedHealthStaff, HealthStaffModel.class);
    }

    @Override
    public void deleteHealthStaff(int healthStaffId) {
        healthStaffRepository.deleteById(healthStaffId);
    }

    @Override
    public HealthStaffModel updateHealthStaffProfile(int healthStaffId, HealthStaffModel healthStaffModel) {
        HealthStaff healthStaff = healthStaffRepository.findById(healthStaffId)
                .orElseThrow(() -> new RuntimeException("HealthStaff not found with ID: " + healthStaffId));
        modelMapper.map(healthStaffModel, healthStaff);
        HealthStaff updatedHealthStaff = healthStaffRepository.save(healthStaff);
        return modelMapper.map(updatedHealthStaff, HealthStaffModel.class);
    }

    @Override
    public List<AppointmentModel> getAppointmentsByHealthStaffId(int healthStaffId) {
        return appointmentRepository.findByDoctorUserId(healthStaffId)
                .stream()
                .map(appointment -> modelMapper.map(appointment, AppointmentModel.class))
                .collect(Collectors.toList());
    }

    @Override
    public HealthStaffModel createHealthStaff(HealthStaffModel healthStaffModel) {
        HealthStaff healthStaff = modelMapper.map(healthStaffModel, HealthStaff.class);
        HealthStaff savedHealthStaff = healthStaffRepository.save(healthStaff);
        return modelMapper.map(savedHealthStaff, HealthStaffModel.class);
    }
}
