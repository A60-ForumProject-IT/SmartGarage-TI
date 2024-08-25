package com.telerikacademy.web.smartgarageti.controllers.rest;

import com.telerikacademy.web.smartgarageti.exceptions.AuthenticationException;
import com.telerikacademy.web.smartgarageti.exceptions.EntityNotFoundException;
import com.telerikacademy.web.smartgarageti.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.smartgarageti.helpers.AuthenticationHelper;
import com.telerikacademy.web.smartgarageti.helpers.PermissionHelper;
import com.telerikacademy.web.smartgarageti.models.User;
import com.telerikacademy.web.smartgarageti.models.dto.ForgottenPasswordDto;
import com.telerikacademy.web.smartgarageti.models.dto.UserCreationDto;
import com.telerikacademy.web.smartgarageti.models.dto.UserDto;
import com.telerikacademy.web.smartgarageti.services.contracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    private final UserService userService;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public UserRestController(UserService userService, AuthenticationHelper authenticationHelper) {
        this.userService = userService;
        this.authenticationHelper = authenticationHelper;
    }

    @GetMapping
    public List<User> getAllUsers(@RequestHeader HttpHeaders headers) {
        try {
            User employee = authenticationHelper.tryGetUser(headers);
            return userService.getAllUsers(employee);
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public User getUserById(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        try {
            User employee = authenticationHelper.tryGetUser(headers);
            return userService.getUserById(employee, id);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PostMapping("/customers")
    public UserDto createCustomer(@RequestHeader HttpHeaders headers, @RequestBody UserCreationDto userCreationDto) {
        try {
            User employee = authenticationHelper.tryGetUser(headers);
           // PermissionHelper.isEmployee(employee, "Only employees can create new customers.");
            return userService.createCustomerProfile(employee,userCreationDto);
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
    @PostMapping("/forgot-password")
    public void forgotPassword(@RequestBody ForgottenPasswordDto forgottenPasswordDto) {
        userService.resetPassword(forgottenPasswordDto);
    }
}
