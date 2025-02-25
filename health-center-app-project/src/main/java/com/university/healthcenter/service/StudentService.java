package com.university.healthcenter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.university.healthcenter.entities.Medicine;
import com.university.healthcenter.models.AppointmentModel;
import com.university.healthcenter.models.StudentModel;
import com.university.healthcenter.models.TreatmentModel;

public interface StudentService {

    List<StudentModel> getAllStudents();

    Optional<StudentModel> getStudentById(int studentId);

    StudentModel saveStudent(StudentModel studentModel);

    void deleteStudent(int studentId);

    StudentModel updateStudentProfile(int studentId, StudentModel studentModel);

    List<AppointmentModel> getAppointmentsByStudentId(int studentId);

    List<StudentModel> findStudentsByAllergy(String allergy);

    StudentModel createStudent(StudentModel studentModel);

    
    void createTreatmentForStudent(int studentId, TreatmentModel treatmentModel);

    List<TreatmentModel> getTreatmentsByStudentId(int studentId);
    
    void deleteTreatmentForStudent(Integer studentId, Integer treatmentId);

    List<Medicine> getAllMedicinesByStudentId(int studentId);
    
    void deleteMedicineForStudent(int studentId, int medicineId, int treatmentId);
         TreatmentModel createTreatmentFromPrescription(MultipartFile file, Integer studentId) throws Exception;


    
}
