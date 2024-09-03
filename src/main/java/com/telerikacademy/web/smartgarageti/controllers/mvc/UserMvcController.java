package com.telerikacademy.web.smartgarageti.controllers.mvc;

import com.telerikacademy.web.smartgarageti.exceptions.AuthenticationException;
import com.telerikacademy.web.smartgarageti.exceptions.EntityNotFoundException;
import com.telerikacademy.web.smartgarageti.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.smartgarageti.helpers.AuthenticationHelper;
import com.telerikacademy.web.smartgarageti.helpers.PermissionHelper;
import com.telerikacademy.web.smartgarageti.models.Brand;
import com.telerikacademy.web.smartgarageti.models.User;
import com.telerikacademy.web.smartgarageti.models.dto.ChangePasswordDto;
import com.telerikacademy.web.smartgarageti.models.dto.UserEditInfoDto;
import com.telerikacademy.web.smartgarageti.services.contracts.AvatarService;
import com.telerikacademy.web.smartgarageti.services.contracts.BrandService;
import com.telerikacademy.web.smartgarageti.services.contracts.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/ti/users")
public class UserMvcController {

    private final UserService userService;
    private final AuthenticationHelper authenticationHelper;
    private final BrandService brandService;
    private final AvatarService avatarService;

    @Autowired
    public UserMvcController(UserService userService, AuthenticationHelper authenticationHelper, BrandService brandService, AvatarService avatarService) {
        this.userService = userService;
        this.authenticationHelper = authenticationHelper;
        this.brandService = brandService;
        this.avatarService = avatarService;
    }

    @ModelAttribute("allVehicleBrands")
    public List<Brand> populateAllVehicleBrands() {
        // Получаване на списъка с всички брандове на коли от VehicleService
        return brandService.findAllBrands();
    }

    @ModelAttribute("isAuthenticated")
    public boolean isAuthenticated(HttpSession session) {
        return session.getAttribute("currentUser") != null;
    }

    // Проверява дали потребителят има роля "Employee"
    @ModelAttribute("isEmployee")
    public boolean isEmployee(HttpSession session) {
        if (session.getAttribute("currentUser") != null) {
            User user = authenticationHelper.tryGetUserFromSession(session);
            return "Employee".equals(user.getRole().getName());
        }
        return false;
    }

    @ModelAttribute("userEditInfoDto")
    public UserEditInfoDto userEditInfoDto() {
        return new UserEditInfoDto();
    }

    @ModelAttribute("user")
    public User populateUser(HttpSession session) {
        if (session.getAttribute("currentUser") != null) {
            return authenticationHelper.tryGetUserFromSession(session);
        }
        return null;
    }

