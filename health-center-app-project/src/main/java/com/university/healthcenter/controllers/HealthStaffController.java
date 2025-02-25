package com.university.healthcenter.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.university.healthcenter.models.AppointmentModel;
import com.university.healthcenter.models.HealthStaffModel;
import com.university.healthcenter.service.HealthStaffService;
import com.university.healthcenter.utils.PermissionChecker;

@RestController
@RequestMapping("/api/health-staff")
public class HealthStaffController {

    private final HealthStaffService healthStaffService;
    private final PermissionChecker permissionChecker;

    public HealthStaffController(HealthStaffService healthStaffService, PermissionChecker permissionChecker) {
        this.healthStaffService = healthStaffService;
        this.permissionChecker = permissionChecker;
    }

    // Fetch all HealthStaff
    @GetMapping
    public ResponseEntity<List<HealthStaffModel>> getAllHealthStaff() {
        return ResponseEntity.ok(healthStaffService.getAllHealthStaff());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HealthStaffModel> getHealthStaffById(
            @RequestHeader("Authorization") String token,
            @PathVariable int id) {
        permissionChecker.validateHealthStaffAccess(token, id);
        HealthStaffModel healthStaff = healthStaffService.getHealthStaffById(id)
                .orElseThrow(() -> new RuntimeException("HealthStaff not found"));
        return ResponseEntity.ok(healthStaff);
    }
    

    // Update HealthStaff profile
    @PutMapping("/{id}")
    public ResponseEntity<HealthStaffModel> updateHealthStaffProfile(
            @RequestHeader("Authorization") String token,
            @PathVariable int id,
            @RequestBody HealthStaffModel healthStaffModel) {
        permissionChecker.validateHealthStaffAccess(token, id);
        HealthStaffModel updatedHealthStaff = healthStaffService.updateHealthStaffProfile(id, healthStaffModel);
        return ResponseEntity.ok(updatedHealthStaff);
    }

    // Fetch all appointments for a HealthStaff
    @GetMapping("/{id}/appointments")
    public ResponseEntity<List<AppointmentModel>> getAppointments(
            @RequestHeader("Authorization") String token,
            @PathVariable int id) {
        permissionChecker.validateHealthStaffAccess(token, id);
        List<AppointmentModel> appointments = healthStaffService.getAppointmentsByHealthStaffId(id);
        return ResponseEntity.ok(appointments);
    }

    // Create new HealthStaff
    @PostMapping
    public ResponseEntity<HealthStaffModel> createHealthStaff(@RequestBody HealthStaffModel healthStaffModel) {
        HealthStaffModel newHealthStaff = healthStaffService.createHealthStaff(healthStaffModel);
        return ResponseEntity.ok(newHealthStaff);
    }

    // Delete HealthStaff
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHealthStaff(
            @RequestHeader("Authorization") String token,
            @PathVariable int id) {
        permissionChecker.validateHealthStaffAccess(token, id);
        healthStaffService.deleteHealthStaff(id);
        return ResponseEntity.noContent().build();
    }
}
