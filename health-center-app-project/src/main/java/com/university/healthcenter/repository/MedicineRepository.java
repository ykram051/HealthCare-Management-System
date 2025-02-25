package com.university.healthcenter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.university.healthcenter.entities.Medicine;

@Repository
public interface MedicineRepository extends JpaRepository<Medicine, Integer> {
    @Modifying
    @Query("DELETE FROM Medicine m WHERE m.treatment.id = :treatmentId")
    void deleteByTreatmentId(@Param("treatmentId") Integer treatmentId);


}
