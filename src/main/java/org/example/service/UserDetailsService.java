package org.example.service;

import org.apache.log4j.Logger;
import org.example.model.MyUserDetails;
import org.example.model.User;
import org.example.repository.RoleRepo;
import org.example.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;


public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {
    private static final Logger log = Logger.getLogger(UserDetailsService.class);

    private final UserRepository userRepository;
    private final RoleRepo roleRepo;

    public UserDetailsService(UserRepository userRepository, RoleRepo roleRepo) {
        this.userRepository = userRepository;
        this.roleRepo = roleRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        User user = userRepository.findUserByLogin(username);
        if (user == null) {
            log.warn("Could not find user");
            throw new UsernameNotFoundException("Could not find user");
        }
        return new org.springframework.security.core.userdetails.User(
                user.getLogin(),
                user.getPassword(),
                user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList())
        );
    }
}
