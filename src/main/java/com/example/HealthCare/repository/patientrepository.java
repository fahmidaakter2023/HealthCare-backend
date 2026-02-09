package com.example.HealthCare.repository;

import com.example.HealthCare.entity.patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface patientrepository extends JpaRepository<patient, Long> {

    patient findByUsername(String username);
}