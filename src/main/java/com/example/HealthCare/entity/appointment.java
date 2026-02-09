package com.example.HealthCare.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String patientName;
    private String doctorName;
    private String appointmentDate;
    private String status; 
}