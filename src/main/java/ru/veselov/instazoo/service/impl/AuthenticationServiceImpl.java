package ru.veselov.instazoo.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.veselov.instazoo.payload.request.LoginRequest;
import ru.veselov.instazoo.payload.response.AuthResponse;
import ru.veselov.instazoo.security.AuthProperties;
import ru.veselov.instazoo.security.jwt.JwtGenerator;
import ru.veselov.instazoo.service.AuthenticationService;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;

    private final JwtGenerator jwtGenerator;

    private final AuthProperties authProperties;

    @Override
    public AuthResponse authenticate(LoginRequest login) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                login.getUsername(),
                login.getPassword()
        );
        Authentication authenticate = authenticationManager.authenticate(authToken);
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        String jwt = authProperties.getPrefix() + jwtGenerator.generateToken(authenticate);
        String refreshToken = jwtGenerator.generateRefreshToken(authenticate);
        log.info("[User {}] authenticated", login.getUsername());
        return new AuthResponse(true, jwt, refreshToken);
    }

}