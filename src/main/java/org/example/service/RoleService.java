package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.model.Role;
import org.example.repository.RoleRepo;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepo roleRepo;
    public Role getUserRole() {
        return roleRepo.findByName("ROLE_USER");
    }
}
