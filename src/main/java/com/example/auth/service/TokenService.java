package com.example.auth.service;

import com.example.auth.config.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtProvider jwtProvider;

    public String generateAccessToken(String email, String role) {
        return jwtProvider.generateAccessToken(email, role);
    }

    public String generateRefreshToken(String email, String role) {
        return jwtProvider.generateRefreshToken(email, role);
    }

    public boolean validateToken(String token) {
        return jwtProvider.validateToken(token);
    }

    public String getEmailFromToken(String token) {
        return jwtProvider.getEmailFromToken(token);
    }

    public String getRoleFromToken(String token) {
        return jwtProvider.getRoleFromToken(token);
    }
}
