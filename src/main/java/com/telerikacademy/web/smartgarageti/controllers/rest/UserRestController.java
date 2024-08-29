package com.telerikacademy.web.smartgarageti.controllers.rest;

import com.telerikacademy.web.smartgarageti.exceptions.AuthenticationException;
import com.telerikacademy.web.smartgarageti.exceptions.EntityNotFoundException;
import com.telerikacademy.web.smartgarageti.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.smartgarageti.helpers.AuthenticationHelper;
import com.telerikacademy.web.smartgarageti.helpers.MapperHelper;
import com.telerikacademy.web.smartgarageti.helpers.PermissionHelper;
import com.telerikacademy.web.smartgarageti.models.FilteredUserOptions;
import com.telerikacademy.web.smartgarageti.models.User;
import com.telerikacademy.web.smartgarageti.models.dto.*;
import com.telerikacademy.web.smartgarageti.services.contracts.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    public static final String PASSWORD_CHANGED_SUCCESSFULLY = "Password changed successfully.";
    public static final String IS_DELETED_SUCCESSFULLY = "User is deleted successfully.";
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
                                  @RequestParam(defaultValue = "asc") String sortDirection,
                                  @RequestParam(defaultValue = "0") int page,   // New parameter for page number
                                  @RequestParam(defaultValue = "10") int size) {
        try {
            User employee = authenticationHelper.tryGetUser(headers);
            // Validate the 'sortBy' parameter
            Sort sort = Sort.unsorted();  // Default to unsorted
            if (sortBy != null && !sortBy.trim().isEmpty()) {  // Check for null and empty
                sort = Sort.by("asc".equalsIgnoreCase(sortDirection) ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
            }
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<User> userPage = userService.getAllUsers(employee, username, email, phoneNumber, vehicleBrand, visitDateFrom, visitDateTo, pageable);

            // Return only the content of the page as a List
            return userPage.getContent();
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

    @PutMapping("/{id}/password-change")
    public ResponseEntity<String> changePassword(@RequestHeader HttpHeaders headers, @PathVariable int id, @Valid @RequestBody ChangePasswordDto changePasswordDto) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            User userToChangePassword = userService.getUserById(id);
            userService.changePassword(user,userToChangePassword,changePasswordDto);
            return ResponseEntity.ok(PASSWORD_CHANGED_SUCCESSFULLY);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            User userToBeDeleted = userService.getUserById(id);
            userService.deleteUser(user, userToBeDeleted);
            return ResponseEntity.ok(IS_DELETED_SUCCESSFULLY);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
}