    @GetMapping
    public String getAllUsers(HttpSession session,
                              @RequestParam(required = false) String username,
                              @RequestParam(required = false) String email,
                              @RequestParam(required = false) String phoneNumber,
                              @RequestParam(required = false) String vehicleBrand,
                              @RequestParam(required = false) LocalDate visitDateFrom,
                              @RequestParam(required = false) LocalDate visitDateTo,
                              @RequestParam(required = false) String sortBy,
                              @RequestParam(defaultValue = "asc") String sortDirection,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int size,
                              Model model) {

        if (vehicleBrand != null && vehicleBrand.trim().isEmpty()) {
            vehicleBrand = null;
        }
        if (sortBy != null && sortBy.trim().isEmpty()) {
            sortBy = null;
        }

        try {
            User employee = authenticationHelper.tryGetUserFromSession(session);

            PermissionHelper.isEmployee(employee, "Only employees can access this resource.");

            Sort sort = Sort.unsorted();
            if (sortBy != null && !sortBy.trim().isEmpty()) {
                sort = Sort.by("asc".equalsIgnoreCase(sortDirection) ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
            }
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<User> userPage = userService.getAllUsers(employee, username, email, phoneNumber, vehicleBrand, visitDateFrom, visitDateTo, pageable);

            model.addAttribute("users", userPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", userPage.getTotalPages());
            model.addAttribute("totalItems", userPage.getTotalElements());
            model.addAttribute("username", username);
            model.addAttribute("email", email);
            model.addAttribute("phoneNumber", phoneNumber);
            model.addAttribute("vehicleBrand", vehicleBrand);
            model.addAttribute("visitDateFrom", visitDateFrom);
            model.addAttribute("visitDateTo", visitDateTo);
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("sortDirection", sortDirection);
            model.addAttribute("size", size);

            return "AllUsers";

        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @GetMapping("/{id}/details")
    public String showUserDetails(@PathVariable int id, Model model, HttpSession session) {
        try {
            User currentUser = authenticationHelper.tryGetUserFromSession(session);
            if (currentUser == null) {
                throw new AuthenticationException("Current user is not authenticated.");
            }
            User userToDisplay = userService.getUserById(id, currentUser);

            model.addAttribute("user", userToDisplay);
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("userEditInfoDto", new UserEditInfoDto());
            model.addAttribute("avatarUrl", userToDisplay.getAvatar().getAvatar());
            model.addAttribute("isEditing", false);
            model.addAttribute("isAuthenticated", true);

            return "UserDetails";
        } catch (UnauthorizedOperationException | EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.FORBIDDEN.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "404";
        } catch (AuthenticationException e) {
            return "redirect:/ti/auth/login";
        }
    }


    @PostMapping("/{id}/edit")
    public String editUser(@PathVariable int id,
                           @Valid @ModelAttribute("userEditInfoDto") UserEditInfoDto userEditInfoDto,
                           BindingResult bindingResult,
                           Model model,
                           HttpSession session) {
        model.addAttribute("isEditing", true);
        if (bindingResult.hasErrors()) {
            model.addAttribute("userEditInfoDto", userEditInfoDto);
            model.addAttribute("user", userService.getUserById(id));
            return "UserDetails";
        }

        try {
            User currentUser = authenticationHelper.tryGetUserFromSession(session);
            User userToEdit = userService.getUserById(id, currentUser);

            model.addAttribute("user", userService.getUserById(id));
            // Преобразуване от DTO към User
            userToEdit.setFirstName(userEditInfoDto.getFirstName());
            userToEdit.setLastName(userEditInfoDto.getLastName());
            userToEdit.setPhoneNumber(userEditInfoDto.getPhoneNumber());
            userToEdit.setEmail(userEditInfoDto.getEmail());

            userService.updateUser(currentUser, userToEdit);
            return "redirect:/ti/users/" + id + "/details"; // Пренасочване след успешна промяна
        } catch (UnauthorizedOperationException | EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.FORBIDDEN.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "404";
        } catch (AuthenticationException e) {
            return "redirect:/ti/auth/login";
        }
    }


    @PostMapping("/upload-photo")
    public String uploadPhoto(@RequestParam("avatarFile") MultipartFile avatarFile, HttpSession session, Model model) {
        try {
            User currentUser = authenticationHelper.tryGetUserFromSession(session);
            avatarService.uploadAvatar(currentUser, avatarFile);
            return "redirect:/ti/users/" + currentUser.getId() + "/details";
        } catch (IOException e) {
            model.addAttribute("errorMessage", "Error while uploading the photo.");
            return "404";
        } catch (AuthenticationException e) {
            return "redirect:/ti/auth/login";
        }
    }

    @GetMapping("/{id}/password-change")
    public String showChangePasswordForm(@PathVariable int id, Model model, HttpSession session) {
        try {
            User currentUser = authenticationHelper.tryGetUserFromSession(session);
            User userToChangePassword = userService.getUserById(id, currentUser);

            model.addAttribute("user", userToChangePassword); // Добавяне на user към модела
            model.addAttribute("changePasswordDto", new ChangePasswordDto());
            return "change-password";
        } catch (EntityNotFoundException | UnauthorizedOperationException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "change-password";
        } catch (AuthenticationException e) {
            return "redirect:/ti/auth/login";
        }
    }

    @PostMapping("/{id}/password-change")
    public String changePassword(@PathVariable int id,
                                 @Valid @ModelAttribute("changePasswordDto") ChangePasswordDto changePasswordDto,
                                 BindingResult bindingResult,
                                 HttpSession session,
                                 Model model) {
        try {
            User currentUser = authenticationHelper.tryGetUserFromSession(session);
            User userToChangePassword = userService.getUserById(id, currentUser);

            // Добави 'user' обекта в модела, за да бъде достъпен в Thymeleaf шаблона
            model.addAttribute("user", userToChangePassword);

            if (bindingResult.hasErrors()) {
                return "change-password";
            }

            if (!changePasswordDto.getOldPassword().equals(userToChangePassword.getPassword())) {
                bindingResult.rejectValue("oldPassword", "error.changePasswordDto", "Old password is incorrect.");
                return "change-password";
            }
            // Проверка дали новата парола съвпада със старата парола
            if (changePasswordDto.getNewPassword().equals(changePasswordDto.getOldPassword())) {
                bindingResult.rejectValue("newPassword", "error.changePasswordDto", "New password must be different from the old password.");
                return "change-password";
            }

            // Проверка дали новата парола съвпада с потвърждението
            if (!changePasswordDto.getNewPassword().equals(changePasswordDto.getConfirmPassword())) {
                bindingResult.rejectValue("confirmPassword", "error.changePasswordDto", "New password and confirm password must match.");
                return "change-password";
            }

            userService.changePassword(currentUser, userToChangePassword, changePasswordDto);
            model.addAttribute("successMessage", "Password changed successfully.");
            return "redirect:/ti/users/" + id + "/details";
        } catch (EntityNotFoundException | UnauthorizedOperationException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "change-password";
        } catch (AuthenticationException e) {
            return "redirect:/ti/auth/login";
        }
    }

}
