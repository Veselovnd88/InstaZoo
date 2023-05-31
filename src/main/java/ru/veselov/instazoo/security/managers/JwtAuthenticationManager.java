package ru.veselov.instazoo.security.managers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import ru.veselov.instazoo.security.providers.JwtAuthenticationProvider;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationManager implements AuthenticationManager {

    private final JwtAuthenticationProvider jwtAuthenticationProvider;

    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (jwtAuthenticationProvider.supports(authentication.getClass())) {
            log.info("Processing authentication with JwtAuthenticationProvider");
            return jwtAuthenticationProvider.authenticate(authentication);
        }
        throw new BadCredentialsException("Invalid Jwt token");
    }
}
