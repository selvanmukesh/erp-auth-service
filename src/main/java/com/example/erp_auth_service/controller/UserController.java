package com.example.erp_auth_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.erp_auth_service.util.JwtUtil;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/auth")
public class UserController {
    @Autowired
    JwtUtil jwtUtil;

    // @PreAuthorize("hasRole('ADMIN')")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/callMe")
    public String getMethodName() {
        return "hello";
    }

    @PostMapping("/login")
    public String login() {
        String[] roles = new String[] { "ADMIN" };
        String token = jwtUtil.generateToken("mukesh", "mukeshselvan0861@gmail.com", roles);
        String userName = jwtUtil.extractUsername(token);
        String[] rolesAll = jwtUtil.extractRoles(token);
        System.out.println("log--------" + Arrays.toString(rolesAll) + "-----" + jwtUtil.validateToken(token));
        System.out.println("userName----->" + userName);
        return token;
    }

    @PostMapping("/register")
    public String register() {
        return "success";
    }

}
