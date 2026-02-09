
package com.example.HealthCare.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "patients")
@Data // Automatically generates Getters/Setters via Lombok
public class patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String phone;
    private String name;
    private String gender;
    private int age;
    private String username;
    private String password;
}