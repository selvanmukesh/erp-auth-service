package com.example.erp_auth_service.service;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.erp_auth_service.dto.LoginRequest;
import com.example.erp_auth_service.dto.RegisterRequest;
import com.example.erp_auth_service.model.RefreshToken;
import com.example.erp_auth_service.model.User;
import com.example.erp_auth_service.repository.UserRepository;
import com.example.erp_auth_service.util.JwtUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.lang.NonNull;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${jwt.access-token.expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;

    public Page<User> getUsers(@NonNull Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public String login(LoginRequest request, HttpServletResponse response) throws Exception {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        System.out.println("user--->" + user.getEmail() + "-----" + user.getPassword());

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }
        String[] roles = new String[] { "ADMIN" };
        String accessToken = jwtUtil.generateToken(user.getName(), user.getEmail(), roles, accessTokenExpiration);
        String refreshToken = jwtUtil.generateToken(user.getName(), user.getEmail(), roles, refreshTokenExpiration);
        refreshTokenService.createRefreshToken(user, refreshToken, refreshTokenExpiration);

        // Store refresh token in cookie
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge((int) (refreshTokenExpiration / 1000));

        response.addCookie(cookie);

        refreshTokenService.updateRefreshToken(user.getId(), refreshToken, refreshTokenExpiration);

        return accessToken;
    }

    public User saveUser(RegisterRequest request) throws Exception {

        // hash password
        String hashedPassword = passwordEncoder.encode(request.getPassword());
        request.setPassword(hashedPassword);
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(hashedPassword);
        // create refresh token entry
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(null);
        refreshToken.setExpiryDate(null);
        refreshToken.setRevoked(false);
        refreshToken.setUser(user);

        // refreshTokenRepository.save(refreshToken);
        return userRepository.save(user);

    }

    public User getUserById(Long id) throws Exception {

        return userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public String softDeleteUser(Long id) throws Exception {

        User user = userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setDeleted(true);
        userRepository.save(user);

        return "User deleted successfully";
    }
    

    
}
