package ru.veselov.instazoo.security.jwt.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.veselov.instazoo.security.AuthProperties;
import ru.veselov.instazoo.security.jwt.JwtParser;
import ru.veselov.instazoo.security.jwt.JwtUtil;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtParserImpl implements JwtParser {

    private final AuthProperties authProperties;

    @Override
    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(JwtUtil.getKey(authProperties.getSecret())).build()
                .parseClaimsJws(token).getBody();
        String id = claims.get("id", String.class);
        log.info("Retrieved [id {}] from jwt", id);
        return Long.parseLong(id);
    }
}
