package com.example.HealthCare.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String specialization;
    private String email;
    private String phone;
    private String username;
    private String password;
}