package ru.veselov.instazoo.security;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import ru.veselov.instazoo.model.User;
import ru.veselov.instazoo.service.CustomUserDetailsService;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    private final CustomUserDetailsService userDetailsService;

    private final JwtValidator jwtValidator;

    private final AuthProperties authProperties;

    @Qualifier("handlerExceptionResolver")
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Autowired
    public JwtAuthenticationFilter(JwtProvider jwtProvider,
                                   JwtValidator jwtValidator,
                                   CustomUserDetailsService userDetailsService,
                                   AuthProperties authProperties,
                                   HandlerExceptionResolver handlerExceptionResolver) {
        this.jwtProvider = jwtProvider;
        this.userDetailsService = userDetailsService;
        this.authProperties = authProperties;
        this.handlerExceptionResolver = handlerExceptionResolver;
        this.jwtValidator = jwtValidator;
    }

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
                Long userId = jwtProvider.getUserIdFromToken(jwt);
                User user = userDetailsService.loadUserById(userId);
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        user, null, Collections.emptyList()
                );
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                log.info("Authentication set for [user {}]", user.getUsername());
            } else {
                log.error("Cannot authenticate user with [{}]", jwt);
            }
        } catch (ExpiredJwtException e) {
            handlerExceptionResolver.resolveException(request, response, null, e);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private Optional<String> getJwtFromRequest(HttpServletRequest request) {
        String bearToken = request.getHeader(authProperties.getHeader());
        if (StringUtils.isNotBlank(bearToken) && bearToken.startsWith(authProperties.getPrefix())) {
            return
                    Optional.of(bearToken.substring(authProperties.getPrefix().length()));
        }
        return Optional.empty();
    }

}
