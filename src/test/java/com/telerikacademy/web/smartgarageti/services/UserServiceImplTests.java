package com.telerikacademy.web.smartgarageti.services;

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

    @Mock
    UserRepository userRepository;

    @Mock
    RoleRepository roleRepository;

    @InjectMocks
    UserServiceImpl userService;

    @InjectMocks
    RoleServiceImpl roleService;

    @Test
    public void testGetUserById() {
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
        when(roleRepository.getRoleById(1)).thenReturn(null);

        Role result = roleService.getRoleById(1);

        assertNull(result);
    }

    @Test
    void getAllRoles_ReturnsListOfRoles() {
        List<Role> roles = Arrays.asList(TestHelpers.createMockRoleEmployee(), TestHelpers.createMockRoleUser());
        when(roleRepository.getAllRoles()).thenReturn(roles);

        List<Role> result = roleService.getAllRoles();

        assertEquals(2, result.size());
        assertEquals("MockRoleEmployee", result.get(0).getName());
        assertEquals("MockRoleUser", result.get(1).getName());
    }
}
