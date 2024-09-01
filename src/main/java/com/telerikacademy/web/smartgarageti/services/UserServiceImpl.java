package com.telerikacademy.web.smartgarageti.services;

import com.telerikacademy.web.smartgarageti.exceptions.EntityNotFoundException;
import com.telerikacademy.web.smartgarageti.helpers.MapperHelper;
import com.telerikacademy.web.smartgarageti.helpers.PasswordGenerator;
import com.telerikacademy.web.smartgarageti.helpers.PasswordHasher;
import com.telerikacademy.web.smartgarageti.helpers.PermissionHelper;
import com.telerikacademy.web.smartgarageti.models.Avatar;
import com.telerikacademy.web.smartgarageti.models.FilteredUserOptions;
import com.telerikacademy.web.smartgarageti.models.Role;
import com.telerikacademy.web.smartgarageti.models.User;
import com.telerikacademy.web.smartgarageti.models.dto.ChangePasswordDto;
import com.telerikacademy.web.smartgarageti.models.dto.ForgottenPasswordDto;
import com.telerikacademy.web.smartgarageti.models.dto.UserCreationDto;
import com.telerikacademy.web.smartgarageti.models.dto.UserDto;
import com.telerikacademy.web.smartgarageti.repositories.contracts.UserRepository;
import com.telerikacademy.web.smartgarageti.services.contracts.AvatarService;
import com.telerikacademy.web.smartgarageti.services.contracts.RoleService;
import com.telerikacademy.web.smartgarageti.services.contracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
    private final AvatarService avatarService;


    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleService roleService, EmailService emailService, AvatarService avatarService) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.emailService = emailService;
        this.avatarService = avatarService;
    }

    @Override
    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User", "username", username));
    }

    @Override
    public User getUserById(User employee, int id) {
        PermissionHelper.isEmployee(employee, INVALID_PERMISSION);
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User", id));
    }

    @Override
    public User getUserById(int id, User currentUser) {
        PermissionHelper.isEmployeeOrSameUser(currentUser, currentUser, INVALID_PERMISSION);
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User", id));
    }

    @Override
    public User getUserById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User", id));
    }

    @Override
    public UserDto createCustomerProfile(User employee, UserCreationDto userCreationDto) {
        PermissionHelper.isEmployee(employee, INVALID_PERMISSION);
        return userRegistration(userCreationDto);
    }

    @Override
    public UserDto createCustomerProfile(UserCreationDto userCreationDto) {
        return userRegistration(userCreationDto);
    }

    @Override
    public void resetPassword(ForgottenPasswordDto forgottenPasswordDto) {
        User user = userRepository.findByEmail(forgottenPasswordDto.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("User", "email", forgottenPasswordDto.getEmail()));
        if (user == null) {
            throw new EntityNotFoundException("User with email " + forgottenPasswordDto.getEmail() + " not found.");
        }

        String newPassword = PasswordGenerator.generateRandomPassword();
        user.setPassword(newPassword);
        userRepository.save(user);

        emailService.sendEmail(
                forgottenPasswordDto.getEmail(),
                "Your new password for Smart Garage TI App",
                "Your new password is: " + newPassword
        );
    }

    @Override
    public Page<User> getAllUsers(User employee, String username, String email, String phoneNumber, String vehicleBrand,
                                  LocalDate visitDateFrom, LocalDate visitDateTo, Pageable pageable) {
        PermissionHelper.isEmployee(employee, INVALID_PERMISSION);
        return userRepository.findAllFiltered(username,email,phoneNumber,vehicleBrand,visitDateFrom,
                visitDateTo,pageable);
    }

    @Override
    public User updateUser(User user, User userToBeEdited) {
        PermissionHelper.isSameUser(user, userToBeEdited, CAN_T_EDIT_INFORMATION_IN_OTHER_USER_ACCOUNTS);
        boolean duplicateExists = true;

        try {
            User existingUser = userRepository.findByUsername(userToBeEdited.getUsername())
                    .orElseThrow(() -> new EntityNotFoundException("User", "username", userToBeEdited.getUsername()));
            if (existingUser.getId() == userToBeEdited.getId()) {
                duplicateExists = false;
            }
        } catch (EntityNotFoundException e) {
            duplicateExists = false;
        }

        if (duplicateExists) {
            throw new EntityNotFoundException("User with username %s already exists.", "username", userToBeEdited.getUsername());
        }

        userRepository.save(userToBeEdited);
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
        userRepository.save(user);
    }

    @Override
    public void deleteUser(User user, User userToBeDeleted) {
        PermissionHelper.isEmployeeOrSameUser(user, userToBeDeleted, CAN_T_DELETE_OTHER_USERS);
        userRepository.delete(userToBeDeleted);
    }

    private UserDto userRegistration(UserCreationDto userCreationDto) {
        Role role = roleService.getRoleById(1);
        String randomPassword = PasswordGenerator.generateRandomPassword();
        User user = MapperHelper.toUserEntity(userCreationDto, randomPassword, role);
        Avatar defaultAvatar = avatarService.initializeDefaultAvatar();
        user.setAvatar(defaultAvatar);
        userRepository.save(user);
        emailService.sendEmail(
                user.getEmail(),
                "Welcome to Smart Garage",
                "Your username is: " + user.getUsername() + " Your password is: " + randomPassword
        );

        return MapperHelper.toUserDto(user);
    }

    @Override
    public List<String> findUsernamesByTerm(String term) {
        return userRepository.findUsernamesByTerm(term);
    }
}
