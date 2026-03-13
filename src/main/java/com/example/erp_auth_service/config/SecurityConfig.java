package com.example.erp_auth_service.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.erp_auth_service.filter.JwtFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            // enable cors
            .cors(cors -> {})

            .authorizeHttpRequests(auth -> auth

                    // allow preflight requests
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                    // public APIs
                    .requestMatchers("/auth/login", "/auth/register").permitAll()

                    // other APIs need authentication
                    .anyRequest().authenticated())

            // add jwt filter
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
