package ru.veselov.instazoo.security.jwt;

import org.springframework.security.core.Authentication;

public interface JwtGenerator {

    String generateToken(Authentication authentication);

    String generateRefreshToken(Authentication authentication);

    boolean isRefreshTokenExpiredSoon(String token);

}
