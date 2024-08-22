package com.telerikacademy.web.smartgarageti.services;

import com.telerikacademy.web.smartgarageti.models.Role;
import com.telerikacademy.web.smartgarageti.repositories.contracts.RoleRepository;
import com.telerikacademy.web.smartgarageti.services.contracts.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
@Autowired
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.getAllRoles();
    }

    @Override
    public Role getRoleById(int id) {
        return roleRepository.getRoleById(id);
    }
}
