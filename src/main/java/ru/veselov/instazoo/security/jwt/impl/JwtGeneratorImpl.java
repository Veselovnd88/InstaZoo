package ru.veselov.instazoo.security.jwt.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.veselov.instazoo.model.User;
import ru.veselov.instazoo.security.AuthProperties;
import ru.veselov.instazoo.security.jwt.JwtGenerator;
import ru.veselov.instazoo.security.jwt.JwtUtil;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtGeneratorImpl implements JwtGenerator {

    public static final String REFRESH = "refresh";

    private final AuthProperties authProperties;

    @Override
    public String generateToken(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Date now = new Date(System.currentTimeMillis());
        Date expired = new Date(now.getTime() + authProperties.getExpirationTime());
        String userId = user.getId().toString();

        Map<String, Object> claimsMap = new HashMap<>();
        claimsMap.put("id", userId);
        claimsMap.put("username", user.getUsername());
        claimsMap.put("firstname", user.getFirstname());
        claimsMap.put("lastname", user.getLastname());
        log.info("Generating access token for [user {}]", user.getUsername());
        return Jwts.builder()
                .setSubject(userId)
                .addClaims(claimsMap)
                .setIssuedAt(now)
                .setExpiration(expired)
                .signWith(JwtUtil.getKey(authProperties.getSecret()))
                .compact();
    }

    @Override
    public String generateRefreshToken(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Date now = new Date(System.currentTimeMillis());
        Date expired = new Date(now.getTime() + authProperties.getRefreshExpirationTime());
        String userId = user.getId().toString();
        Map<String, Object> claimsMap = new HashMap<>();
        claimsMap.put("id", userId);
        claimsMap.put(REFRESH, true);
        log.info("Generating refresh token for [user {}]", user.getUsername());
        return Jwts.builder()
                .setSubject(userId)
                .addClaims(claimsMap)
                .setIssuedAt(now)
                .setExpiration(expired)
                .signWith(JwtUtil.getKey(authProperties.getSecret()))
                .compact();
    }

    @Override
    public boolean isRefreshTokenExpiredSoon(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(JwtUtil.getKey(authProperties.getSecret())).build()
                .parseClaimsJws(token).getBody();
        Date expiration = claims.getExpiration();
        Instant plus = Instant.now().plus(3, ChronoUnit.HOURS);
        return expiration.toInstant().isBefore(plus);
    }
}
