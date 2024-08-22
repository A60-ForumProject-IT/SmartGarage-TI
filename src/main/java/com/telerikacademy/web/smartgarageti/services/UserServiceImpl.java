package com.telerikacademy.web.smartgarageti.services;

import com.telerikacademy.web.smartgarageti.helpers.MapperHelper;
import com.telerikacademy.web.smartgarageti.helpers.PasswordGenerator;
import com.telerikacademy.web.smartgarageti.helpers.PasswordHasher;
import com.telerikacademy.web.smartgarageti.helpers.PermissionHelper;
import com.telerikacademy.web.smartgarageti.models.Role;
import com.telerikacademy.web.smartgarageti.models.User;
import com.telerikacademy.web.smartgarageti.models.dto.UserCreationDto;
import com.telerikacademy.web.smartgarageti.models.dto.UserDto;
import com.telerikacademy.web.smartgarageti.repositories.contracts.UserRepository;
import com.telerikacademy.web.smartgarageti.services.contracts.RoleService;
import com.telerikacademy.web.smartgarageti.services.contracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private static final String INVALID_PERMISSION = "You dont have permissions! Only employees can do this operation.";

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
    public UserDto createCustomerProfile(UserCreationDto userCreationDto) {
        Role role = roleService.getRoleById(1);
        String randomPassword = PasswordGenerator.generateRandomPassword();
        System.out.println("Generated Password: " + randomPassword);
        String hashedPassword = PasswordHasher.hashPassword(randomPassword);

        User user = MapperHelper.toUserEntity(userCreationDto, hashedPassword, role);
        userRepository.create(user);

        // Изпращане на имейл с данните за вход, използвайки smtpEmail и smtpPassword
        emailService.sendEmail(
                userCreationDto.getSmtpEmail(),
                userCreationDto.getSmtpPassword(),
                user.getEmail(),
                "Welcome to Smart Garage",
                "Your username is: " + user.getEmail() + "\nYour password is: " + randomPassword
        );

        return MapperHelper.toUserDto(user);
    }

}
