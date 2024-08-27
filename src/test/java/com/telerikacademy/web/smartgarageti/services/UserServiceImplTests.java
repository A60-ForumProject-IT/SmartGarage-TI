package com.telerikacademy.web.smartgarageti.services;

import com.telerikacademy.web.smartgarageti.exceptions.EntityNotFoundException;
import com.telerikacademy.web.smartgarageti.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.smartgarageti.helpers.MapperHelper;
import com.telerikacademy.web.smartgarageti.helpers.PasswordGenerator;
import com.telerikacademy.web.smartgarageti.helpers.PermissionHelper;
import com.telerikacademy.web.smartgarageti.helpers.TestHelpers;
import com.telerikacademy.web.smartgarageti.models.FilteredUserOptions;
import com.telerikacademy.web.smartgarageti.models.Role;
import com.telerikacademy.web.smartgarageti.models.User;
import com.telerikacademy.web.smartgarageti.models.dto.ChangePasswordDto;
import com.telerikacademy.web.smartgarageti.models.dto.ForgottenPasswordDto;
import com.telerikacademy.web.smartgarageti.models.dto.UserCreationDto;
import com.telerikacademy.web.smartgarageti.models.dto.UserDto;
import com.telerikacademy.web.smartgarageti.repositories.contracts.RoleRepository;
import com.telerikacademy.web.smartgarageti.repositories.contracts.UserRepository;
import com.telerikacademy.web.smartgarageti.services.contracts.RoleService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static com.telerikacademy.web.smartgarageti.services.UserServiceImpl.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTests {

    public static final String DONT_HAVE_PERMISSIONS_ONLY_EMPLOYEES_CAN_DO_THIS_OPERATION = "You dont have permissions! Only employees can do this operation.";
    public static final String OLD_PASSWORD_IS_INCORRECT_DOES_NOT_EXIST = "This Old password is incorrect. does not exist!";
    public static final String PASSWORD_CAN_T_BE_THE_SAME_AS_THE_OLD_PASSWORD_DOES_NOT_EXIST = "This New password can't be the same as the old password. does not exist!";
    @Mock
    UserRepository userRepository;

    @Mock
    RoleRepository roleRepository;

    @Mock
    RoleService roleService;

    @Mock
    EmailService emailService;

    private String smtpEmail = "smtp@test.com";
    private String smtpPasswordFromConfig = "smtpPassword";

    @InjectMocks
    UserServiceImpl userService;
    @InjectMocks
    RoleServiceImpl roleServiceImpl;

    @BeforeEach
    void setUp() {
        // Инжектиране на стойности на полетата, които се инжектират с @Value в реалния код
        ReflectionTestUtils.setField(userService, "smtpEmail", "smtp@test.com");
        ReflectionTestUtils.setField(userService, "smtpPasswordFromConfig", "smtpPassword");
    }

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
        assertThrows(UnauthorizedOperationException.class, () -> userService.getUserById(mockEmployee, 1));
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

            assertThrows(UnauthorizedOperationException.class, () -> userService.createCustomerProfile(mockUser, userCreationDto));
        }
    }

    @Test
    void resetPassword_ShouldResetPassword_WhenUserExists() {
        // Arrange
        String email = "test@test.com";
        String newPassword = "newRandomPassword%";
        User mockUser = TestHelpers.createMockUser();
        mockUser.setEmail(email);
        ForgottenPasswordDto forgottenPasswordDto = new ForgottenPasswordDto();
        forgottenPasswordDto.setEmail(email);

        Mockito.when(userRepository.getUserByEmail(email)).thenReturn(mockUser);

        // Mocking static method for password generation
        try (MockedStatic<PasswordGenerator> passwordGeneratorMock = Mockito.mockStatic(PasswordGenerator.class)) {
            passwordGeneratorMock.when(PasswordGenerator::generateRandomPassword).thenReturn(newPassword);

            // Act
            userService.resetPassword(forgottenPasswordDto);

            // Assert
            Mockito.verify(userRepository).update(mockUser);
            Mockito.verify(emailService).sendEmail(
                    smtpEmail,
                    smtpPasswordFromConfig,
                    email,
                    "Your new password for Smart Garage TI App",
                    "Your new password is: " + newPassword
            );
            Assertions.assertEquals(newPassword, mockUser.getPassword());
        }
    }


    @Test
    void resetPassword_ShouldThrowEntityNotFoundException_WhenUserDoesNotExist() {
        // Arrange
        String email = "nonexistent@test.com";
        ForgottenPasswordDto forgottenPasswordDto = new ForgottenPasswordDto();
        forgottenPasswordDto.setEmail(email);

        Mockito.when(userRepository.getUserByEmail(email)).thenReturn(null);

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            userService.resetPassword(forgottenPasswordDto);
        });
    }

    @Test
    void getRoleById_ReturnsRole_WhenIdExists() {
        // Arrange
        int roleId = 1;
        Role mockRole = new Role();
        mockRole.setId(roleId);
        mockRole.setName("Admin");

        Mockito.when(roleRepository.getRoleById(roleId)).thenReturn(mockRole);

        // Act
        Role result = roleServiceImpl.getRoleById(roleId);

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals("Admin", result.getName());
        Mockito.verify(roleRepository, times(1)).getRoleById(roleId);
    }

    @Test
    void getRoleById_ReturnsNull_WhenIdDoesNotExists() {
        // Arrange
        int roleId = 999;

        Mockito.when(roleRepository.getRoleById(roleId)).thenReturn(null);

        // Act
        Role result = roleServiceImpl.getRoleById(roleId);

        // Assert
        Assertions.assertNull(result);
        Mockito.verify(roleRepository, times(1)).getRoleById(roleId);
    }

    @Test
    void getAllUsers_ReturnsListOfUsers_WhenEmployeeHasPermission() {
        // Arrange
        User mockEmployee = TestHelpers.createMockUserEmployee();
        List<User> mockUsers = Arrays.asList(TestHelpers.createMockUser(), TestHelpers.createMockUser());

        // Примерни аргументи за FilteredUserOptions
        String username = "MockUsername";
        String email = "mockemail@test.com";
        String phoneNumber = "0895999999";
        String vehicleBrand = "MockBrand";
        LocalDate visitDateFrom = LocalDate.of(2023, 1, 1);
        LocalDate visitDateTo = LocalDate.of(2023, 12, 31);
        String sortBy = "username";
        String sortOrder = "asc";

        FilteredUserOptions filteredUserOptions = new FilteredUserOptions(
                username, email, phoneNumber, vehicleBrand, visitDateFrom, visitDateTo, sortBy, sortOrder
        );

        int page = 0;
        int size = 10;

        Mockito.when(userRepository.getAllUsers(filteredUserOptions, page, size)).thenReturn(mockUsers);

        // Act
        List<User> result = userService.getAllUsers(mockEmployee, filteredUserOptions, page, size);

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());
        Mockito.verify(userRepository, times(1)).getAllUsers(filteredUserOptions, page, size);
    }

    @Test
    void updateUser_ShouldUpdateUser_WhenNoDuplicateExists() {
        // Arrange
        User mockUser = TestHelpers.createMockUser();
        User mockUserToBeEdited = TestHelpers.createMockUser();
        mockUserToBeEdited.setId(2);
        mockUserToBeEdited.setUsername("NewUsername");

        Mockito.when(userRepository.getByUsername(mockUserToBeEdited.getUsername())).thenThrow(new EntityNotFoundException());

        try (MockedStatic<PermissionHelper> permissionHelperMock = Mockito.mockStatic(PermissionHelper.class)) {
            User result = userService.updateUser(mockUser, mockUserToBeEdited);

            Assertions.assertNotNull(result);
            Assertions.assertEquals(mockUserToBeEdited.getUsername(), result.getUsername());
            Mockito.verify(userRepository, times(1)).update(mockUserToBeEdited);
        }
    }

    @Test
    void updateUser_ShouldThrowEntityNotFoundException_WhenDuplicateExists() {
        // Arrange
        User mockUser = TestHelpers.createMockUser();
        User mockUserToBeEdited = TestHelpers.createMockUser();
        mockUserToBeEdited.setId(2);
        mockUserToBeEdited.setUsername("ExistingUsername");

        User existingUser = TestHelpers.createMockUser();
        existingUser.setId(3);  // Different ID to simulate a duplicate username
        existingUser.setUsername("ExistingUsername");

        Mockito.when(userRepository.getByUsername(mockUserToBeEdited.getUsername())).thenReturn(existingUser);

        try (MockedStatic<PermissionHelper> permissionHelperMock = Mockito.mockStatic(PermissionHelper.class)) {

            EntityNotFoundException thrown = Assertions.assertThrows(EntityNotFoundException.class, () -> {
                userService.updateUser(mockUser, mockUserToBeEdited);
            });

            Assertions.assertEquals("User with username %s already exists. with username ExistingUsername not found.", thrown.getMessage());
            Mockito.verify(userRepository, times(0)).update(mockUserToBeEdited);
        }
    }


    @Test
    void updateUser_ShouldThrowUnauthorizedOperationException_WhenUserIsNotAllowedToEdit() {
        // Arrange
        User mockUser = TestHelpers.createMockUser();
        User mockUserToBeEdited = TestHelpers.createMockUser();
        mockUserToBeEdited.setId(2);

        try (MockedStatic<PermissionHelper> permissionHelperMock = Mockito.mockStatic(PermissionHelper.class)) {
            permissionHelperMock.when(() -> PermissionHelper.isSameUser(mockUser, mockUserToBeEdited, CAN_T_EDIT_INFORMATION_IN_OTHER_USER_ACCOUNTS))
                    .thenThrow(new UnauthorizedOperationException(CAN_T_EDIT_INFORMATION_IN_OTHER_USER_ACCOUNTS));

            // Act & Assert
            UnauthorizedOperationException thrown = Assertions.assertThrows(UnauthorizedOperationException.class, () -> {
                userService.updateUser(mockUser, mockUserToBeEdited);
            });

            Assertions.assertEquals(CAN_T_EDIT_INFORMATION_IN_OTHER_USER_ACCOUNTS, thrown.getMessage());
            Mockito.verify(userRepository, times(0)).update(mockUserToBeEdited);
        }
    }

    @Test
    void updateUser_ShouldNotThrowException_WhenUserHasSameId() {
        // Arrange
        User mockUser = TestHelpers.createMockUser();
        User mockUserToBeEdited = TestHelpers.createMockUser();
        mockUserToBeEdited.setId(2);
        mockUserToBeEdited.setUsername("ExistingUsername");

        Mockito.when(userRepository.getByUsername(mockUserToBeEdited.getUsername())).thenReturn(mockUserToBeEdited);

        try (MockedStatic<PermissionHelper> permissionHelperMock = Mockito.mockStatic(PermissionHelper.class)) {

            // Act
            User result = userService.updateUser(mockUser, mockUserToBeEdited);

            // Assert
            Assertions.assertNotNull(result);
            Assertions.assertEquals(mockUserToBeEdited.getUsername(), result.getUsername());
            Mockito.verify(userRepository, times(1)).update(mockUserToBeEdited);
        }
    }
    @Test
    void changePassword_ShouldChangePassword_WhenValidDataProvided() {
        // Arrange
        User mockUser = TestHelpers.createMockUser();
        ChangePasswordDto changePasswordDto = new ChangePasswordDto();
        changePasswordDto.setOldPassword(mockUser.getPassword());
        changePasswordDto.setNewPassword("newPassword");
        changePasswordDto.setConfirmPassword("newPassword");

        try (MockedStatic<PermissionHelper> permissionHelperMock = Mockito.mockStatic(PermissionHelper.class)) {
            // Act
            userService.changePassword(mockUser, mockUser, changePasswordDto);

            // Assert
            Mockito.verify(userRepository, Mockito.times(1)).update(mockUser);
            Assertions.assertEquals("newPassword", mockUser.getPassword());
        }
    }

    @Test
    void changePassword_ShouldThrowEntityNotFoundException_WhenOldPasswordIsIncorrect() {
        // Arrange
        User mockUser = TestHelpers.createMockUser();
        ChangePasswordDto changePasswordDto = new ChangePasswordDto();
        changePasswordDto.setOldPassword("wrongPassword");
        changePasswordDto.setNewPassword("newPassword");
        changePasswordDto.setConfirmPassword("newPassword");

        try (MockedStatic<PermissionHelper> permissionHelperMock = Mockito.mockStatic(PermissionHelper.class)) {
            // Act & Assert
            EntityNotFoundException thrown = Assertions.assertThrows(EntityNotFoundException.class, () -> {
                userService.changePassword(mockUser, mockUser, changePasswordDto);
            });

            Assertions.assertEquals(OLD_PASSWORD_IS_INCORRECT_DOES_NOT_EXIST, thrown.getMessage());
            Mockito.verify(userRepository, Mockito.times(0)).update(mockUser);
        }
    }

    @Test
    void changePassword_ShouldThrowEntityNotFoundException_WhenNewPasswordMatchesOldPassword() {
        // Arrange
        User mockUser = TestHelpers.createMockUser();
        ChangePasswordDto changePasswordDto = new ChangePasswordDto();
        changePasswordDto.setOldPassword(mockUser.getPassword());
        changePasswordDto.setNewPassword(mockUser.getPassword());
        changePasswordDto.setConfirmPassword(mockUser.getPassword());

        try (MockedStatic<PermissionHelper> permissionHelperMock = Mockito.mockStatic(PermissionHelper.class)) {
            // Act & Assert
            EntityNotFoundException thrown = Assertions.assertThrows(EntityNotFoundException.class, () -> {
                userService.changePassword(mockUser, mockUser, changePasswordDto);
            });

            Assertions.assertEquals(PASSWORD_CAN_T_BE_THE_SAME_AS_THE_OLD_PASSWORD_DOES_NOT_EXIST, thrown.getMessage());
            Mockito.verify(userRepository, Mockito.times(0)).update(mockUser);
        }
    }

    @Test
    void changePassword_ShouldThrowEntityNotFoundException_WhenNewPasswordAndConfirmPasswordDoNotMatch() {
        // Arrange
        User mockUser = TestHelpers.createMockUser();
        ChangePasswordDto changePasswordDto = new ChangePasswordDto();
        changePasswordDto.setOldPassword(mockUser.getPassword());
        changePasswordDto.setNewPassword("newPassword");
        changePasswordDto.setConfirmPassword("differentPassword");

        try (MockedStatic<PermissionHelper> permissionHelperMock = Mockito.mockStatic(PermissionHelper.class)) {
            // Act & Assert
            EntityNotFoundException thrown = Assertions.assertThrows(EntityNotFoundException.class, () -> {
                userService.changePassword(mockUser, mockUser, changePasswordDto);
            });

            Assertions.assertEquals("This New password and confirm password do not match. does not exist!", thrown.getMessage());
            Mockito.verify(userRepository, Mockito.times(0)).update(mockUser);
        }
    }

    @Test
    void deleteUser_ShouldDeleteUser_WhenUserHasPermission() {
        // Arrange
        User mockEmployee = TestHelpers.createMockUserEmployee();
        User mockUserToBeDeleted = TestHelpers.createMockUser();

        try (MockedStatic<PermissionHelper> permissionHelperMock = Mockito.mockStatic(PermissionHelper.class)) {
            // Act
            userService.deleteUser(mockEmployee, mockUserToBeDeleted);

            // Assert
            Mockito.verify(userRepository, Mockito.times(1)).delete(mockUserToBeDeleted);
        }
    }

    @Test
    void deleteUser_ShouldThrowUnauthorizedOperationException_WhenUserHasNoPermission() {
        // Arrange
        User mockUser = TestHelpers.createMockUser();
        User mockUserToBeDeleted = TestHelpers.createMockUser();

        try (MockedStatic<PermissionHelper> permissionHelperMock = Mockito.mockStatic(PermissionHelper.class)) {
            permissionHelperMock.when(() -> PermissionHelper.isEmployeeOrSameUser(mockUser, mockUserToBeDeleted, CAN_T_DELETE_OTHER_USERS))
                    .thenThrow(new UnauthorizedOperationException(CAN_T_DELETE_OTHER_USERS));

            // Act & Assert
            UnauthorizedOperationException thrown = Assertions.assertThrows(UnauthorizedOperationException.class, () -> {
                userService.deleteUser(mockUser, mockUserToBeDeleted);
            });

            Assertions.assertEquals(CAN_T_DELETE_OTHER_USERS, thrown.getMessage());
            Mockito.verify(userRepository, Mockito.times(0)).delete(mockUserToBeDeleted);
        }
    }
}


