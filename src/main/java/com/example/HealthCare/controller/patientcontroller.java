package com.example.HealthCare.controller;

import com.example.HealthCare.entity.appointment;
import com.example.HealthCare.entity.patient;
import com.example.HealthCare.repository.appointmentrepository;
import com.example.HealthCare.repository.doctorrepository;
import com.example.HealthCare.repository.patientrepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Random;
import java.util.List;

@Controller
public class patientcontroller {

    @Autowired
    private patientrepository repository;

    @Autowired
    private doctorrepository docRepo;

    @Autowired
    private appointmentrepository appRepo;

    // --- HOME & AUTHENTICATION ---

    @GetMapping("/")
    public String homePage() {
        return "index";
    }

    @GetMapping("/patient-login")
    public String showLoginPage(Model model, HttpSession session) {
        if (!model.containsAttribute("captchaDisplay")) {
            String captcha = String.valueOf(new Random().nextInt(9000) + 1000);
            session.setAttribute("captchaCode", captcha);
            model.addAttribute("captchaDisplay", captcha);
        }
        return "patientlogin";
    }

    @GetMapping("/patient-signup")
    public String showSignUpPage() {
        return "signup";
    }

    @PostMapping("/register-patient")
    public String registerPatient(@RequestParam String name, @RequestParam String email,
            @RequestParam String phone, @RequestParam int age,
            @RequestParam String gender, @RequestParam String username,
            @RequestParam String password, Model model, HttpSession session) {

        patient existingPatient = repository.findByUsername(username);
        if (existingPatient != null) {
            model.addAttribute("error", "Username already exists!");
            return "signup";
        }

        patient newPatient = new patient();
        newPatient.setName(name);
        newPatient.setEmail(email);
        newPatient.setPhone(phone);
        newPatient.setAge(age);
        newPatient.setGender(gender);
        newPatient.setUsername(username);
        newPatient.setPassword(password);

        repository.save(newPatient);

        model.addAttribute("message", "Registration successful! Please login.");
        return showLoginPage(model, session);
    }

    @PostMapping("/patient-login")
    public String loginProcess(@RequestParam String username,
            @RequestParam String password,
            @RequestParam String captchaInput,
            HttpSession session,
            Model model) {
        try {
            String sessionCaptcha = (String) session.getAttribute("captchaCode");

            if (sessionCaptcha == null || !sessionCaptcha.equals(captchaInput)) {
                model.addAttribute("error", "Invalid Captcha code!");
                return showLoginPage(model, session);
            }

            patient existingPatient = repository.findByUsername(username);
            if (existingPatient != null && password.equals(existingPatient.getPassword())) {
                session.setAttribute("patientName", existingPatient.getName());
                return "redirect:/patient-home";
            } else {
                model.addAttribute("error", "Invalid username or password");
                return showLoginPage(model, session);
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "System error: " + e.getMessage());
            return "patientlogin";
        }
    }

    // --- DASHBOARD ---

    @GetMapping("/patient-home")
    public String showDashboard(HttpSession session, Model model) {
        String patientName = (String) session.getAttribute("patientName");

        // Redirect to login if user tries to access dashboard without logging in
        if (patientName == null) {
            return "redirect:/patient-login";
        }

        // Fetch appointments for this specific patient to show in the table
        List<appointment> myAppointments = appRepo.findByPatientName(patientName);

        model.addAttribute("patientName", patientName);
        model.addAttribute("appointments", myAppointments);

        return "patienthome";
    }

    // --- APPOINTMENT BOOKING ---

    @GetMapping("/book-appointment")
    public String showBookingPage(Model model, HttpSession session) {
        if (session.getAttribute("patientName") == null) {
            return "redirect:/patient-login";
        }
        model.addAttribute("doctors", docRepo.findAll());
        return "appointment"; 
    }

    @PostMapping("/confirm-appointment")
    public String confirmAppointment(@RequestParam String doctorName,
            @RequestParam String date,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        String patientName = (String) session.getAttribute("patientName");

        if (patientName == null) {
            return "redirect:/patient-login";
        }

        appointment app = new appointment();
        app.setPatientName(patientName);
        app.setDoctorName(doctorName);
        app.setAppointmentDate(date);
        app.setStatus("Pending");

        appRepo.save(app);

        redirectAttributes.addFlashAttribute("message", "Appointment request sent to Dr. " + doctorName + ". Status: Pending.");
        return "redirect:/patient-home";
    }

    // --- UTILS ---

    @GetMapping("/api/refresh-captcha")
    @ResponseBody
    public String refreshCaptcha(HttpSession session) {
        String newCaptcha = String.valueOf(new Random().nextInt(9000) + 1000);
        session.setAttribute("captchaCode", newCaptcha);
        return newCaptcha;
    }

    @GetMapping("/cancel-appointment/{id}")
    public String cancelAppointment(@PathVariable Long id, RedirectAttributes redirectAttributes, HttpSession session) {
        if (session.getAttribute("patientName") == null) {
            return "redirect:/patient-login";
        }

        appRepo.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "Appointment cancelled successfully.");
        return "redirect:/patient-home";
    }

    @GetMapping("/patient-logout")
    public String patientLogout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/";
    }
}