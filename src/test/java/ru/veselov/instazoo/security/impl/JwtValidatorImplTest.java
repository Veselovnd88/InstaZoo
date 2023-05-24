package ru.veselov.instazoo.security.impl;

import io.jsonwebtoken.ExpiredJwtException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import ru.veselov.instazoo.model.User;
import ru.veselov.instazoo.security.JwtProvider;
import ru.veselov.instazoo.security.SecurityProperties;
import ru.veselov.instazoo.util.Constants;
import ru.veselov.instazoo.util.TestUtils;

@ExtendWith(MockitoExtension.class)
class JwtValidatorImplTest {

    JwtValidatorImpl jwtValidator;

    JwtProvider jwtProvider;

    SecurityProperties securityProperties;

    User user;

    UsernamePasswordAuthenticationToken auth;

    @BeforeEach
    void init() {
        securityProperties = new SecurityProperties();
        securityProperties.setHeader(Constants.AUTH_HEADER);
        securityProperties.setPrefix(Constants.BEARER_PREFIX);
        securityProperties.setSecret(Constants.SECRET);
        securityProperties.setExpirationTime(Constants.EXPIRATION_TIME);
        securityProperties.setRefreshExpirationTime(Constants.EXPIRATION_REFRESH_TIME);
        jwtValidator = new JwtValidatorImpl(securityProperties);
        user = TestUtils.getUser();
        jwtProvider = new JwtProvider(securityProperties);
        auth = new UsernamePasswordAuthenticationToken(user, "Pass");
    }

    @Test
    void shouldValidateAccessToken() {
        String token = jwtProvider.generateToken(auth);

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
        securityProperties.setExpirationTime(1L);
        String token = jwtProvider.generateToken(auth);

        Assertions.assertThatThrownBy(() -> jwtValidator.validateAccessToken(token))
                .isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    void shouldValidateRefreshToken() {
        String token = jwtProvider.generateRefreshToken(auth);

        boolean isValid = jwtValidator.validateRefreshToken(token);

        Assertions.assertThat(isValid).isTrue();
    }

    @Test
    void shouldNotValidateRefreshToken() {
        String token = "I am not JWT, exactly";

        boolean isValid = jwtValidator.validateRefreshToken(token);

        Assertions.assertThat(isValid).isFalse();

        //Second check also for expired refresh token
        securityProperties.setRefreshExpirationTime(1L);
        token = jwtProvider.generateRefreshToken(auth);

        isValid = jwtValidator.validateRefreshToken(token);

        Assertions.assertThat(isValid).isFalse();
    }

}
