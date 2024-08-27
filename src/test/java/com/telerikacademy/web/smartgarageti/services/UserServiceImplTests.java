package com.telerikacademy.web.smartgarageti.services;

import com.telerikacademy.web.smartgarageti.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.smartgarageti.helpers.MapperHelper;
import com.telerikacademy.web.smartgarageti.helpers.PasswordGenerator;
import com.telerikacademy.web.smartgarageti.helpers.PermissionHelper;
import com.telerikacademy.web.smartgarageti.helpers.TestHelpers;
import com.telerikacademy.web.smartgarageti.models.Role;
import com.telerikacademy.web.smartgarageti.models.User;
import com.telerikacademy.web.smartgarageti.models.dto.UserCreationDto;
import com.telerikacademy.web.smartgarageti.models.dto.UserDto;
import com.telerikacademy.web.smartgarageti.repositories.contracts.RoleRepository;
import com.telerikacademy.web.smartgarageti.repositories.contracts.UserRepository;
import com.telerikacademy.web.smartgarageti.services.contracts.RoleService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTests {

    public static final String DONT_HAVE_PERMISSIONS_ONLY_EMPLOYEES_CAN_DO_THIS_OPERATION = "You dont have permissions! Only employees can do this operation.";
    @Mock
    UserRepository userRepository;

    @Mock
    RoleRepository roleRepository;

    @Mock
    RoleService roleService;

    @Mock
    EmailService emailService;

    @InjectMocks
    UserServiceImpl userService;
    @InjectMocks
    RoleServiceImpl roleServiceImpl;


    @Test
    public void getUserById_ReturnsUserId_WhenIdExists() {
        User mockUser = TestHelpers.createMockUser();
        Mockito.when(userRepository.getUserById(1))
                .thenReturn(mockUser);
        User result = userService.getUserById(1);
        Assertions.assertEquals("MockUsername", result.getUsername());
    }


    @MockitoSettings(strictness = Strictness.LENIENT)
    @Test
    void getRoleById_ReturnsNull_WhenIdDoesNotExist() {
        // Трябва да проверим дали `roleService.getRoleById(1)` се извиква и дали стойността `null` е коректно върната.
        Mockito.when(roleRepository.getRoleById(1)).thenReturn(null);

        Role result = roleService.getRoleById(1);

        Assertions.assertNull(result);
    }

    @MockitoSettings(strictness = Strictness.LENIENT)
    @Test
    void getAllRoles_ReturnsListOfRoles() {
        List<Role> roles = Arrays.asList(TestHelpers.createMockRoleEmployee(), TestHelpers.createMockRoleUser());
        Mockito.when(roleRepository.getAllRoles()).thenReturn(roles);

        List<Role> result = roleServiceImpl.getAllRoles();

        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("Employee", result.get(0).getName());
        Assertions.assertEquals("Customer", result.get(1).getName());
    }

    @Test
    void getByUsername_ReturnsUser_WhenUsernameExists() {
        User mockUser = TestHelpers.createMockUser();
        Mockito.when(userRepository.getByUsername("MockUsername")).thenReturn(mockUser);

        User result = userService.getByUsername("MockUsername");

        Assertions.assertEquals("MockUsername", result.getUsername());
    }

    @Test
    void getByUsername_ReturnsNull_WhenUsernameDoesNotExist() {
        Mockito.when(userRepository.getByUsername("NonExistentUsername")).thenReturn(null);

        User result = userService.getByUsername("NonExistentUsername");

        Assertions.assertNull(result);
    }

    @Test
    void getUserById_ReturnsUser_WhenEmployeeHasPermission() {
        User mockEmployee = TestHelpers.createMockUserEmployee();
        User mockUser = TestHelpers.createMockUser();
        Mockito.when(userRepository.getUserById(1))
                .thenReturn(mockUser);

        User result = userService.getUserById(mockEmployee, 1);

        Assertions.assertEquals("MockUsername", result.getUsername());
    }

    @Test
    void getUserById_ThrowsException_WhenEmployeeHasNoPermission() {
        User mockEmployee = TestHelpers.createMockUser();
        Assertions.assertThrows(UnauthorizedOperationException.class, () -> userService.getUserById(mockEmployee, 1));
    }

    @Test
    void getUserById_ReturnsNull_WhenUserIdDoesNotExist() {
        User mockEmployee = TestHelpers.createMockUserEmployee();
        Mockito.when(userRepository.getUserById(999)).thenReturn(null);

        User result = userService.getUserById(mockEmployee, 999);

        Assertions.assertNull(result);
    }

    @Test
    void createCustomerProfile_CreatesUser_WhenEmployeeHasPermission() {
        User mockEmployee = TestHelpers.createMockUserEmployee();
        UserCreationDto userCreationDto = TestHelpers.createMockUserCreationDto();
        Role mockRole = TestHelpers.createMockRoleUser();
        User mockUser = TestHelpers.createMockUser();
        String randomPassword = "randomPassword%";
        UserDto expectedUserDto = new UserDto(mockUser.getUsername(), mockUser.getEmail(), mockUser.getPhoneNumber());

        try (MockedStatic<PasswordGenerator> passwordGeneratorMock = Mockito.mockStatic(PasswordGenerator.class);
             MockedStatic<MapperHelper> mapperHelperMock = Mockito.mockStatic(MapperHelper.class)) {

            passwordGeneratorMock.when(PasswordGenerator::generateRandomPassword).thenReturn(randomPassword);
            mapperHelperMock.when(() -> MapperHelper.toUserEntity(userCreationDto, randomPassword, mockRole)).thenReturn(mockUser);
            mapperHelperMock.when(() -> MapperHelper.toUserDto(mockUser)).thenReturn(expectedUserDto);  // Ensure to mock the correct return value

            Mockito.when(roleService.getRoleById(1)).thenReturn(mockRole);

            UserDto result = userService.createCustomerProfile(mockEmployee, userCreationDto);

            Mockito.verify(userRepository).create(mockUser);
            Mockito.verify(emailService).sendEmail(
                    userCreationDto.getSmtpEmail(),
                    userCreationDto.getSmtpPassword(),
                    mockUser.getEmail(),
                    "Welcome to Smart Garage",
                    "Your username is: " + mockUser.getUsername() + " Your password is: " + randomPassword
            );

            Assertions.assertEquals(expectedUserDto.getUsername(), result.getUsername());
        }
    }


    @Test
    void createCustomerProfile_ThrowsException_WhenEmployeeHasNoPermission() {
        User mockUser = TestHelpers.createMockUser();
        UserCreationDto userCreationDto = TestHelpers.createMockUserCreationDto();

        try (MockedStatic<PermissionHelper> permissionHelperMock = Mockito.mockStatic(PermissionHelper.class)) {

            permissionHelperMock.when(() -> PermissionHelper.isEmployee(mockUser, DONT_HAVE_PERMISSIONS_ONLY_EMPLOYEES_CAN_DO_THIS_OPERATION))
                    .thenThrow(new UnauthorizedOperationException(DONT_HAVE_PERMISSIONS_ONLY_EMPLOYEES_CAN_DO_THIS_OPERATION));

            Assertions.assertThrows(UnauthorizedOperationException.class, () -> userService.createCustomerProfile(mockUser, userCreationDto));
        }
    }
}

