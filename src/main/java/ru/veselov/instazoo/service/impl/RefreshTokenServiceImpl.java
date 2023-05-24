package ru.veselov.instazoo.service.impl;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import ru.veselov.instazoo.exception.BadTokenException;
import ru.veselov.instazoo.model.User;
import ru.veselov.instazoo.payload.response.AuthResponse;
import ru.veselov.instazoo.security.JwtProvider;
import ru.veselov.instazoo.security.SecurityProperties;
import ru.veselov.instazoo.security.TokenType;
import ru.veselov.instazoo.service.CustomUserDetailsService;
import ru.veselov.instazoo.service.RefreshTokenService;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final JwtProvider jwtProvider;

    private final CustomUserDetailsService userDetailsService;

    private final SecurityProperties securityProperties;

    @Override
    public AuthResponse processRefreshToken(String refreshToken) {
        try {
            if (jwtProvider.validateToken(refreshToken, TokenType.REFRESH)) {
                Long userId = jwtProvider.getUserIdFromToken(refreshToken);
                User user = userDetailsService.loadUserById(userId);
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        user, null, Collections.emptyList()
                );
                String jwt = securityProperties.getPrefix() + jwtProvider.generateToken(authenticationToken);
                if (jwtProvider.isRefreshTokenExpiredSoon(refreshToken)) {
                    refreshToken = jwtProvider.generateRefreshToken(authenticationToken);
                }
                return new AuthResponse(true, jwt, refreshToken);
            } else {
                throw new BadTokenException("Refresh token is invalid, please re-login");
            }
        } catch (ExpiredJwtException e) {
            throw new BadTokenException("Refresh token is invalid, please re-login");
        }
    }

}