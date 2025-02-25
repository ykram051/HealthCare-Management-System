package com.university.healthcenter.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.university.healthcenter.entities.Treatment;

@Repository
public interface TreatmentRepository extends JpaRepository<Treatment, Integer> {

    List<Treatment> findByStudentUserId(int studentId);

    @Modifying
    @Query("DELETE FROM Treatment t WHERE t.student.id = :studentId AND t.id = :treatmentId")
    void deleteByStudentUserIdAndId(@Param("studentId") int studentId, @Param("treatmentId") int treatmentId);

}
