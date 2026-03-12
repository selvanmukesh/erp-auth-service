package com.example.erp_auth_service.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.erp_auth_service.dto.LoginRequest;
import com.example.erp_auth_service.dto.RegisterRequest;
import com.example.erp_auth_service.model.User;
import com.example.erp_auth_service.repository.UserRepository;
import com.example.erp_auth_service.util.JwtUtil;
import org.springframework.lang.NonNull;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public Page<User> getUsers(@NonNull Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public String login(LoginRequest request) throws Exception {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        System.out.println("user--->" + user.getEmail() + "-----" + user.getPassword());

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
        user.setPassword(hashedPassword);
        return userRepository.save(user);

    }

    public User getUserById(Long id) throws Exception {

        return userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public String softDeleteUser(Long id)  throws Exception{

        User user = userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setDeleted(true);
        userRepository.save(user);

        return "User deleted successfully";
    }
}
