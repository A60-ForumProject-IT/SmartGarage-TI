package com.telerikacademy.web.smartgarageti.services;

import com.telerikacademy.web.smartgarageti.exceptions.EntityNotFoundException;
import com.telerikacademy.web.smartgarageti.helpers.MapperHelper;
import com.telerikacademy.web.smartgarageti.helpers.PasswordGenerator;
import com.telerikacademy.web.smartgarageti.helpers.PasswordHasher;
import com.telerikacademy.web.smartgarageti.helpers.PermissionHelper;
import com.telerikacademy.web.smartgarageti.models.FilteredUserOptions;
import com.telerikacademy.web.smartgarageti.models.Role;
import com.telerikacademy.web.smartgarageti.models.User;
import com.telerikacademy.web.smartgarageti.models.dto.ChangePasswordDto;
import com.telerikacademy.web.smartgarageti.models.dto.ForgottenPasswordDto;
import com.telerikacademy.web.smartgarageti.models.dto.UserCreationDto;
import com.telerikacademy.web.smartgarageti.models.dto.UserDto;
import com.telerikacademy.web.smartgarageti.repositories.contracts.UserRepository;
import com.telerikacademy.web.smartgarageti.services.contracts.RoleService;
import com.telerikacademy.web.smartgarageti.services.contracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private static final String INVALID_PERMISSION = "You dont have permissions! Only employees can do this operation.";
    public static final String CAN_T_EDIT_INFORMATION_IN_OTHER_USER_ACCOUNTS = "You can't edit information in other user accounts.";
    public static final String OLD_PASSWORD_IS_INCORRECT = "Old password is incorrect.";
    public static final String PASSWORD_CAN_T_BE_THE_SAME_AS_THE_OLD_PASSWORD = "New password can't be the same as the old password.";
    public static final String NEW_PASSWORD_AND_CONFIRM_PASSWORD_DO_NOT_MATCH = "New password and confirm password do not match.";
    public static final String CAN_T_DELETE_OTHER_USERS = "You can't delete other users.";
    @Value("${spring.mail.username}")
    private String smtpEmail;

    @Value("${spring.mail.password}")
    private String smtpPasswordFromConfig;

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final EmailService emailService;


    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleService roleService, EmailService emailService) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.emailService = emailService;
    }

    @Override
    public User getByUsername(String username) {
        return userRepository.getByUsername(username);
    }

    @Override
    public User getUserById(User employee, int id) {
        PermissionHelper.isEmployee(employee, INVALID_PERMISSION);
        return userRepository.getUserById(id);
    }

    @Override
    public User getUserById(int id) {
        return userRepository.getUserById(id);
    }

    @Override
    public UserDto createCustomerProfile(User employee, UserCreationDto userCreationDto) {
        Role role = roleService.getRoleById(1);
        PermissionHelper.isEmployee(employee, INVALID_PERMISSION);
        String randomPassword = PasswordGenerator.generateRandomPassword();
        User user = MapperHelper.toUserEntity(userCreationDto, randomPassword, role);
        userRepository.create(user);

        emailService.sendEmail(
                userCreationDto.getSmtpEmail(),
                userCreationDto.getSmtpPassword(),
                user.getEmail(),
                "Welcome to Smart Garage",
                "Your username is: " + user.getUsername() + " Your password is: " + randomPassword
        );

        return MapperHelper.toUserDto(user);
    }

    @Override
    public void resetPassword(ForgottenPasswordDto forgottenPasswordDto) {
        User user = userRepository.getUserByEmail(forgottenPasswordDto.getEmail());
        if (user == null) {
            throw new EntityNotFoundException("User with email " + forgottenPasswordDto.getEmail() + " not found.");
        }

        String newPassword = PasswordGenerator.generateRandomPassword();
        user.setPassword(newPassword);
        userRepository.update(user);

        emailService.sendEmail(
                smtpEmail,
                smtpPasswordFromConfig,
                forgottenPasswordDto.getEmail(),
                "Your new password for Smart Garage TI App",
                "Your new password is: " + newPassword
        );
    }

    @Override
    public List<User> getAllUsers(User employee, FilteredUserOptions filteredUserOptions, int page, int size) {
        PermissionHelper.isEmployee(employee, INVALID_PERMISSION);
        return userRepository.getAllUsers(filteredUserOptions, page, size);
    }

    @Override
    public User updateUser(User user, User userToBeEdited) {
        PermissionHelper.isSameUser(user, userToBeEdited, CAN_T_EDIT_INFORMATION_IN_OTHER_USER_ACCOUNTS);
        boolean duplicateExists = true;

        try {
            User existingUser = userRepository.getByUsername(userToBeEdited.getUsername());
            if (existingUser.getId() == userToBeEdited.getId()) {
                duplicateExists = false;
            }
        } catch (EntityNotFoundException e) {
            duplicateExists = false;
        }

        if (duplicateExists) {
            throw new EntityNotFoundException("User with %s %s already exists.", "username", userToBeEdited.getUsername());
        }

        userRepository.update(userToBeEdited);
        return userToBeEdited;
    }

    @Override
    public void changePassword(User user, User userToChangePassword, ChangePasswordDto changePasswordDto) {
        PermissionHelper.isSameUser(user, userToChangePassword, CAN_T_EDIT_INFORMATION_IN_OTHER_USER_ACCOUNTS);

        if (!user.getPassword().equals(changePasswordDto.getOldPassword())) {
            throw new EntityNotFoundException(OLD_PASSWORD_IS_INCORRECT);
        }

        if (changePasswordDto.getNewPassword().equals(changePasswordDto.getOldPassword())) {
            throw new EntityNotFoundException(PASSWORD_CAN_T_BE_THE_SAME_AS_THE_OLD_PASSWORD);
        }

        if (!changePasswordDto.getNewPassword().equals(changePasswordDto.getConfirmPassword())) {
            throw new EntityNotFoundException(NEW_PASSWORD_AND_CONFIRM_PASSWORD_DO_NOT_MATCH);
        }

        user.setPassword(changePasswordDto.getNewPassword());
        userRepository.update(user);
    }

    @Override
    public void deleteUser(User user, User userToBeDeleted) {
        PermissionHelper.isEmployeeOrSameUser(user, userToBeDeleted, CAN_T_DELETE_OTHER_USERS);
        userRepository.delete(userToBeDeleted);
    }
}
