package com.example.erp_auth_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.erp_auth_service.common.ApiResponse;
import com.example.erp_auth_service.dto.LoginRequest;
import com.example.erp_auth_service.dto.RegisterRequest;
import com.example.erp_auth_service.dto.RegisterResponse;
import com.example.erp_auth_service.model.User;
import com.example.erp_auth_service.service.UserService;
import com.example.erp_auth_service.util.JwtUtil;

import jakarta.validation.Valid;

import java.util.Arrays;

import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/auth")
public class UserController {
    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    UserService userService;

    // @PreAuthorize("hasRole('ADMIN')")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/callMe")
    public String getMethodName() {
        return "hello";
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@Valid @RequestBody LoginRequest request) {
        try {
            String token = userService.login(request);
            ApiResponse<String> apiResponse = new ApiResponse<String>(token, "Login Success", HttpStatus.OK.value(),
                    getMethodName());
            return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
        } catch (Exception e) {

            ApiResponse<String> apiResponse = new ApiResponse<>(null, "Login Failed",
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    e.getMessage());

            return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(@Valid @RequestBody RegisterRequest request) {
        try {
            User user = userService.saveUser(request);
            RegisterResponse registerResponse = new RegisterResponse(user.getName(), user.getEmail());
            ApiResponse<RegisterResponse> apiResponse = new ApiResponse<>(registerResponse, "Created Successfully",
                    HttpStatus.CREATED.value(),
                    null);

            return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);

        } catch (Exception e) {

            ApiResponse<RegisterResponse> apiResponse = new ApiResponse<>(null, "User creation failed",
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    e.getMessage());

            return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
