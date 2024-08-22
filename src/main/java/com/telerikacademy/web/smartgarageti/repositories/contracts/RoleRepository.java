package com.telerikacademy.web.smartgarageti.repositories.contracts;

import com.telerikacademy.web.smartgarageti.models.Role;

import java.util.List;

public interface RoleRepository {
    List<Role> getAllRoles();

    Role getRoleById(int id);
}
