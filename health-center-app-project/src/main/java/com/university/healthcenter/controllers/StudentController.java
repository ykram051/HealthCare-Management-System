package com.university.healthcenter.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import org.springframework.web.multipart.MultipartFile;

import com.university.healthcenter.models.AppointmentModel;
import com.university.healthcenter.models.StudentModel;
import com.university.healthcenter.models.TreatmentModel;
import com.university.healthcenter.service.StudentService;
import com.university.healthcenter.utils.PermissionChecker;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;
    private final PermissionChecker permissionChecker;

    public StudentController(StudentService studentService, PermissionChecker permissionChecker) {
        this.studentService = studentService;
        this.permissionChecker = permissionChecker;
    }

    // Fetch all students
    @GetMapping
    public ResponseEntity<List<StudentModel>> getAllStudents() {
        List<StudentModel> students = studentService.getAllStudents();
        return ResponseEntity.ok(students);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentModel> getStudentProfile(
            @RequestHeader("Authorization") String token,
            @PathVariable int id) {
        permissionChecker.validateStudentAccess(token, id);
        StudentModel student = studentService.getStudentById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        return ResponseEntity.ok(student);
    }

    // Update student profile
    @PutMapping("/{id}")
    public ResponseEntity<StudentModel> updateStudentProfile(
            @RequestHeader("Authorization") String token,
            @PathVariable int id,
            @RequestBody StudentModel studentModel) {
        permissionChecker.validateStudentAccess(token, id);
        StudentModel updatedStudent = studentService.updateStudentProfile(id, studentModel);
        return ResponseEntity.ok(updatedStudent);
    }

    // Get all appointments for a student
    @GetMapping("/{id}/appointments")
    public ResponseEntity<List<AppointmentModel>> getAppointments(
            @RequestHeader("Authorization") String token,
            @PathVariable int id) {
        permissionChecker.validateStudentAccess(token, id);
        List<AppointmentModel> appointments = studentService.getAppointmentsByStudentId(id);
        return ResponseEntity.ok(appointments);
    }

    // Create a new student
    @PostMapping
    public ResponseEntity<StudentModel> createStudent(@RequestBody StudentModel studentModel) {
        StudentModel newStudent = studentService.createStudent(studentModel);
        return ResponseEntity.ok(newStudent);
    }

    // Delete a student
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(
            @RequestHeader("Authorization") String token,
            @PathVariable int id) {
        permissionChecker.validateStudentAccess(token, id);
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

    // create treatment for a student
    @PostMapping("/{id}/treatments")
    public ResponseEntity<String> createTreatmentForStudent(
            @RequestHeader("Authorization") String token,
            @PathVariable int id,
            @RequestBody TreatmentModel treatmentModel) {
        permissionChecker.validateStudentAccess(token, id);
        studentService.createTreatmentForStudent(id, treatmentModel);
        return ResponseEntity.status(HttpStatus.CREATED).body("Treatment created successfully for student ID: " + id);
    }

    @PostMapping(
            value = "/{id}/treatments/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<TreatmentModel> uploadPrescriptionForStudent(
            @RequestHeader("Authorization") String token,
            @PathVariable("id") String id,
            @RequestParam("file") MultipartFile file) {

        Integer studentId;
        try {
            if ("me".equals(id)) {
                studentId = permissionChecker.getUserIdByToken(token);
            } else {
                studentId = Integer.parseInt(id);
            }

            // Log file details for debugging
            System.out.println("File name: " + file.getOriginalFilename());
            System.out.println("File size: " + file.getSize());

            permissionChecker.validateStudentAccess(token, studentId);
            TreatmentModel treatmentModel = studentService.createTreatmentFromPrescription(file, studentId);
            return ResponseEntity.status(HttpStatus.CREATED).body(treatmentModel);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // get all treatments for a student
    @GetMapping("/{id}/treatments")
    public ResponseEntity<List<TreatmentModel>> getTreatmentsByStudentId(
            @RequestHeader("Authorization") String token,
            @PathVariable int id) {
        permissionChecker.validateStudentAccess(token, id);
        List<TreatmentModel> treatments = studentService.getTreatmentsByStudentId(id);
        return ResponseEntity.ok(treatments);
    }

    @DeleteMapping("/{studentId}/treatments/{treatmentId}")
    public ResponseEntity<Void> deleteTreatmentForStudent(
            @RequestHeader("Authorization") String token,
            @PathVariable("studentId") Integer studentId,
            @PathVariable("treatmentId") Integer treatmentId) {

        permissionChecker.validateStudentAccess(token, studentId); // Validate token and student access
        studentService.deleteTreatmentForStudent(studentId, treatmentId);
        return ResponseEntity.noContent().build();
    }

}
