package ru.veselov.instazoocource.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import ru.veselov.instazoocource.entity.User;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtProvider {

    private final JwtProperties jwtProperties;

    public String generateToken(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Date now = new Date(System.currentTimeMillis());
        Date expired = new Date(now.getTime() + jwtProperties.getExpirationTime());
        String userId = user.getId().toString();

        Map<String, Object> claimsMap = new HashMap<>();
        claimsMap.put("id", userId);
        claimsMap.put("username", user.getUsername());
        claimsMap.put("firstname", user.getName());
        claimsMap.put("lastname", user.getLastname());
        return Jwts.builder()
                .setSubject(userId)
                .addClaims(claimsMap)
                .setIssuedAt(now)
                .setExpiration(expired)
                .signWith(getKey(jwtProperties.getSecret()))
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getKey(jwtProperties.getSecret())).build()
                    .parse(token);
            log.info("Jwt successfully parsed");
            return true;
        } catch (SignatureException |
                 MalformedJwtException |
                 ExpiredJwtException |
                 UnsupportedJwtException |
                 IllegalArgumentException exception) {
            log.error("Error occurred [{}]", exception.getMessage());
            return false;
        }
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(getKey(jwtProperties.getSecret())).build()
                .parseClaimsJws(token).getBody();
        String id = claims.get("id", String.class);
        log.info("Retrieved [id {}] from jwt", id);
        return Long.parseLong(id);
    }

    private Key getKey(String secret) {
        //converting key from application.yml
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
