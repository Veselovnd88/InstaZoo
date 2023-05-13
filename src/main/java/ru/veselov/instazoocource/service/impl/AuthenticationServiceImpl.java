package ru.veselov.instazoocource.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.veselov.instazoocource.payload.request.LoginRequest;
import ru.veselov.instazoocource.payload.response.AuthResponseDTO;
import ru.veselov.instazoocource.security.JwtProvider;
import ru.veselov.instazoocource.security.SecurityProperties;
import ru.veselov.instazoocource.service.AuthenticationService;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;

    private final JwtProvider jwtProvider;

    private final SecurityProperties securityProperties;

    @Override
    public AuthResponseDTO authenticate(LoginRequest login) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                login.getUsername(),
                login.getPassword()
        );
        Authentication authenticate = authenticationManager.authenticate(authToken);
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        String jwt = securityProperties.getPrefix() + jwtProvider.generateToken(authenticate);
        log.info("[User {}] authenticated", login.getUsername());
        return new AuthResponseDTO(true, jwt);
    }

}
