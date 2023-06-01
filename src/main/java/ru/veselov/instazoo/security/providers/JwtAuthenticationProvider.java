package ru.veselov.instazoo.security.providers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import ru.veselov.instazoo.model.User;
import ru.veselov.instazoo.security.authentication.JwtAuthenticationToken;
import ru.veselov.instazoo.security.jwt.JwtParser;
import ru.veselov.instazoo.security.jwt.JwtValidator;
import ru.veselov.instazoo.service.CustomUserDetailsService;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final CustomUserDetailsService userDetailsService;

    private final JwtParser jwtParser;

    private final JwtValidator jwtValidator;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        JwtAuthenticationToken authToken = (JwtAuthenticationToken) authentication;
        String jwt = authToken.getJwt();
        if (jwtValidator.validateAccessToken(jwt)) {
            Long userId = jwtParser.getUserIdFromToken(jwt);
            User user = userDetailsService.loadUserById(userId);
            authToken.setPrincipal(user);
            authToken.setAuthenticated(true);
            JwtAuthenticationToken authenticatedToken =
                    new JwtAuthenticationToken(user.getAuthorities(), user, true, jwt);
            authenticatedToken.setDetails(authToken.getDetails());
            log.info("[User {} authenticated]", user.getUsername());
            return authenticatedToken;
        }
        return authentication;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.equals(authentication);
    }

}
