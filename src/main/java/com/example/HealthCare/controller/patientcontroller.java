package com.example.HealthCare.controller;

import com.example.HealthCare.entity.appointment;
import com.example.HealthCare.entity.patient;
import com.example.HealthCare.repository.appointmentrepository;
//import com.example.HealthCare.repository.doctorrepository;
import com.example.HealthCare.repository.patientrepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/patient")
@CrossOrigin(origins = "http://localhost:3000")
public class patientcontroller {

    @Autowired
    private patientrepository repository;

    // @Autowired
    // private doctorrepository docRepo;

    @Autowired
    private appointmentrepository appRepo;

    // --- PATIENT REGISTRATION ---
    @PostMapping("/register")
    public ResponseEntity<?> registerPatient(@RequestBody Map<String, String> data) {
        String username = data.get("username");

        if (repository.findByUsername(username) != null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Username already exists!");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }

        patient newPatient = new patient();
        newPatient.setName(data.get("name"));
        newPatient.setEmail(data.get("email"));
        newPatient.setPhone(data.get("phone"));
        // newPatient.setAge(Integer.parseInt(data.get("age")));
        if (data.get("age") != null && !data.get("age").isEmpty()) {
            newPatient.setAge(Integer.parseInt(data.get("age")));
        }
        newPatient.setGender(data.get("gender"));
        newPatient.setUsername(username);
        newPatient.setPassword(data.get("password"));

        repository.save(newPatient);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Registration successful! Please login.");
        return ResponseEntity.ok(response);
    }

    // --- PATIENT LOGIN ---
    @PostMapping("/login")
    public ResponseEntity<?> patientLogin(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");

        patient existingPatient = repository.findByUsername(username);
        if (existingPatient != null && existingPatient.getPassword().equals(password)) {
            Map<String, Object> response = new HashMap<>();
            response.put("id", existingPatient.getId());
            response.put("name", existingPatient.getName());
            response.put("email", existingPatient.getEmail());
            response.put("message", "Login successful");
            return ResponseEntity.ok(response);
        }

        Map<String, String> error = new HashMap<>();
        error.put("error", "Invalid username or password");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    // --- GET PATIENT APPOINTMENTS ---
    @GetMapping("/{username}/appointments")
    public ResponseEntity<?> getAppointments(@PathVariable String username) {
        List<appointment> appointments = appRepo.findByPatientName(username);
        return ResponseEntity.ok(appointments);
    }

    // --- BOOK APPOINTMENT ---
    @PostMapping("/appointments")
    public ResponseEntity<?> bookAppointment(@RequestBody Map<String, String> data) {
        String patientName = data.get("patientName");
        String doctorName = data.get("doctorName");
        String date = data.get("date");

        appointment app = new appointment();
        app.setPatientName(patientName);
        app.setDoctorName(doctorName);
        app.setAppointmentDate(date);
        app.setStatus("Pending");

        appRepo.save(app);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Appointment request sent to Dr. " + doctorName + ". Status: Pending.");
        return ResponseEntity.ok(response);
    }

    // --- CANCEL APPOINTMENT ---
    @DeleteMapping("/appointments/{id}")
    public ResponseEntity<?> cancelAppointment(@PathVariable Long id) {
        if (!appRepo.existsById(id)) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Appointment not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        appRepo.deleteById(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Appointment cancelled successfully.");
        return ResponseEntity.ok(response);
    }

    // --- LOGOUT ---
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logout successful");
        return ResponseEntity.ok(response);
    }

}