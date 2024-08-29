package com.telerikacademy.web.smartgarageti.controllers.mvc;

import com.telerikacademy.web.smartgarageti.exceptions.AuthenticationException;
import com.telerikacademy.web.smartgarageti.exceptions.DuplicateEntityException;
import com.telerikacademy.web.smartgarageti.helpers.AuthenticationHelper;
import com.telerikacademy.web.smartgarageti.helpers.MapperHelper;
import com.telerikacademy.web.smartgarageti.models.User;
import com.telerikacademy.web.smartgarageti.models.dto.LoginDto;
import com.telerikacademy.web.smartgarageti.models.dto.UserCreationDto;
import com.telerikacademy.web.smartgarageti.services.contracts.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/ti/auth")
public class AuthenticationMvcController {
    private final AuthenticationHelper authenticationHelper;
    private final MapperHelper mapperHelper;
    private final UserService userService;

    public AuthenticationMvcController(AuthenticationHelper authenticationHelper, MapperHelper mapperHelper, UserService userService) {
        this.authenticationHelper = authenticationHelper;
        this.mapperHelper = mapperHelper;
        this.userService = userService;
    }

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession session) {
        return session.getAttribute("currentUser") != null;
    }

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("login", new LoginDto());
        return "LoginView";
    }

    @PostMapping("/login")
    public String handleLogin(@Valid @ModelAttribute("login") LoginDto login,
                              BindingResult bindingResult, HttpSession session) {
        if (bindingResult.hasErrors()) {
            return "LoginView";
        }
        try {
            authenticationHelper.verifyAuthentication(login.getUsername(), login.getPassword());
            session.setAttribute("currentUser", login.getUsername());
            return "redirect:/ti";
        } catch (AuthenticationException e) {
            bindingResult.rejectValue("username", "error.login", e.getMessage());
            return "LoginView";
        }
    }

    @GetMapping("/logout")
    public String handleLogout(HttpSession session) {
        session.removeAttribute("currentUser");
        return "redirect:/ti";
    }
//
//    @GetMapping("/register")
//    public String showRegister(Model model) {
//        model.addAttribute("register", new UserCreationDto());
//        return "RegisterView";
//    }
//
//    @PostMapping("/register")
//    public String handleRegister(@Valid @ModelAttribute("register") UserCreationDto registrationDto,
//                                 BindingResult bindingResult) {
//        if (bindingResult.hasErrors()) {
//            return "RegisterView";
//        }
//
//        try {
//            User user = mapperHelper.(registrationDto);
//
//            userService.cre(user);
//            return "redirect:/ti/auth/login";
//        } catch (DuplicateEntityException e) {
//            bindingResult.rejectValue("username", "registration_error", e.getMessage());
//        }
//
//        return "RegisterView";
//    }
}
