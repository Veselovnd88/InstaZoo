package ru.veselov.instazoo.security.jwt.impl;

import io.jsonwebtoken.ExpiredJwtException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import ru.veselov.instazoo.model.User;
import ru.veselov.instazoo.security.AuthProperties;
import ru.veselov.instazoo.util.Constants;
import ru.veselov.instazoo.util.TestUtils;

@ExtendWith(MockitoExtension.class)
class JwtValidatorImplTest {

    JwtValidatorImpl jwtValidator;

    AuthProperties authProperties;

    JwtGeneratorImpl jwtGenerator;

    User user;

    UsernamePasswordAuthenticationToken auth;

    @BeforeEach
    void init() {
        authProperties = new AuthProperties();
        authProperties.setHeader(Constants.AUTH_HEADER);
        authProperties.setPrefix(Constants.BEARER_PREFIX);
        authProperties.setSecret(Constants.SECRET);
        authProperties.setExpirationTime(Constants.EXPIRATION_TIME);
        authProperties.setRefreshExpirationTime(Constants.EXPIRATION_REFRESH_TIME);
        jwtValidator = new JwtValidatorImpl(authProperties);
        user = TestUtils.getUser();
        jwtGenerator = new JwtGeneratorImpl(authProperties);
        auth = new UsernamePasswordAuthenticationToken(user, "Pass");
    }

    @Test
    void shouldValidateAccessToken() {
        String token = jwtGenerator.generateToken(auth);

        boolean isValid = jwtValidator.validateAccessToken(token);

        Assertions.assertThat(isValid).isTrue();
    }

    @Test
    void shouldNotValidateAccessToken() {
        String token = "I am not Jwt, exactly";

        boolean isValid = jwtValidator.validateAccessToken(token);

        Assertions.assertThat(isValid).isFalse();
    }

    @Test
    void shouldThrowExpiredJwtException() {
        authProperties.setExpirationTime(1L);
        String token = jwtGenerator.generateToken(auth);

        Assertions.assertThatThrownBy(() -> jwtValidator.validateAccessToken(token))
                .isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    void shouldValidateRefreshToken() {
        String token = jwtGenerator.generateRefreshToken(auth);

        boolean isValid = jwtValidator.validateRefreshToken(token);

        Assertions.assertThat(isValid).isTrue();
    }

    @Test
    void shouldNotValidateRefreshToken() {
        String token = "I am not JWT, exactly";

        boolean isValid = jwtValidator.validateRefreshToken(token);

        Assertions.assertThat(isValid).isFalse();

        //Second check also for expired refresh token
        authProperties.setRefreshExpirationTime(1L);
        token = jwtGenerator.generateRefreshToken(auth);

        isValid = jwtValidator.validateRefreshToken(token);

        Assertions.assertThat(isValid).isFalse();
    }

}
