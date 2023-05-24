package ru.veselov.instazoo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import ru.veselov.instazoo.model.User;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class
JwtProvider {

    public static final String REFRESH = "refresh";

    private final SecurityProperties securityProperties;

    public String generateToken(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Date now = new Date(System.currentTimeMillis());
        Date expired = new Date(now.getTime() + securityProperties.getExpirationTime());
        String userId = user.getId().toString();

        Map<String, Object> claimsMap = new HashMap<>();
        claimsMap.put("id", userId);
        claimsMap.put("username", user.getUsername());
        claimsMap.put("firstname", user.getFirstname());
        claimsMap.put("lastname", user.getLastname());
        return Jwts.builder()
                .setSubject(userId)
                .addClaims(claimsMap)
                .setIssuedAt(now)
                .setExpiration(expired)
                .signWith(JwtUtil.getKey(securityProperties.getSecret()))
                .compact();
    }

    public String generateRefreshToken(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Date now = new Date(System.currentTimeMillis());
        Date expired = new Date(now.getTime() + securityProperties.getRefreshExpirationTime());
        String userId = user.getId().toString();
        Map<String, Object> claimsMap = new HashMap<>();
        claimsMap.put("id", userId);
        claimsMap.put(REFRESH, true);
        return Jwts.builder()
                .setSubject(userId)
                .addClaims(claimsMap)
                .setIssuedAt(now)
                .setExpiration(expired)
                .signWith(JwtUtil.getKey(securityProperties.getSecret()))
                .compact();
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(JwtUtil.getKey(securityProperties.getSecret())).build()
                .parseClaimsJws(token).getBody();
        String id = claims.get("id", String.class);
        log.info("Retrieved [id {}] from jwt", id);
        return Long.parseLong(id);
    }

    public boolean isRefreshTokenExpiredSoon(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(JwtUtil.getKey(securityProperties.getSecret())).build()
                .parseClaimsJws(token).getBody();
        Date expiration = claims.getExpiration();
        Instant plus = Instant.now().plus(3, ChronoUnit.HOURS);
        return expiration.toInstant().isBefore(plus);
    }

}
