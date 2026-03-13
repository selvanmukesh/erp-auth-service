package com.example.erp_auth_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.erp_auth_service.common.ApiResponse;
import com.example.erp_auth_service.dto.LoginRequest;
import com.example.erp_auth_service.dto.RegisterRequest;
import com.example.erp_auth_service.dto.RegisterResponse;
import com.example.erp_auth_service.model.User;
import com.example.erp_auth_service.service.RefreshTokenService;
import com.example.erp_auth_service.service.UserService;
import com.example.erp_auth_service.util.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/auth")
public class UserController {
    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    UserService userService;

    @Autowired
    RefreshTokenService refreshTokenService;

    // @PreAuthorize("hasRole('ADMIN')")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<User>>> getUsers(@NonNull Pageable pageable) {
        try {

            ApiResponse<Page<User>> apiResponse = new ApiResponse<Page<User>>(userService.getUsers(pageable), "Success",
                    HttpStatus.OK.value(),
                    null);
            return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
        } catch (Exception e) {
            ApiResponse<Page<User>> apiResponse = new ApiResponse<>(null, ".Failed",
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    e.getMessage());

            return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {
        try {
            String token = userService.login(request, response);
            ApiResponse<String> apiResponse = new ApiResponse<String>(token, "Login Success", HttpStatus.OK.value(),
                    null);
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
            System.out.println("receieved---------->");
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

    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable Long id) {

        try {

            ApiResponse<User> apiResponse = new ApiResponse<>(userService.getUserById(id),
                    "Success",
                    HttpStatus.OK.value(),
                    null);

            return new ResponseEntity<>(apiResponse, HttpStatus.OK);

        } catch (Exception e) {

            ApiResponse<User> apiResponse = new ApiResponse<>(null,
                    "Failed",
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    e.getMessage());

            return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse<String>> softDeleteUser(@PathVariable Long id) {

        try {

            String message = userService.softDeleteUser(id);

            ApiResponse<String> apiResponse = new ApiResponse<>(message,
                    "Success",
                    HttpStatus.OK.value(),
                    null);

            return new ResponseEntity<>(apiResponse, HttpStatus.OK);

        } catch (Exception e) {

            ApiResponse<String> apiResponse = new ApiResponse<>(null,
                    "Failed",
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    e.getMessage());

            return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(HttpServletRequest request) {

        try {

            refreshTokenService.logout(request);
            ApiResponse<String> apiResponse = new ApiResponse<>("Logout successful",
                    "Success",
                    HttpStatus.OK.value(),
                    null);

            return new ResponseEntity<>(apiResponse, HttpStatus.OK);

        } catch (Exception e) {

            ApiResponse<String> apiResponse = new ApiResponse<>(null,
                    "Logout Failed",
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    e.getMessage());

            return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
