package com.telerikacademy.web.smartgarageti.services;

import com.telerikacademy.web.smartgarageti.exceptions.EntityNotFoundException;
import com.telerikacademy.web.smartgarageti.helpers.MapperHelper;
import com.telerikacademy.web.smartgarageti.helpers.PasswordGenerator;
import com.telerikacademy.web.smartgarageti.helpers.PasswordHasher;
import com.telerikacademy.web.smartgarageti.helpers.PermissionHelper;
import com.telerikacademy.web.smartgarageti.models.Role;
import com.telerikacademy.web.smartgarageti.models.User;
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
    public UserDto createCustomerProfile(User employee, UserCreationDto userCreationDto) {
        Role role = roleService.getRoleById(1);
        PermissionHelper.isEmployee(employee, INVALID_PERMISSION);
        String randomPassword = PasswordGenerator.generateRandomPassword();
        System.out.println("Generated Password: " + randomPassword);
        String hashedPassword = PasswordHasher.hashPassword(randomPassword);

        User user = MapperHelper.toUserEntity(userCreationDto, randomPassword, role);
        userRepository.create(user);

        // Изпращане на имейл с данните за вход, използвайки smtpEmail и smtpPassword
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
    public List<User> getAllUsers(User employee) {
        PermissionHelper.isEmployee(employee, INVALID_PERMISSION);
        return userRepository.getAllUsers();
    }
}
