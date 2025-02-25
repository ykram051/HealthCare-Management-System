package com.university.healthcenter.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.university.healthcenter.exceptions.InvalidTokenException;
import com.university.healthcenter.models.AppointmentModel;
import com.university.healthcenter.models.RescheduleRequest;
import com.university.healthcenter.service.AppointmentService;
import com.university.healthcenter.utils.PermissionChecker;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private PermissionChecker permissionChecker;


    // View a specific appointment
    @GetMapping("/{appointmentId}")
    public ResponseEntity<AppointmentModel> getAppointmentById(
            @RequestHeader("Authorization") String token,
            @PathVariable int appointmentId) {
        try {
            int userId = permissionChecker.getUserIdByToken(token);

            // Fetch the appointment
            AppointmentModel appointment = appointmentService.getAppointmentById(appointmentId);
            if (appointment == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            // Restrict access: Only the student or the doctor involved can view the appointment
            if (appointment.getStudentId() != userId && appointment.getDoctorId() != userId) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            return new ResponseEntity<>(appointment, HttpStatus.OK);
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping
    public ResponseEntity<?> createAppointment(
            @RequestHeader("Authorization") String token,
            @RequestBody AppointmentModel appointmentModel) {
        try {
            int userId = permissionChecker.getUserIdByToken(token);

            if (!permissionChecker.isStudent(token)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only students can create appointments.");
            }

            appointmentModel.setStudentId(userId);
            AppointmentModel createdAppointment = appointmentService.createAppointment(appointmentModel);
            System.out.println("appoit created");
            return new ResponseEntity<>(createdAppointment, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // View all appointments for a specific student
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<AppointmentModel>> getAppointmentsByStudentId(
            @RequestHeader("Authorization") String token,
            @PathVariable int studentId) {
        try {
            int userId = permissionChecker.getUserIdByToken(token);

            // Restrict access: Allow only the student to view their appointments
            if (userId != studentId) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            List<AppointmentModel> appointments = appointmentService.getAppointmentsByStudentId(studentId);
            return ResponseEntity.ok(appointments);
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<AppointmentModel>> getAppointmentsByDoctorIdAndDate(
            @RequestHeader("Authorization") String token,
            @PathVariable int doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            // Fetch appointments by doctor and date
            List<AppointmentModel> appointments = appointmentService.getAppointmentsByDoctorIdAndDate(doctorId, date);
            return ResponseEntity.ok(appointments);
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // Delete an appointment (Only the student who created it)
    @DeleteMapping("/{appointmentId}")
    public ResponseEntity<Void> deleteAppointment(
            @RequestHeader("Authorization") String token,
            @PathVariable int appointmentId) {
        try {
            int userId = permissionChecker.getUserIdByToken(token);

            // Fetch the appointment
            AppointmentModel appointment = appointmentService.getAppointmentById(appointmentId);
            if (appointment == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            // Allow deletion only for the student who created it
            if (appointment.getStudentId() != userId) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            appointmentService.deleteAppointment(appointmentId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }@PutMapping("/{appointmentId}/reschedule")
public ResponseEntity<?> rescheduleAppointment(
        @RequestHeader("Authorization") String token,
        @PathVariable int appointmentId,
        @RequestBody RescheduleRequest rescheduleRequest) {
    try {
        // Validate and get the student ID from the token
        int studentId = permissionChecker.getUserIdByToken(token);

        // Fetch the appointment
        AppointmentModel appointment = appointmentService.getAppointmentById(appointmentId);
        if (appointment == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Appointment not found.");
        }

        // Ensure the student owns this appointment
        if (appointment.getStudentId() != studentId) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to reschedule this appointment.");
        }

        // Validate the new date is in the future
        if (rescheduleRequest.getNewDate().isBefore(LocalDate.now())) {
            return ResponseEntity.badRequest().body("Cannot reschedule to a past date.");
        }

        // Check if the time slot is already booked for the doctor
        boolean isSlotBooked = appointmentService.getAppointmentsByDoctorIdAndDate(
                appointment.getDoctorId(), rescheduleRequest.getNewDate())
                .stream()
                .anyMatch(appt -> appt.getAppointmentTime().equals(rescheduleRequest.getNewTime()));

        if (isSlotBooked) {
            return ResponseEntity.badRequest().body("The selected time slot is already booked. Please choose another.");
        }

        // Reschedule the appointment
        appointmentService.rescheduleAppointment(
                studentId,
                appointmentId,
                rescheduleRequest.getNewDate(),
                rescheduleRequest.getNewTime()
        );

        return ResponseEntity.ok("Appointment rescheduled successfully.");
    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    } catch (InvalidTokenException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token.");
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
    }
}


}