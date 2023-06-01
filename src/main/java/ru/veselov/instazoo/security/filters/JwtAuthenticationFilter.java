package ru.veselov.instazoo.security.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.veselov.instazoo.exception.error.ErrorConstants;
import ru.veselov.instazoo.exception.error.JwtErrorResponse;
import ru.veselov.instazoo.security.AuthProperties;
import ru.veselov.instazoo.security.SecurityConstants;
import ru.veselov.instazoo.security.authentication.JwtAuthenticationToken;
import ru.veselov.instazoo.security.jwt.JwtValidator;
import ru.veselov.instazoo.security.managers.JwtAuthenticationManager;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtAuthenticationManager authenticationManager;

    private final JwtValidator jwtValidator;

    private final AuthProperties authProperties;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        Optional<String> jwtOpt = getJwtFromRequest(request);
        if (jwtOpt.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }
        String jwt = jwtOpt.get();
        try {
            if (StringUtils.isNotBlank(jwt) && jwtValidator.validateAccessToken(jwt)) {
                JwtAuthenticationToken token = new JwtAuthenticationToken(jwt);
                token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                Authentication authenticationToken = authenticationManager.authenticate(token);
                if (authenticationToken.isAuthenticated()) {
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    log.info("Authentication set for [request {}]", request.getRemoteAddr());
                }
            } else {
                log.error("Cannot authenticate user with [{}]", jwt);
            }
        } catch (ExpiredJwtException exception) {
            log.error("Jwt is expired");
            sendErrorResponse(response, exception);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private Optional<String> getJwtFromRequest(HttpServletRequest request) {
        String bearToken = request.getHeader(authProperties.getHeader());
        if (StringUtils.isNotBlank(bearToken) && bearToken.startsWith(authProperties.getPrefix())) {
            return Optional.of(bearToken.substring(authProperties.getPrefix().length()));
        }
        return Optional.empty();
    }

    private void sendErrorResponse(HttpServletResponse response, ExpiredJwtException e) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JwtErrorResponse jwtErrorResponse = new JwtErrorResponse(
                ErrorConstants.JWT_EXPIRED,
                e.getMessage(),
                authProperties.getSigninUrl(),
                authProperties.getRefreshTokenUrl());
        String errorMessage = objectMapper.writeValueAsString(jwtErrorResponse);
        response.setContentType(SecurityConstants.CONTENT_TYPE);
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.getWriter().println(errorMessage);
    }

}
