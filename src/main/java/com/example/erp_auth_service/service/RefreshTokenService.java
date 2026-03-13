package com.example.erp_auth_service.service;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.erp_auth_service.model.RefreshToken;
import com.example.erp_auth_service.model.User;
import com.example.erp_auth_service.repository.RefreshTokenRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class RefreshTokenService {
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    public void updateRefreshToken(Long userId, String refreshToken, long refreshTokenExpiration) {

        Instant expiry = Instant.now().plusMillis(refreshTokenExpiration);

        // refreshTokenRepository.updateRefreshTokenByUserId(
        // userId,
        // refreshToken,
        // expiry);
    }

    public RefreshToken createRefreshToken(User user, String token, long expiry) {

        RefreshToken refreshToken = refreshTokenRepository
                .findByUserId(user.getId())
                .orElse(new RefreshToken());

        refreshToken.setUser(user);
        refreshToken.setToken(token);
        refreshToken.setExpiryDate(Instant.now().plusMillis(expiry));
        refreshToken.setRevoked(false);

        return refreshTokenRepository.save(refreshToken);
    }

    public void logout(HttpServletRequest request) throws Exception {

        String refreshToken = null;

        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {

                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }
System.out.println("refreshToken----------->"+refreshToken);
        if (refreshToken != null) {

            RefreshToken token = refreshTokenRepository
                    .findByToken(refreshToken)
                    .orElseThrow(() -> new RuntimeException("Token not found"));
                    System.out.println("res--->"+token.getId());

            token.setRevoked(true);

            refreshTokenRepository.save(token);
        }
    }

}
