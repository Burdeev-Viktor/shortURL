package org.example.service;

import org.apache.log4j.Logger;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private static final Logger log = Logger.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
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
}

