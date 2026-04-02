package com.example.HealthCare.repository;

import com.example.HealthCare.entity.patient;
import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface patientrepository extends JpaRepository<patient, Long> {

    
   // @Query("SELECT p FROM patient p WHERE p.username = :username")
    // patient findByUsername(@Param("username") String username);

    patient findByUsername(String username);
}