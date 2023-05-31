package ru.veselov.instazoo.security.jwt.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import ru.veselov.instazoo.model.User;
import ru.veselov.instazoo.security.AuthProperties;
import ru.veselov.instazoo.security.jwt.JwtUtil;
import ru.veselov.instazoo.util.Constants;
import ru.veselov.instazoo.util.TestUtils;

class JwtGeneratorImplTest {

    public static final String REFRESH_CLAIM = "refresh";

    public AuthProperties authProperties;

    JwtGeneratorImpl jwtGenerator;

    User user;

    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken;

    @BeforeEach
    void init() {
        authProperties = new AuthProperties();
        authProperties.setHeader(Constants.AUTH_HEADER);
        authProperties.setPrefix(Constants.BEARER_PREFIX);
        authProperties.setSecret(Constants.SECRET);
        authProperties.setExpirationTime(Constants.EXPIRATION_TIME);
        authProperties.setRefreshExpirationTime(Constants.EXPIRATION_REFRESH_TIME);
        jwtGenerator = new JwtGeneratorImpl(authProperties);
        user = TestUtils.getUser();
        usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(user, null);
    }

    @Test
    void shouldGenerateAccessToken() {
        String jwt = jwtGenerator.generateToken(usernamePasswordAuthenticationToken);

        Claims body = Jwts.parserBuilder()
                .setSigningKey(JwtUtil.getKey(authProperties.getSecret()))
                .build().parseClaimsJws(jwt).getBody();
        Assertions.assertThat(body.getSubject()).isEqualTo(user.getId().toString());
        Assertions.assertThat(body).containsEntry("username", user.getUsername())
                .containsEntry("lastname", user.getLastname())
                .containsEntry("firstname", user.getFirstname());
        Assertions.assertThat(body.getIssuedAt()).isNotNull();
        Assertions.assertThat(body.getExpiration()).isNotNull();
        Assertions.assertThat(body.get(REFRESH_CLAIM)).isNull();
    }

    @Test
    void shouldGenerateRefreshToken() {
        String jwt = jwtGenerator.generateRefreshToken(usernamePasswordAuthenticationToken);

        Claims body = Jwts.parserBuilder()
                .setSigningKey(JwtUtil.getKey(authProperties.getSecret()))
                .build().parseClaimsJws(jwt).getBody();
        Assertions.assertThat(body.getSubject()).isEqualTo(user.getId().toString());
        Assertions.assertThat(body).containsEntry(REFRESH_CLAIM, true);
        Assertions.assertThat(body.getIssuedAt()).isNotNull();
        Assertions.assertThat(body.getExpiration()).isNotNull();
    }

}
