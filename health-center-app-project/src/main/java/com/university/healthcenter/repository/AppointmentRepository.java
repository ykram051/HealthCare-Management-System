package com.university.healthcenter.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.university.healthcenter.entities.Appointment;

public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {

    List<Appointment> findByStudentUserId(int studentId);

    List<Appointment> findByDoctorUserId(int doctorUserId);
    boolean existsByDoctorUserIdAndAppointmentDateAndAppointmentTime(int doctorId, LocalDate date, LocalTime time);

    @Query("SELECT DISTINCT a FROM Appointment a WHERE a.doctor.userId = :doctorId AND a.appointmentDate = :date")
List<Appointment> findByDoctorUserIdAndAppointmentDate(@Param("doctorId") int doctorId, @Param("date") LocalDate date);
@Query("SELECT a FROM Appointment a WHERE a.status = :status AND (a.appointmentDate < :date OR (a.appointmentDate = :date AND a.appointmentTime < :time))")
List<Appointment> findByStatusAndAppointmentBefore(
    @Param("status") String status,
    @Param("date") LocalDate date,
    @Param("time") LocalTime time
);

}