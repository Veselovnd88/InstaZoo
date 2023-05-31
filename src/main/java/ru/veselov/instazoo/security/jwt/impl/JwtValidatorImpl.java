package ru.veselov.instazoo.security.jwt.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.DecodingException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import ru.veselov.instazoo.security.jwt.JwtUtil;
import ru.veselov.instazoo.security.jwt.JwtValidator;
import ru.veselov.instazoo.security.AuthProperties;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtValidatorImpl implements JwtValidator {

    public static final String ERROR_MESSAGE = "Error occurred during parsing access token [{}]";

    public static final String REFRESH = "refresh";

    private final AuthProperties authProperties;

    @Override
    public boolean validateAccessToken(String token) {
        try {
            Claims body = Jwts.parserBuilder()
                    .setSigningKey(JwtUtil.getKey(authProperties.getSecret()))
                    .build().parseClaimsJws(token).getBody();
            log.info("Access token validated");
            return ObjectUtils.isEmpty(body.get(REFRESH));
        } catch (SignatureException |
                 MalformedJwtException |
                 UnsupportedJwtException |
                 DecodingException |
                 IllegalArgumentException exception) {
            //ExpiredJwt can be thrown to process Error
            log.error(ERROR_MESSAGE, exception.getMessage());
            return false;
        }
    }

    @Override
    public boolean validateRefreshToken(String token) {
        try {
            Claims body = Jwts.parserBuilder()
                    .setSigningKey(JwtUtil.getKey(authProperties.getSecret()))
                    .build().parseClaimsJws(token).getBody();
            log.info("Refresh token validated");
            return !ObjectUtils.isEmpty(body.get(REFRESH));
        } catch (JwtException | IllegalArgumentException exception) {
            log.error(ERROR_MESSAGE, exception.getMessage());
            return false;
        }
    }

}
