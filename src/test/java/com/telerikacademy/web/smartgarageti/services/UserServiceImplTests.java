package com.telerikacademy.web.smartgarageti.services;

import com.telerikacademy.web.smartgarageti.helpers.PermissionHelper;
import com.telerikacademy.web.smartgarageti.helpers.TestHelpers;
import com.telerikacademy.web.smartgarageti.models.Role;
import com.telerikacademy.web.smartgarageti.models.User;
import com.telerikacademy.web.smartgarageti.repositories.contracts.RoleRepository;
import com.telerikacademy.web.smartgarageti.repositories.contracts.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @InjectMocks
    UserServiceImpl userService;

    @InjectMocks
    RoleServiceImpl roleService;

    @Test
    public void getUserById_ReturnsUserId_WhenIdExists() {
        // Arrange
        User mockUser = TestHelpers.createMockUser();
        Mockito.when(userRepository.getUserById(1))
                .thenReturn(mockUser);

        // Act
        User result = userService.getUserById(1);

        // Assert
        assertEquals("MockUsername", result.getUsername());
    }

    @Test
    void getRoleById_ReturnsNull_WhenIdDoesNotExist() {
        Mockito.when(roleRepository.getRoleById(1)).thenReturn(null);

        Role result = roleService.getRoleById(1);

        Assertions.assertNull(result);
    }

    @Test
    void getAllRoles_ReturnsListOfRoles() {
        List<Role> roles = Arrays.asList(TestHelpers.createMockRoleEmployee(), TestHelpers.createMockRoleUser());
        Mockito.when(roleRepository.getAllRoles()).thenReturn(roles);

        List<Role> result = roleService.getAllRoles();

        Assertions.assertEquals(2, result.size());
        Assertions. assertEquals("MockRoleEmployee", result.get(0).getName());
        Assertions. assertEquals("MockRoleUser", result.get(1).getName());
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
        Mockito.doThrow(new SecurityException(DONT_HAVE_PERMISSIONS_ONLY_EMPLOYEES_CAN_DO_THIS_OPERATION))
                .when(PermissionHelper.class);
        PermissionHelper.isEmployee(mockEmployee, DONT_HAVE_PERMISSIONS_ONLY_EMPLOYEES_CAN_DO_THIS_OPERATION);

        Assertions.assertThrows(SecurityException.class, () -> userService.getUserById(mockEmployee, 1));
    }

    @Test
    void getUserById_ReturnsNull_WhenUserIdDoesNotExist() {
        User mockEmployee = TestHelpers.createMockUserEmployee();
        Mockito.when(userRepository.getUserById(999)).thenReturn(null);

        User result = userService.getUserById(mockEmployee, 999);

        Assertions.assertNull(result);
    }
}
