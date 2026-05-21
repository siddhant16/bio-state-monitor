package com.biostate.monitor.service;

import com.biostate.monitor.model.User;
import com.biostate.monitor.repository.UserRepository;
import com.biostate.monitor.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(String username, String email, String password) {
        if (username == null || email == null || password == null) {
            throw new RuntimeException("Username, email, and password are required");
        }

        username = username.trim();
        email = email.trim();

        if (!ValidationUtil.isValidUsername(username)) {
            throw new RuntimeException("Username must be 3-30 characters and contain only letters, numbers, or underscores");
        }
        if (!ValidationUtil.isValidEmail(email)) {
            throw new RuntimeException("A valid email address is required");
        }
        if (!ValidationUtil.isValidPassword(password)) {
            throw new RuntimeException("Password must be at least 8 characters and include uppercase, lowercase, number, and special character");
        }
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }
        User user = new User(username, email, passwordEncoder.encode(password));
        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}