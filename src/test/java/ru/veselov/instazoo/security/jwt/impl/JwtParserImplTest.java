package ru.veselov.instazoo.security.jwt.impl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import ru.veselov.instazoo.model.User;
import ru.veselov.instazoo.security.AuthProperties;
import ru.veselov.instazoo.util.Constants;
import ru.veselov.instazoo.util.TestUtils;

class JwtParserImplTest {

    public AuthProperties authProperties;

    JwtGeneratorImpl jwtGenerator;

    JwtParserImpl jwtParser;
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
        jwtParser = new JwtParserImpl(authProperties);
    }

    @Test
    void shouldReturnUserId() {
        String jwt = jwtGenerator.generateToken(usernamePasswordAuthenticationToken);

        Long userIdFromToken = jwtParser.getUserIdFromToken(jwt);

        Assertions.assertThat(userIdFromToken).isEqualTo(user.getId());
    }

}