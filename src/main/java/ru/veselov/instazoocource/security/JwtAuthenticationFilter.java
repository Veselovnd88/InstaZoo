package ru.veselov.instazoocource.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.veselov.instazoocource.entity.User;
import ru.veselov.instazoocource.service.CustomUserDetailsService;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    private final CustomUserDetailsService userDetailsService;

    private final SecurityProperties securityProperties;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        String jwt = getJwtFromRequest(request);
        if (StringUtils.isNotBlank(jwt) && jwtProvider.validateToken(jwt)) {
            Long userId = jwtProvider.getUserIdFromToken(jwt);
            User userDetails = userDetailsService.loadUserById(userId);

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, Collections.emptyList()
            );
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            log.info("Authentication set for [user {}]", userDetails.getName());
        } else {
            log.error("Cannot authenticate user with [{}]", jwt);
            throw new JwtException("Cannot authenticate user");
        }
        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearToken = request.getHeader(securityProperties.getHeader());
        if (StringUtils.isNotBlank(bearToken) && bearToken.startsWith(securityProperties.getPrefix())) {
            return bearToken.substring(securityProperties.getPrefix().length());
        }
        return null;
    }
}
