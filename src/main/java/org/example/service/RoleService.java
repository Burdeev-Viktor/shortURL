package org.example.service;

import org.example.model.Role;
import org.example.repository.RoleRepo;
import org.springframework.stereotype.Service;

@Service
public class RoleService {
    private final RoleRepo roleRepo;

    public RoleService(RoleRepo roleRepo) {
        this.roleRepo = roleRepo;
    }

    public Role getUserRole() {
        return roleRepo.findByName("ROLE_USER");
    }
}
