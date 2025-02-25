
package com.university.healthcenter.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.university.healthcenter.entities.HealthStaff;

@Repository
public interface HealthStaffRepository extends JpaRepository<HealthStaff, Integer> {
    Optional<HealthStaff> findById(int id);
    Optional<HealthStaff> findByEmail(String email);
}
