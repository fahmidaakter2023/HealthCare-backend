package com.example.HealthCare.repository;

import com.example.HealthCare.entity.doctor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface doctorrepository extends JpaRepository<doctor, Long> {
    doctor findByUsername(String username);
}