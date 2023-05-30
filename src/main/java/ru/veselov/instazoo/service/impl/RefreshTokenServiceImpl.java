package ru.veselov.instazoo.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import ru.veselov.instazoo.exception.BadTokenException;
import ru.veselov.instazoo.model.User;
import ru.veselov.instazoo.payload.response.AuthResponse;
import ru.veselov.instazoo.security.JwtProvider;
import ru.veselov.instazoo.security.JwtValidator;
import ru.veselov.instazoo.security.AuthProperties;
import ru.veselov.instazoo.service.CustomUserDetailsService;
import ru.veselov.instazoo.service.RefreshTokenService;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final JwtProvider jwtProvider;

    private final JwtValidator jwtValidator;

    private final CustomUserDetailsService userDetailsService;

    private final AuthProperties authProperties;

    @Override
    public AuthResponse processRefreshToken(String refreshToken) {
        if (jwtValidator.validateRefreshToken(refreshToken)) {
            log.info("Refresh token validated");
            Long userId = jwtProvider.getUserIdFromToken(refreshToken);
            User user = userDetailsService.loadUserById(userId);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    user, null, Collections.emptyList()
            );
            String jwt = authProperties.getPrefix() + jwtProvider.generateToken(authenticationToken);
            if (jwtProvider.isRefreshTokenExpiredSoon(refreshToken)) {
                refreshToken = jwtProvider.generateRefreshToken(authenticationToken);
                log.info("Refresh token expired after 3 hours, replaced to the new one");
            }
            return new AuthResponse(true, jwt, refreshToken);
        } else {
            log.error("Invalid refresh token");
            throw new BadTokenException("Refresh token is invalid, please re-login");
        }
    }

}
