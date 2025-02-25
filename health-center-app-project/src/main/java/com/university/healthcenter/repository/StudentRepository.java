package com.university.healthcenter.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.university.healthcenter.entities.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {
    Optional<Student> findById(int id); 
    List<Student> findByAllergiesContaining(String allergy);
    Optional<Student> findByEmail(String email);


}
