package com.telerikacademy.web.smartgarageti.controllers.mvc;

import com.telerikacademy.web.smartgarageti.helpers.AuthenticationHelper;
import com.telerikacademy.web.smartgarageti.models.User;
import com.telerikacademy.web.smartgarageti.models.dto.ContactRequestDto;
import com.telerikacademy.web.smartgarageti.services.EmailService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/ti")
public class HomeMvcController {
    @Value("${spring.mail.username}")
    private String defaultFromEmail;

    private final AuthenticationHelper authenticationHelper;
    private final EmailService emailService;

    @Autowired
    public HomeMvcController(AuthenticationHelper authenticationHelper, EmailService emailService) {
        this.authenticationHelper = authenticationHelper;
        this.emailService = emailService;
    }

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession session){
        return session.getAttribute("currentUser") !=null;
    }

    @ModelAttribute("isEmployee")
    public boolean populateIsEmployee(HttpSession session) {
        if (session.getAttribute("currentUser") != null) {
            User user = authenticationHelper.tryGetUserFromSession(session);
            return user.getRole().getName().equals("Employee");
        }
        return false;
    }

    @ModelAttribute("username")
    public String populateUsername(HttpSession session) {
        if (session.getAttribute("currentUser") != null) {
            User user = authenticationHelper.tryGetUserFromSession(session);
            return user.getUsername();
        }
        return null;
    }

    @ModelAttribute("loggedInUser")
    public User populateUser(HttpSession session) {
        if (session.getAttribute("currentUser") != null) {
            return authenticationHelper.tryGetUserFromSession(session);
        }
        return null;
    }

    @GetMapping
    public String showHomePage() {
        return "home";
    }

    @GetMapping("/about")
    public String showAboutPage() {
        return "about";
    }

    @GetMapping("/gallery")
    public String showGalleryPage() {
        return "galleries";
    }

    @GetMapping("/appointment")
    public String showAppointmentPage() {
        return "appointment";
    }

    @GetMapping("/contact")
    public String showContactPage(Model model) {
        model.addAttribute("contactRequest", new ContactRequestDto());
        return "contact_2";
    }

    @GetMapping("/contact/success")
    public String showSuccessPage() {
        return "success";
    }

    @PostMapping("/contact")
    public String handleContactForm(
            @Valid @ModelAttribute("contactRequest") ContactRequestDto contactRequestDto,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", "There are errors in the form. Please correct them.");
            System.out.println("Validation errors: " + bindingResult.getAllErrors());
            return "contact_2";
        }

        String subject = "New Contact Request";
        String text = "Name: " + contactRequestDto.getName() + "<br>" +
                "Email: " + contactRequestDto.getEmail() + "<br>" +
                "Phone: " + contactRequestDto.getPhone() + "<br>" +
                "Message: " + contactRequestDto.getMessage();

        try {
            emailService.sendEmail(defaultFromEmail, subject, text, contactRequestDto.getEmail());
        } catch (Exception e) {
            model.addAttribute("errorMessage", "There was a problem sending your message. Please try again.");
        }
        return "redirect:/ti/contact/success";
    }



    @PostMapping("/appointment")
    public String handleAppointmentForm(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
            @RequestParam("vehicle-year") String vehicleYear,
            @RequestParam("vehicle-make") String vehicleMake,
            @RequestParam("vehicle-mileage") String vehicleMileage,
            @RequestParam("appointment-date") String appointmentDate,
            @RequestParam("time-frame") String timeFrame,
            @RequestParam(value = "services", required = false) String[] services,
            @RequestParam("message_appointment") String message,
            Model model) {

        String subject = "New Appointment Request";
        StringBuilder text = new StringBuilder();
        text.append("Name: ").append(name).append("\n")
                .append("Email: ").append(email).append("\n")
                .append("Phone: ").append(phone).append("\n")
                .append("Vehicle Year: ").append(vehicleYear).append("\n")
                .append("Vehicle Make: ").append(vehicleMake).append("\n")
                .append("Vehicle Mileage: ").append(vehicleMileage).append("\n")
                .append("Appointment Date: ").append(appointmentDate).append("\n")
                .append("Preferred Time Frame: ").append(timeFrame).append("\n");

        if (services != null) {
            text.append("Services: ").append(String.join(", ", services)).append("\n");
        }

        text.append("Additional Message: ").append(message);

        try {
            emailService.sendEmail(defaultFromEmail, subject, text.toString());
            model.addAttribute("successMessage", "Appointment request sent successfully!");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Failed to send appointment request.");
        }

        return "appointment";
    }

    @GetMapping("/admin-panel")
    public String showAdminPanel() {
        return "AdminPanel";
    }
}


