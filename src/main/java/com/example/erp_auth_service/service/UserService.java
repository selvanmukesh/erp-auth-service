package com.example.erp_auth_service.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.erp_auth_service.dto.LoginRequest;
import com.example.erp_auth_service.dto.RegisterRequest;
import com.example.erp_auth_service.model.User;
import com.example.erp_auth_service.repository.UserRepository;
import com.example.erp_auth_service.util.JwtUtil;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public String login(LoginRequest request) throws Exception {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }
        String[] roles = new String[] { "ADMIN" };
        String token = jwtUtil.generateToken(user.getName(), user.getEmail(), roles);
        return token;
    }

    public User saveUser(RegisterRequest request) throws Exception {

        // hash password
        String hashedPassword = passwordEncoder.encode(request.getPassword());
        request.setPassword(hashedPassword);
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        return userRepository.save(user);

    }
}
