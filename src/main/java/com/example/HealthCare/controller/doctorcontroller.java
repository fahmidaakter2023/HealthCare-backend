package com.example.HealthCare.controller;

import com.example.HealthCare.entity.appointment;
import com.example.HealthCare.entity.doctor;
import com.example.HealthCare.repository.appointmentrepository;
import com.example.HealthCare.repository.doctorrepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class doctorcontroller {

    @Autowired
    private doctorrepository docRepo;
    
    @Autowired
    private appointmentrepository appRepo;

    @GetMapping("/doctor-login") 
    public String showDoctorLogin() {
        return "doctorlogin";
    }

    // FIX: Added this missing mapping
    @GetMapping("/doctor-signup")
    public String showDoctorSignUp() {
        return "doctorsignup";
    }

    @PostMapping("/doctor-login")
    public String doctorLogin(@RequestParam String username,
            @RequestParam String password,
            HttpSession session, Model model) {
        doctor doc = docRepo.findByUsername(username);

        if (doc != null && doc.getPassword().equals(password)) {
            session.setAttribute("doctorName", doc.getName());
            session.setAttribute("specialization", doc.getSpecialization());
            // Use redirect to prevent form resubmission
            return "redirect:/doctor-home";
        }

        model.addAttribute("error", "Invalid Username or Password");
        return "doctorlogin";
    }

    @GetMapping("/doctor-home")
    public String doctorDashboard(HttpSession session, Model model) {
        String docName = (String) session.getAttribute("doctorName");
        if (docName == null) {
            return "redirect:/doctor-login";
        }
        model.addAttribute("name", docName);
        return "doctorhome";
    }

    @PostMapping("/register-doctor")
    public String registerDoctor(@RequestParam String name,
            @RequestParam String specialization,
            @RequestParam String email,
            @RequestParam String username,
            @RequestParam String password,
            Model model) {

        if (docRepo.findByUsername(username) != null) {
            model.addAttribute("error", "Username already taken!");
            return "doctorsignup";
        }

        doctor newDoc = new doctor();
        newDoc.setName(name);
        newDoc.setSpecialization(specialization);
        newDoc.setEmail(email);
        newDoc.setUsername(username);
        newDoc.setPassword(password);

        docRepo.save(newDoc);
        model.addAttribute("message", "Registration successful! Please login.");
        return "doctorlogin";
    }

    @GetMapping("/doctor-logout")
    public String logout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/";
    }


    @GetMapping("/doctor/confirm-appointment/{id}")
public String doctorConfirmAppointment(@PathVariable Long id) {
    appointment app = appRepo.findById(id).orElse(null);
    if (app != null) {
        app.setStatus("Confirmed"); // Change status from Pending to Confirmed
        appRepo.save(app);
    }
    return "redirect:/doctor-home"; // Redirect back to doctor dashboard
}
}