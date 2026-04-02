package com.example.HealthCare.controller;

import com.example.HealthCare.entity.appointment;
import com.example.HealthCare.entity.doctor;
import com.example.HealthCare.repository.appointmentrepository;
import com.example.HealthCare.repository.doctorrepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.List;

@RestController
@RequestMapping("/api/doctor")
@CrossOrigin(origins = "http://localhost:3000")
public class doctorcontroller {

    @Autowired
    private doctorrepository docRepo;

    @Autowired
    private appointmentrepository appRepo;

    // Doctor login
    @PostMapping("/login")
    public ResponseEntity<?> doctorLogin(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");

        doctor doc = docRepo.findByUsername(username);
        if (doc != null && doc.getPassword().equals(password)) {
            Map<String, Object> response = new HashMap<>();
            response.put("id", doc.getId());
            response.put("name", doc.getName());
            response.put("specialization", doc.getSpecialization());
            response.put("email", doc.getEmail());
            response.put("message", "Login successful");
            return ResponseEntity.ok(response);
        }

        Map<String, String> error = new HashMap<>();
        error.put("error", "Invalid Username or Password");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    // Doctor registration
    @PostMapping("/register")
    public ResponseEntity<?> registerDoctor(@RequestBody Map<String, String> data) {
        String username = data.get("username");

        if (docRepo.findByUsername(username) != null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Username already taken!");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }

        doctor newDoc = new doctor();
        newDoc.setName(data.get("name"));
        newDoc.setSpecialization(data.get("specialization"));
        newDoc.setEmail(data.get("email"));
        newDoc.setPhone(data.get("phone"));
        newDoc.setUsername(username);
        newDoc.setPassword(data.get("password"));

        docRepo.save(newDoc);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Registration successful! Please login.");
        return ResponseEntity.ok(response);
    }

    // Get ALL doctors for patient dropdown
    @GetMapping("/all")
    public ResponseEntity<?> getAllDoctors() {
        List<doctor> doctors = docRepo.findAll();
        return ResponseEntity.ok(doctors);
    }

    // Get appointments for specific doctor
    @GetMapping("/appointments")
    public ResponseEntity<?> getAppointments(@RequestParam(required = false) String doctorName) {
        List<appointment> appointments;
        if (doctorName != null && !doctorName.isEmpty()) {
            appointments = appRepo.findByDoctorName(doctorName);
        } else {
            appointments = appRepo.findAll();
        }
        return ResponseEntity.ok(appointments);
    }

    // Confirm appointment
    @PostMapping("/appointments/{id}/confirm")
    public ResponseEntity<?> confirmAppointment(@PathVariable Long id) {
        Optional<appointment> appOpt = appRepo.findById(id);
        if (appOpt.isPresent()) {
            appointment app = appOpt.get();
            app.setStatus("Confirmed");
            appRepo.save(app);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Appointment confirmed");
            return ResponseEntity.ok(response);
        }
        Map<String, String> error = new HashMap<>();
        error.put("error", "Appointment not found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // Reject appointment
    @PostMapping("/appointments/{id}/reject")
    public ResponseEntity<?> rejectAppointment(@PathVariable Long id) {
        Optional<appointment> appOpt = appRepo.findById(id);
        if (appOpt.isPresent()) {
            appointment app = appOpt.get();
            app.setStatus("Cancelled");
            appRepo.save(app);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Appointment rejected");
            return ResponseEntity.ok(response);
        }
        Map<String, String> error = new HashMap<>();
        error.put("error", "Appointment not found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // Doctor logout
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logout successful");
        return ResponseEntity.ok(response);
    }
}