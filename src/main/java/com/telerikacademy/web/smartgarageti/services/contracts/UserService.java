package com.telerikacademy.web.smartgarageti.services.contracts;

import com.telerikacademy.web.smartgarageti.models.FilteredUserOptions;
import com.telerikacademy.web.smartgarageti.models.User;
import com.telerikacademy.web.smartgarageti.models.dto.ChangePasswordDto;
import com.telerikacademy.web.smartgarageti.models.dto.ForgottenPasswordDto;
import com.telerikacademy.web.smartgarageti.models.dto.UserCreationDto;
import com.telerikacademy.web.smartgarageti.models.dto.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;

public interface UserService {
    User getByUsername(String username);

    User getUserById(User employee, int id);

    User getUserById(int id, User currentUser);

    User getUserById(int id);

    UserDto createCustomerProfile(User employee, UserCreationDto userCreationDto);

    UserDto createCustomerProfile( UserCreationDto userCreationDto);

    UserDto createMechanicProfile(User employee, UserCreationDto userCreationDto);

    UserDto createEmployeeProfile(User employee, UserCreationDto userCreationDto);

    void resetPassword(ForgottenPasswordDto forgottenPasswordDto);

    Page<User> getAllUsers(User employee, String username, String email, String phoneNumber, String vehicleBrand,
                           LocalDate visitDateFrom, LocalDate visitDateTo, Pageable pageable);

    User updateUser(User user, User userToBeEdited);

    void changePassword(User user, User userToChangePassword, ChangePasswordDto changePasswordDto);

    void deleteUser(User user, User userToBeDeleted);

    List<String> findUsernamesByTerm(String term);
}
