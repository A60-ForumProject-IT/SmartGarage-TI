package com.telerikacademy.web.smartgarageti.services.contracts;

import com.telerikacademy.web.smartgarageti.models.FilteredUserOptions;
import com.telerikacademy.web.smartgarageti.models.User;
import com.telerikacademy.web.smartgarageti.models.dto.ChangePasswordDto;
import com.telerikacademy.web.smartgarageti.models.dto.ForgottenPasswordDto;
import com.telerikacademy.web.smartgarageti.models.dto.UserCreationDto;
import com.telerikacademy.web.smartgarageti.models.dto.UserDto;

import java.util.List;

public interface UserService {
    User getByUsername(String username);

    User getUserById(User employee, int id);
    User getUserById(int id);

    UserDto createCustomerProfile(User employee, UserCreationDto userCreationDto);

    void resetPassword(ForgottenPasswordDto forgottenPasswordDto);

    List<User> getAllUsers(User employee, FilteredUserOptions filteredUserOptions, int page, int size);

    User updateUser(User user, User userToBeEdited);

    void changePassword(User user, User userToChangePassword, ChangePasswordDto changePasswordDto);

    void deleteUser(User user, User userToBeDeleted);
}
