package org.example.service;

import org.apache.log4j.Logger;
import org.example.dtos.user.RegistrationUserDto;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {
    private static final Logger log = Logger.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    public UserService(UserRepository userRepository, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }
    public void save(User user) {
        if(userRepository.findUserByLogin(user.getLogin()) == null)
        {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
            log.info("user saved" + user);
        }
    }
    public void delete(User user) {
        userRepository.delete(user);
    }
    public User findUserByLogin(String login) {
        return userRepository.findUserByLogin(login);
    }
    public boolean userIsExistsByLoginAndPassword(User user) {
        return userRepository.existsByLoginAndPassword(user.getLogin(), passwordEncoder.encode(user.getPassword()));
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findUserByLogin(username);
        return new org.springframework.security.core.userdetails.User(
                user.getLogin(),
                user.getPassword(),
                user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList())
        ); }


    public User createNewUser(RegistrationUserDto registrationUserDto) {
        User user = new User();
        user.setLogin(registrationUserDto.getUsername());
        user.setPassword(passwordEncoder.encode(registrationUserDto.getPassword()));
        user.setRoles(List.of(roleService.getUserRole()));
        user = userRepository.save(user);
        log.info("user saved" + user);
        return user;
    }

}

