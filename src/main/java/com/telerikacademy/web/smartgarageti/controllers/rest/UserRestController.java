package com.telerikacademy.web.smartgarageti.controllers.rest;

import com.telerikacademy.web.smartgarageti.exceptions.AuthenticationException;
import com.telerikacademy.web.smartgarageti.exceptions.EntityNotFoundException;
import com.telerikacademy.web.smartgarageti.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.smartgarageti.helpers.AuthenticationHelper;
import com.telerikacademy.web.smartgarageti.helpers.MapperHelper;
import com.telerikacademy.web.smartgarageti.helpers.PermissionHelper;
import com.telerikacademy.web.smartgarageti.models.FilteredUserOptions;
import com.telerikacademy.web.smartgarageti.models.User;
import com.telerikacademy.web.smartgarageti.models.dto.ForgottenPasswordDto;
import com.telerikacademy.web.smartgarageti.models.dto.UserCreationDto;
import com.telerikacademy.web.smartgarageti.models.dto.UserDto;
import com.telerikacademy.web.smartgarageti.models.dto.UserEditInfoDto;
import com.telerikacademy.web.smartgarageti.services.contracts.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    private final UserService userService;
    private final AuthenticationHelper authenticationHelper;
    private final MapperHelper mapperHelper;

    @Autowired
    public UserRestController(UserService userService, AuthenticationHelper authenticationHelper, MapperHelper mapperHelper) {
        this.userService = userService;
        this.authenticationHelper = authenticationHelper;
        this.mapperHelper = mapperHelper;
    }

    @GetMapping
    public List<User> getAllUsers(@RequestHeader HttpHeaders headers,
                                  @RequestParam(required = false) String username,
                                  @RequestParam(required = false) String email,
                                  @RequestParam(required = false) String phoneNumber,
                                  @RequestParam(required = false) String vehicleBrand,
                                  @RequestParam(required = false) LocalDate visitDateFrom,
                                  @RequestParam(required = false) LocalDate visitDateTo,
                                  @RequestParam(required = false) String sortBy,
                                  @RequestParam(required = false) String sortOrder,
                                  @RequestParam(defaultValue = "1") int page,
                                  @RequestParam(defaultValue = "10") int size) {
        try {
            User employee = authenticationHelper.tryGetUser(headers);
            FilteredUserOptions filteredUserOptions = new FilteredUserOptions(username, email, phoneNumber, vehicleBrand, visitDateFrom, visitDateTo, sortBy, sortOrder);
            return userService.getAllUsers(employee, filteredUserOptions, page, size);
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
    public UserDto createCustomer(@RequestHeader HttpHeaders headers, @Valid @RequestBody UserCreationDto userCreationDto) {
        try {
            User employee = authenticationHelper.tryGetUser(headers);
            // PermissionHelper.isEmployee(employee, "Only employees can create new customers.");
            return userService.createCustomerProfile(employee, userCreationDto);
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

    @PutMapping("/{id}")
    public User updateUser(@RequestHeader HttpHeaders headers, @PathVariable int id, @Valid @RequestBody UserEditInfoDto userEditInfoDto) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            User userToBeEdited = mapperHelper.editUserFromDto(userEditInfoDto, id);
            return userService.updateUser(user, userToBeEdited);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
}
