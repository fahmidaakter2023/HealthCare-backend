package com.example.HealthCare.repository;

import com.example.HealthCare.entity.appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface appointmentrepository extends JpaRepository<appointment, Long> {
    List<appointment> findByPatientName(String patientName);

    List<appointment> findByDoctorName(String doctorName);
}
